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

package de.adorsys.psd2.xs2a.service.authorization.pis.stage;

import de.adorsys.psd2.consent.api.pis.authorisation.GetPisCommonPaymentAuthorisationResponse;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.domain.consent.pis.Xs2aUpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.xs2a.domain.consent.pis.Xs2aUpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.xs2a.service.consent.PisCommonPaymentDataService;
import de.adorsys.psd2.xs2a.service.context.SpiContextDataProvider;
import de.adorsys.psd2.xs2a.service.mapper.consent.CmsToXs2aPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.consent.Xs2aPisCommonPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.SpiErrorMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.Xs2aToSpiBulkPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.Xs2aToSpiPeriodicPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.Xs2aToSpiSinglePaymentMapper;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PaymentSpi;
import de.adorsys.psd2.xs2a.spi.service.SpiPayment;
import org.springframework.stereotype.Service;

import static de.adorsys.psd2.xs2a.core.sca.ScaStatus.FINALISED;

@Service("PIS_SCAMETHODSELECTED")
public class PisScaMethodSelectedStage extends PisScaStage<Xs2aUpdatePisCommonPaymentPsuDataRequest, GetPisCommonPaymentAuthorisationResponse, Xs2aUpdatePisCommonPaymentPsuDataResponse> {
    private final PisCommonPaymentDataService pisCommonPaymentDataService;
    private final SpiErrorMapper spiErrorMapper;
    private final Xs2aPisCommonPaymentMapper xs2aPisConsentMapper;
    private final SpiContextDataProvider spiContextDataProvider;

    public PisScaMethodSelectedStage(PisCommonPaymentDataService pisCommonPaymentDataService, CmsToXs2aPaymentMapper cmsToXs2aPaymentMapper, Xs2aToSpiPeriodicPaymentMapper xs2aToSpiPeriodicPaymentMapper, Xs2aToSpiSinglePaymentMapper xs2aToSpiSinglePaymentMapper, Xs2aToSpiBulkPaymentMapper xs2aToSpiBulkPaymentMapper, Xs2aPisCommonPaymentMapper xs2aPisConsentMapper, SpiErrorMapper spiErrorMapper, SpiContextDataProvider spiContextDataProvider) {
        super(cmsToXs2aPaymentMapper, xs2aToSpiPeriodicPaymentMapper, xs2aToSpiSinglePaymentMapper, xs2aToSpiBulkPaymentMapper);
        this.spiErrorMapper = spiErrorMapper;
        this.pisCommonPaymentDataService = pisCommonPaymentDataService;
        this.xs2aPisConsentMapper = xs2aPisConsentMapper;
        this.spiContextDataProvider = spiContextDataProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Xs2aUpdatePisCommonPaymentPsuDataResponse apply(Xs2aUpdatePisCommonPaymentPsuDataRequest request, GetPisCommonPaymentAuthorisationResponse response) {
        PaymentType paymentType = response.getPaymentType();
        String paymentProduct = response.getPaymentProduct();
        SpiPayment payment = mapToSpiPayment(response, paymentType, paymentProduct);
        PsuIdData psuData = request.getPsuData();
        PaymentSpi paymentSpi = getPaymentService(response, paymentType);

        AspspConsentData aspspConsentData = pisCommonPaymentDataService.getAspspConsentData(request.getPaymentId());

        // we need to get decrypted payment ID
        String internalId = pisCommonPaymentDataService.getInternalPaymentIdByEncryptedString(request.getPaymentId());
        SpiScaConfirmation spiScaConfirmation = xs2aPisConsentMapper.buildSpiScaConfirmation(request, response.getPaymentId(), internalId);

        SpiContextData contextData = spiContextDataProvider.provideWithPsuIdData(psuData);

        SpiResponse<SpiResponse.VoidResponse> spiResponse = paymentSpi.verifyScaAuthorisationAndExecutePayment(contextData, spiScaConfirmation, payment, aspspConsentData);
        pisCommonPaymentDataService.updateAspspConsentData(spiResponse.getAspspConsentData());

        if (spiResponse.hasError()) {
            return new Xs2aUpdatePisCommonPaymentPsuDataResponse(spiErrorMapper.mapToErrorHolder(spiResponse));
        }
        // TODO check the paymentSpi result first https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/338
        Xs2aUpdatePisCommonPaymentPsuDataResponse xs2aResponse = new Xs2aUpdatePisCommonPaymentPsuDataResponse(FINALISED);
        xs2aResponse.setPsuId(psuData.getPsuId());
        return xs2aResponse;
    }
}
