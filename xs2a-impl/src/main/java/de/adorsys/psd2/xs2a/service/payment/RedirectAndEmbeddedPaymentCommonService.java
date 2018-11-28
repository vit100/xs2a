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

package de.adorsys.psd2.xs2a.service.payment;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.profile.PaymentProduct;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.domain.consent.Xs2aPisConsent;
import de.adorsys.psd2.xs2a.domain.pis.PaymentInitialisationRequest;
import de.adorsys.psd2.xs2a.domain.pis.SinglePaymentInitiationResponse;
import de.adorsys.psd2.xs2a.service.consent.PisConsentDataService;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.SpiErrorMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.SpiToXs2aPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.Xs2aToSpiPsuDataMapper;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPaymentInfo;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.CommonPaymentSpi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


// TODO add add checking SpiResponse on error https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/450
@Service
@RequiredArgsConstructor
public class RedirectAndEmbeddedPaymentCommonService implements ScaPaymentCommonService {

    private final CommonPaymentSpi commonPaymentSpi;
    private final SpiToXs2aPaymentMapper spiToXs2aPaymentMapper;
    private final Xs2aToSpiPsuDataMapper psuDataMapper;
    private final PisConsentDataService pisConsentDataService;
    private final SpiErrorMapper spiErrorMapper;

    @Override
    public SinglePaymentInitiationResponse createPayment(PaymentInitialisationRequest payment, TppInfo tppInfo, PaymentProduct paymentProduct, Xs2aPisConsent pisConsent) {
        AspspConsentData aspspConsentData = pisConsentDataService.getAspspConsentData(pisConsent.getConsentId());
        SpiPsuData spiPsuData = psuDataMapper.mapToSpiPsuData(pisConsent.getPsuData());

        SpiResponse<SpiPaymentInitiationResponse> spiResponse = commonPaymentSpi.initiatePayment(spiPsuData, mapToSpiPaymentRequest(payment, paymentProduct), aspspConsentData);
        pisConsentDataService.updateAspspConsentData(spiResponse.getAspspConsentData());

        if (spiResponse.hasError()) {
            return new SinglePaymentInitiationResponse(spiErrorMapper.mapToErrorHolder(spiResponse));
        }

        return spiToXs2aPaymentMapper.mapToPaymentInitiateResponse(spiResponse.getPayload(), SinglePaymentInitiationResponse::new);
    }


    private SpiPaymentInfo mapToSpiPaymentRequest(PaymentInitialisationRequest payment, PaymentProduct paymentProduct) {
        SpiPaymentInfo request = new SpiPaymentInfo(paymentProduct);

        request.setPaymentId(payment.getPaymentId());
        request.setPaymentType(payment.getPaymentType());
        request.setPaymentData(payment.getPaymentData());

        return request;
    }
}
