/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.psd2.consent.service;

import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisCommonPaymentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.consent.api.pis.proto.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentRequest;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisPaymentInfo;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentService;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.repository.PisAuthorizationRepository;
import de.adorsys.psd2.consent.repository.PisCommonPaymentDataRepository;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.consent.service.mapper.PisCommonPaymentMapper;
import de.adorsys.psd2.consent.service.mapper.PsuDataMapper;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;
import static de.adorsys.psd2.xs2a.core.sca.ScaStatus.SCAMETHODSELECTED;
import static de.adorsys.psd2.xs2a.core.sca.ScaStatus.STARTED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PisCommonPaymentServiceInternal implements PisCommonPaymentService {
    //
    private final PisCommonPaymentMapper pisCommonPaymentMapper;
    private final PsuDataMapper psuDataMapper;
    private final PisAuthorizationRepository pisAuthorizationRepository;
    private final PisPaymentDataRepository pisPaymentDataRepository;
    private final PisCommonPaymentDataRepository pisCommonPaymentDataRepository;
    private final SecurityDataService securityDataService;
    private final AspspProfileService aspspProfileService;

    /**
     * Creates new pis common payment with full information about payment
     *
     * @param request Consists information about payments.
     * @return Response containing identifier of common payment
     */
    @Override
    @Transactional
    public Optional<CreatePisCommonPaymentResponse> createCommonPayment(PisPaymentInfo request) {
        PisCommonPaymentData commonPaymentData = pisCommonPaymentMapper.mapToPisCommonPaymentData(request);
        String externalId = UUID.randomUUID().toString();
        commonPaymentData.setPaymentId(externalId);

        PisCommonPaymentData saved = pisCommonPaymentDataRepository.save(commonPaymentData);

        return Optional.ofNullable(saved.getId())
                   .flatMap(id -> securityDataService.encryptId(saved.getPaymentId()))
                   .map(CreatePisCommonPaymentResponse::new);
    }

    /**
     * Retrieves common payment status from pis common payment by payment identifier
     *
     * @param paymentId String representation of pis payment identifier
     * @return Information about the status of a common payment
     */
    @Override
    public Optional<TransactionStatus> getPisCommonPaymentStatusById(String paymentId) {
        return getPisCommonPaymentById(paymentId)
                   .map(PisCommonPaymentData::getTransactionStatus);
    }

    /**
     * Reads full information of pis common payment by payment identifier
     *
     * @param paymentId String representation of pis encrypted payment identifier
     * @return Response containing full information about pis common payment
     */
    @Override
    public Optional<PisCommonPaymentResponse> getCommonPaymentById(String paymentId) {
        return getPisCommonPaymentById(paymentId)
                   .flatMap(pisCommonPaymentMapper::mapToPisCommonPaymentResponse);
    }

    /**
     * Updates pis common payment status by payment identifier
     *
     * @param paymentId String representation of pis encrypted payment identifier
     * @param status             new common payment status
     * @return Response containing result of status changing
     */
    @Override
    @Transactional
    public Optional<Boolean> updateCommonPaymentStatusById(String paymentId, TransactionStatus status) {
        return getActualPisCommonPayment(paymentId)
                   .map(pmt -> setStatusAndSaveCommonPaymentData(pmt, status))
                   .map(con -> con.getTransactionStatus() == status);
    }

    /**
     * Get original decrypted Id from encrypted string
     *
     * @param encryptedId id to be decrypted
     * @return Response containing original decrypted Id
     */
    @Override
    public Optional<String> getDecryptedId(String encryptedId) {
        return securityDataService.decryptId(encryptedId);
    }

    /**
     * Create common payment authorization
     *
     * @param encryptedPaymentId encrypted id of the payment
     * @param authorizationType  type of authorization required to create. Can be  CREATED or CANCELLED
     * @return response contains authorization id
     */
    @Override
    @Transactional
    public Optional<CreatePisAuthorisationResponse> createAuthorization(String encryptedPaymentId, CmsAuthorisationType authorizationType,
                                                                        PsuIdData psuData) {
        Optional<String> paymentId = securityDataService.decryptId(encryptedPaymentId);
        if (!paymentId.isPresent()) {
            log.warn("Payment Id has not encrypted: {}", encryptedPaymentId);
            return Optional.empty();
        }

        return readReceivedConsentByPaymentId(paymentId.get())
                   .map(pisConsent -> saveNewAuthorisation(pisConsent, authorizationType, psuData))
                   .map(c -> new CreatePisAuthorisationResponse(c.getExternalId()));
    }

    @Override
    @Transactional
    public Optional<CreatePisAuthorisationResponse> createAuthorizationCancellation(String paymentId, CmsAuthorisationType authorizationType, PsuIdData psuData) {
        return createAuthorization(paymentId, authorizationType, psuData);
    }

    /**
     * Update common payment authorisation
     *
     * @param authorizationId id of the authorisation to be updated
     * @param request         contains data for updating authorisation
     * @return response contains updated data
     */
    @Override
    @Transactional
    public Optional<UpdatePisCommonPaymentPsuDataResponse> updateCommonPaymentAuthorisation(String authorizationId, UpdatePisCommonPaymentPsuDataRequest request) {
        Optional<PisAuthorization> pisConsentAuthorisationOptional = pisAuthorizationRepository.findByExternalIdAndAuthorizationType(
            authorizationId, CmsAuthorisationType.CREATED);

        if (pisConsentAuthorisationOptional.isPresent()) {
            ScaStatus scaStatus = doUpdateConsentAuthorisation(request, pisConsentAuthorisationOptional.get());
            return Optional.of(new UpdatePisCommonPaymentPsuDataResponse(scaStatus));
        }

        return Optional.empty();
    }

    /**
     * Update common payment cancellation authorisation
     *
     * @param cancellationId id of the authorisation to be updated
     * @param request        contains data for updating authorisation
     * @return response contains updated data
     */
    @Override
    @Transactional
    public Optional<UpdatePisCommonPaymentPsuDataResponse> updateCommonPaymentCancellationAuthorisation(String cancellationId, UpdatePisCommonPaymentPsuDataRequest request) {
        Optional<PisAuthorization> pisConsentAuthorisationOptional = pisAuthorizationRepository.findByExternalIdAndAuthorizationType(
            cancellationId, CmsAuthorisationType.CANCELLED);

        if (pisConsentAuthorisationOptional.isPresent()) {
            ScaStatus scaStatus = doUpdateConsentAuthorisation(request, pisConsentAuthorisationOptional.get());
            return Optional.of(new UpdatePisCommonPaymentPsuDataResponse(scaStatus));
        }

        return Optional.empty();
    }

    /**
     * Update PIS common payment payment data and stores it into database
     *
     * @param request            PIS common payment request for update payment data
     * @param encryptedPaymentId encrypted common payment ID
     */
    // TODO return correct error code in case consent was not found https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/408
    @Override
    @Transactional
    public void updateCommonPayment(PisCommonPaymentRequest request, String encryptedPaymentId) {
        Optional<PisCommonPaymentData> pisCommonPaymentById = getPisCommonPaymentById(encryptedPaymentId);
        pisCommonPaymentById
            .ifPresent(commonPayment -> savePaymentData(commonPayment, request));
    }

    /**
     * Reads authorisation data by authorisation Id
     *
     * @param authorisationId id of the authorisation
     * @return response contains authorisation data
     */
    @Override
    public Optional<GetPisCommonPaymentAuthorisationResponse> getPisCommonPaymentAuthorisationById(String authorisationId) {
        return pisAuthorizationRepository.findByExternalIdAndAuthorizationType(authorisationId, CmsAuthorisationType.CREATED)
                   .map(pisCommonPaymentMapper::mapToGetPisConsentAuthorizationResponse);
    }

    /**
     * Reads cancellation authorisation data by cancellation Id
     *
     * @param cancellationId id of the authorisation
     * @return response contains authorisation data
     */
    @Override
    public Optional<GetPisCommonPaymentAuthorisationResponse> getPisCommonPaymentCancellationAuthorisationById(String cancellationId) {
        return pisAuthorizationRepository.findByExternalIdAndAuthorizationType(cancellationId, CmsAuthorisationType.CANCELLED)
                   .map(pisCommonPaymentMapper::mapToGetPisConsentAuthorizationResponse);
    }

    /**
     * Reads authorisation IDs data by encrypted payment Id and type of authorization
     *
     * @param encryptedPaymentId encrypted id of the payment
     * @param authorisationType  type of authorization required to create. Can be  CREATED or CANCELLED
     * @return response contains authorisation IDs
     */
    @Override
    public Optional<List<String>> getAuthorisationsByPaymentId(String encryptedPaymentId, CmsAuthorisationType authorisationType) {
        Optional<String> paymentId = securityDataService.decryptId(encryptedPaymentId);
        if (!paymentId.isPresent()) {
            log.warn("Payment Id has not encrypted: {}", encryptedPaymentId);
            return Optional.empty();
        }

        return readReceivedConsentByPaymentId(paymentId.get())
                   .map(cnst -> readAuthorisationsFromConsent(cnst, authorisationType));
    }

    /**
     * Reads Psu data by encrypted payment Id
     *
     * @param encryptedPaymentId encrypted id of the payment
     * @return response contains data of Psu
     */
    @Override
    public Optional<PsuIdData> getPsuDataByPaymentId(String encryptedPaymentId) {
        Optional<String> paymentId = securityDataService.decryptId(encryptedPaymentId);
        if (!paymentId.isPresent()) {
            log.warn("Payment Id has not encrypted: {}", encryptedPaymentId);
            return Optional.empty();
        }

        return readPisCommonPaymentDataByPaymentId(paymentId.get())
                   .map(pc -> psuDataMapper.mapToPsuIdData(pc.getPsuData()));
    }

    /**
     * Reads Psu data by encrypted common payment Id
     *
     * @param encryptedPaymentId encrypted Consent ID
     * @return response contains data of Psu
     */
    @Override
    public Optional<PsuIdData> getPsuDataByConsentId(String encryptedPaymentId) {
        return getPisCommonPaymentById(encryptedPaymentId)
                   .map(pc -> psuDataMapper.mapToPsuIdData(pc.getPsuData()));
    }

    private Optional<PisCommonPaymentData> getActualPisCommonPayment(String encryptedPaymentId) {
        Optional<String> paymentIdDecrypted = securityDataService.decryptId(encryptedPaymentId);
        if (!paymentIdDecrypted.isPresent()) {
            log.warn("Payment Id has not encrypted: {}", encryptedPaymentId);
        }

        return paymentIdDecrypted
                   .flatMap(pisCommonPaymentDataRepository::findByPaymentId)
                   .filter(pm -> !pm.getTransactionStatus().isFinalisedStatus());
    }

    private Optional<PisCommonPaymentData> getPisCommonPaymentById(String encryptedPaymentId) {
        Optional<String> paymentIdDecrypted = securityDataService.decryptId(encryptedPaymentId);
        if (!paymentIdDecrypted.isPresent()) {
            log.warn("Payment Id has not encrypted: {}", encryptedPaymentId);
        }

        return paymentIdDecrypted
                   .flatMap(pisCommonPaymentDataRepository::findByPaymentId);
    }

    private PisCommonPaymentData setStatusAndSaveCommonPaymentData(PisCommonPaymentData commonPaymentData, TransactionStatus status) {
        commonPaymentData.setTransactionStatus(status);
        return pisCommonPaymentDataRepository.save(commonPaymentData);
    }

    private Optional<PisCommonPaymentData> readReceivedConsentByPaymentId(String paymentId) {
        // todo implementation should be changed https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/534
        return pisPaymentDataRepository.findByPaymentIdAndConsent_ConsentStatus(paymentId, RECEIVED)
                                                .filter(CollectionUtils::isNotEmpty)
                                                .map(list -> list.get(0).getPaymentData());

        if (!consentOpt.isPresent()) {
            consentOpt = pisCommonPaymentDataRepository.findByPaymentIdAndConsent_ConsentStatus(paymentId, RECEIVED)
                             .map(PisCommonPaymentData::getConsent);
        }

        return consentOpt;
    }

    private Optional<PisCommonPaymentData> readPisCommonPaymentDataByPaymentId(String paymentId) {
        // todo implementation should be changed https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/534
        Optional<PisCommonPaymentData> commonPayment = pisPaymentDataRepository.findByPaymentId(paymentId)
                                              .filter(CollectionUtils::isNotEmpty)
                                                   .map(list -> list.get(0).getPaymentData())
                                                           .map(pmt-> pmt.get)


        if (!commonPayment.isPresent()) {
            commonPayment = pisCommonPaymentDataRepository.findByPaymentId(paymentId)
                             .map(PisCommonPaymentData::getConsent);
        }

        return commonPayment;
    }

    private void savePaymentData(PisCommonPaymentData pisCommonPayment, PisCommonPaymentRequest request) {
        boolean isCommonPayment = CollectionUtils.isEmpty(request.getPayments()) && request.getPaymentInfo() != null;
        // todo implementation should be changed  https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/534

        if (isCommonPayment) {
            pisCommonPaymentDataRepository.save(pisCommonPaymentMapper.mapToPisCommonPaymentData(request.getPaymentInfo()));
        } else {
            pisPaymentDataRepository.save(pisCommonPaymentMapper.mapToPisPaymentDataList(request.getPayments(), pisCommonPayment));
        }
    }

    /**
     * Creates PIS consent authorisation entity and stores it into database
     *
     * @param pisConsent PIS Consent, for which authorisation is performed
     * @return PisAuthorization
     */
    private PisAuthorization saveNewAuthorisation(PisConsent pisConsent, CmsAuthorisationType authorisationType, PsuIdData psuData) {
        PisAuthorization consentAuthorization = new PisAuthorization();
        consentAuthorization.setExternalId(UUID.randomUUID().toString());
        consentAuthorization.setConsent(pisConsent);
        consentAuthorization.setScaStatus(STARTED);
        consentAuthorization.setAuthorizationType(authorisationType);
        consentAuthorization.setPsuData(psuDataMapper.mapToPsuData(psuData));
        consentAuthorization.setRedirectUrlExpirationTimestamp(OffsetDateTime.now().plus(aspspProfileService.getAspspSettings().getRedirectUrlExpirationTimeMs(), ChronoUnit.MILLIS));
        return pisAuthorizationRepository.save(consentAuthorization);
    }

    private List<String> readAuthorisationsFromConsent(PisConsent pisConsent, CmsAuthorisationType authorisationType) {
        return pisConsent.getAuthorizations()
                   .stream()
                   .filter(auth -> auth.getAuthorizationType() == authorisationType)
                   .map(PisAuthorization::getExternalId)
                   .collect(Collectors.toList());
    }

    private ScaStatus doUpdateConsentAuthorisation(UpdatePisCommonPaymentPsuDataRequest request, PisAuthorization pisConsentAuthorisation) {
        if (pisConsentAuthorisation.getScaStatus().isFinalisedStatus()) {
            return pisConsentAuthorisation.getScaStatus();
        }

        if (SCAMETHODSELECTED == request.getScaStatus()) {
            String chosenMethod = request.getAuthenticationMethodId();
            if (StringUtils.isNotBlank(chosenMethod)) {
                pisConsentAuthorisation.setChosenScaMethod(chosenMethod);
            }
        }
        pisConsentAuthorisation.setScaStatus(request.getScaStatus());
        PisAuthorization saved = pisAuthorizationRepository.save(pisConsentAuthorisation);
        return saved.getScaStatus();
    }
}
