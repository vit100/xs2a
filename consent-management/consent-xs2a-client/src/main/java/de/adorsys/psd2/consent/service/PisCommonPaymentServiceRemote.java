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
import de.adorsys.psd2.consent.config.CmsRestException;
import de.adorsys.psd2.consent.config.PisConsentRemoteUrls;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO discuss error handling (e.g. 400 HttpCode response) https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/498
@Slf4j
@Service
@RequiredArgsConstructor
public class PisCommonPaymentServiceRemote implements PisCommonPaymentService {
    @Qualifier("consentRestTemplate")
    private final RestTemplate consentRestTemplate;
    private final PisConsentRemoteUrls remotePisConsentUrls;

    @Override
    public Optional<CreatePisCommonPaymentResponse> createCommonPayment(PisPaymentInfo request) {
        return Optional.ofNullable(consentRestTemplate.postForEntity(remotePisConsentUrls.createPisConsent(), request, CreatePisCommonPaymentResponse.class).getBody());
    }

    @Override
    public Optional<TransactionStatus> getPisCommonPaymentStatusById(String paymentId) {
        return Optional.empty();
    }

    @Override
    public Optional<PisCommonPaymentResponse> getCommonPaymentById(String paymentId) {
        return Optional.ofNullable(consentRestTemplate.getForEntity(remotePisConsentUrls.getPisConsentById(), PisCommonPaymentResponse.class, paymentId)
                                       .getBody());
    }

    @Override
    public Optional<Boolean> updateCommonPaymentStatusById(String paymentId, TransactionStatus status) {
        HttpStatus statusCode = consentRestTemplate.exchange(remotePisConsentUrls.updatePisConsentStatus(), HttpMethod.PUT,
                                                             null, Void.class, paymentId, status).getStatusCode();

        return Optional.of(statusCode == HttpStatus.OK);
    }

    @Override
    public Optional<String> getDecryptedId(String encryptedId) {
        return Optional.ofNullable(consentRestTemplate.getForEntity(remotePisConsentUrls.getPaymentIdByEncryptedString(), String.class, encryptedId)
                                       .getBody());
    }

    @Override
    public Optional<CreatePisAuthorisationResponse> createAuthorization(String paymentId, CmsAuthorisationType authorizationType, PsuIdData psuData) {
        return Optional.ofNullable(consentRestTemplate.postForEntity(remotePisConsentUrls.createPisConsentAuthorisation(),
                                                                     psuData, CreatePisAuthorisationResponse.class, paymentId)
                                       .getBody());
    }

    @Override
    public Optional<CreatePisAuthorisationResponse> createAuthorizationCancellation(String paymentId, CmsAuthorisationType authorizationType, PsuIdData psuData) {
        return Optional.ofNullable(consentRestTemplate.postForEntity(remotePisConsentUrls.createPisConsentAuthorisationCancellation(),
                                                                     psuData, CreatePisAuthorisationResponse.class, paymentId)
                                       .getBody());
    }

    @Override
    public Optional<UpdatePisCommonPaymentPsuDataResponse> updateCommonPaymentAuthorisation(String authorisationId, UpdatePisCommonPaymentPsuDataRequest request) {
        return Optional.ofNullable(consentRestTemplate.exchange(remotePisConsentUrls.updatePisConsentAuthorisation(), HttpMethod.PUT, new HttpEntity<>(request),
                                                                UpdatePisCommonPaymentPsuDataResponse.class, request.getAuthorizationId()).getBody());
    }

    @Override
    public Optional<UpdatePisCommonPaymentPsuDataResponse> updateCommonPaymentCancellationAuthorisation(String authorisationId, UpdatePisCommonPaymentPsuDataRequest request) {
        return Optional.ofNullable(consentRestTemplate.exchange(remotePisConsentUrls.updatePisConsentCancellationAuthorisation(), HttpMethod.PUT, new HttpEntity<>(request),
                                                                UpdatePisCommonPaymentPsuDataResponse.class, request.getAuthorizationId()).getBody());
    }

    @Override
    public void updateCommonPayment(PisCommonPaymentRequest request, String paymentId) {
        consentRestTemplate.exchange(remotePisConsentUrls.updatePisConsentPayment(), HttpMethod.PUT, new HttpEntity<>(request), Void.class, paymentId);
    }

    @Override
    public Optional<GetPisCommonPaymentAuthorisationResponse> getPisCommonPaymentAuthorisationById(String authorizationId) {
        return Optional.ofNullable(consentRestTemplate.exchange(remotePisConsentUrls.getPisConsentAuthorisationById(), HttpMethod.GET, null, GetPisCommonPaymentAuthorisationResponse.class, authorizationId)
                                       .getBody());
    }

    @Override
    public Optional<GetPisCommonPaymentAuthorisationResponse> getPisCommonPaymentCancellationAuthorisationById(String cancellationId) {
        return Optional.ofNullable(consentRestTemplate.exchange(remotePisConsentUrls.getPisConsentCancellationAuthorisationById(), HttpMethod.GET, null, GetPisCommonPaymentAuthorisationResponse.class, cancellationId)
                                       .getBody());
    }

    @Override
    public Optional<List<String>> getAuthorisationsByPaymentId(String paymentId, CmsAuthorisationType authorisationType) {
        String url = getAuthorisationSubResourcesUrl(authorisationType);
        try {
            ResponseEntity<List<String>> request = consentRestTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
                }, paymentId);
            return Optional.ofNullable(request.getBody());
        } catch (CmsRestException cmsRestException) {
            log.warn("No authorisation found by paymentId {}", paymentId);
        }
        return Optional.empty();
    }

    private String getAuthorisationSubResourcesUrl(CmsAuthorisationType authorisationType) {
        switch (authorisationType) {
            case CREATED:
                return remotePisConsentUrls.getAuthorisationSubResources();
            case CANCELLED:
                return remotePisConsentUrls.getCancellationAuthorisationSubResources();
            default:
                log.error("Unknown payment authorisation type {}", authorisationType);
                throw new IllegalArgumentException("Unknown payment authorisation type " + authorisationType);
        }
    }

    @Override
    public Optional<List<PsuIdData>> getPsuDataListByPaymentId(String paymentId) {
        return Optional.ofNullable(consentRestTemplate.getForEntity(remotePisConsentUrls.getPsuDataByPaymentId(), PsuIdData[].class, paymentId)
                                   .getBody())
                   .map(Arrays::asList);
    }
}
