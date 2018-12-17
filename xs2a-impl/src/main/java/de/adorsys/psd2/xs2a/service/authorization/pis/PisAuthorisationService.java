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

package de.adorsys.psd2.xs2a.service.authorization.pis;

import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisCommonPaymentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentService;
import de.adorsys.psd2.xs2a.config.factory.PisScaStageAuthorisationFactory;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.domain.consent.pis.Xs2aUpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.xs2a.domain.consent.pis.Xs2aUpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.xs2a.service.authorization.pis.stage.PisScaStage;
import de.adorsys.psd2.xs2a.service.mapper.consent.Xs2aPisCommonPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static de.adorsys.psd2.xs2a.config.factory.PisScaStageAuthorisationFactory.CANCELLATION_SERVICE_PREFIX;
import static de.adorsys.psd2.xs2a.config.factory.PisScaStageAuthorisationFactory.SERVICE_PREFIX;

@Service
@RequiredArgsConstructor
// TODO this class takes low-level communication to Consent-management-system. Should be migrated to consent-services package. All XS2A business-logic should be removed from here to XS2A services. https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/332
public class PisAuthorisationService {
    private final PisCommonPaymentService pisCommonPaymentService;
    private final PisScaStageAuthorisationFactory pisScaStageAuthorisationFactory;
    private final Xs2aPisCommonPaymentMapper pisCommonPaymentMapper;

    /**
     * Sends a POST request to CMS to store created pis authorisation
     *
     * @param paymentId String representation of identifier of stored payment
     * @param psuData   PsuIdData container of authorisation data about PSU
     * @return a response object containing authorisation id
     */
    public CreatePisAuthorisationResponse createPisAuthorisation(String paymentId, PsuIdData psuData) {
        return pisCommonPaymentService.createAuthorization(paymentId, CmsAuthorisationType.CREATED, psuData)
                   .orElse(null);
    }

    /**
     * Updates PIS  authorisation according to psu's sca methods
     *
     * @param request Provides transporting data when updating pis authorisation
     * @return update pis authorisation response, which contains payment id, authorisation id, sca status, psu message and links
     */
    public Xs2aUpdatePisCommonPaymentPsuDataResponse updatePisAuthorisation(Xs2aUpdatePisCommonPaymentPsuDataRequest request) {
        GetPisCommonPaymentAuthorisationResponse response = pisCommonPaymentService.getPisCommonPaymentAuthorisationById(request.getAuthorizationId())
                                                                .orElse(null);

        PisScaStage<Xs2aUpdatePisCommonPaymentPsuDataRequest, GetPisCommonPaymentAuthorisationResponse, Xs2aUpdatePisCommonPaymentPsuDataResponse> service = pisScaStageAuthorisationFactory.getService(SERVICE_PREFIX + response.getScaStatus().name());
        Xs2aUpdatePisCommonPaymentPsuDataResponse stageResponse = service.apply(request, response);

        if (!stageResponse.hasError()) {
            doUpdatePisAuthorisation(pisCommonPaymentMapper.mapToCmsUpdateCommonPaymentPsuDataReq(request, stageResponse));
        }

        return stageResponse;
    }

    /**
     * Updates PIS cancellation authorisation according to psu's sca methods
     *
     * @param request Provides transporting data when updating pis cancellation authorisation
     * @return update pis authorisation response, which contains payment id, authorisation id, sca status, psu message and links
     */
    public Xs2aUpdatePisCommonPaymentPsuDataResponse updatePisCancellationAuthorisation(Xs2aUpdatePisCommonPaymentPsuDataRequest request) {
        GetPisCommonPaymentAuthorisationResponse response = pisCommonPaymentService.getPisCommonPaymentCancellationAuthorisationById(request.getAuthorizationId())
                                                                .orElse(null);

        PisScaStage<Xs2aUpdatePisCommonPaymentPsuDataRequest, GetPisCommonPaymentAuthorisationResponse, Xs2aUpdatePisCommonPaymentPsuDataResponse> service = pisScaStageAuthorisationFactory.getService(CANCELLATION_SERVICE_PREFIX + response.getScaStatus().name());
        Xs2aUpdatePisCommonPaymentPsuDataResponse stageResponse = service.apply(request, response);

        if (!stageResponse.hasError()) {
            doUpdatePisCancellationAuthorisation(pisCommonPaymentMapper.mapToCmsUpdateCommonPaymentPsuDataReq(request, stageResponse));
        }

        return stageResponse;
    }

    public void doUpdatePisAuthorisation(UpdatePisCommonPaymentPsuDataRequest request) {
        pisCommonPaymentService.updateCommonPaymentAuthorisation(request.getAuthorizationId(), request);
    }

    public void doUpdatePisCancellationAuthorisation(UpdatePisCommonPaymentPsuDataRequest request) {
        pisCommonPaymentService.updateCommonPaymentCancellationAuthorisation(request.getAuthorizationId(), request);
    }

    /**
     * Sends a POST request to CMS to store created pis authorisation cancellation
     *
     * @param paymentId String representation of identifier of payment ID
     * @param psuData   PsuIdData container of authorisation data about PSU
     * @return long representation of identifier of stored pis authorisation cancellation
     */
    public CreatePisAuthorisationResponse createPisAuthorisationCancellation(String paymentId, PsuIdData psuData) {
        return pisCommonPaymentService.createAuthorizationCancellation(paymentId, CmsAuthorisationType.CANCELLED, psuData)
                   .orElse(null);
    }

    /**
     * Sends a GET request to CMS to get cancellation authorisation sub resources
     *
     * @param paymentId String representation of identifier of payment ID
     * @return list of pis authorisation IDs
     */
    public Optional<List<String>> getCancellationAuthorisationSubResources(String paymentId) {
        return pisCommonPaymentService.getAuthorisationsByPaymentId(paymentId, CmsAuthorisationType.CANCELLED);
    }

    /**
     * Sends a GET request to CMS to get authorisation sub resources
     *
     * @param paymentId String representation of identifier of payment ID
     * @return list of pis authorisation IDs
     */
    public Optional<List<String>> getAuthorisationSubResources(String paymentId) {
        return pisCommonPaymentService.getAuthorisationsByPaymentId(paymentId, CmsAuthorisationType.CREATED);
    }
}
