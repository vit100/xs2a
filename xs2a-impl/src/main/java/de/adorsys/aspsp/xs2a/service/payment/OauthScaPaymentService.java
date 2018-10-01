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

package de.adorsys.aspsp.xs2a.service.payment;

import de.adorsys.aspsp.xs2a.domain.MessageErrorCode;
import de.adorsys.aspsp.xs2a.domain.Xs2aTransactionStatus;
import de.adorsys.aspsp.xs2a.domain.pis.*;
import de.adorsys.aspsp.xs2a.service.mapper.PaymentMapper;
import de.adorsys.aspsp.xs2a.spi.domain.SpiResponse;
import de.adorsys.aspsp.xs2a.spi.domain.consent.AspspConsentData;
import de.adorsys.aspsp.xs2a.spi.domain.payment.SpiPaymentInitialisationResponse;
import de.adorsys.aspsp.xs2a.spi.service.PaymentSpi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static de.adorsys.aspsp.xs2a.domain.MessageErrorCode.PAYMENT_FAILED;

@Service
@RequiredArgsConstructor
public class OauthScaPaymentService implements ScaPaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentSpi paymentSpi;

    @Override
    public PaymentInitialisationResponse createSinglePayment(SinglePayment singlePayment, TppInfo tppInfo, String paymentProduct) {
        return paymentMapper.mapToPaymentInitializationResponse(paymentSpi.createPaymentInitiation(paymentMapper.mapToSpiSinglePayment(singlePayment), new AspspConsentData()));
    }

    @Override
    public PaymentInitialisationResponse createPeriodicPayment(PeriodicPayment periodicPayment, TppInfo tppInfo, String paymentProduct) {
        return paymentMapper.mapToPaymentInitializationResponse(paymentSpi.initiatePeriodicPayment(paymentMapper.mapToSpiPeriodicPayment(periodicPayment), new AspspConsentData()));
    }

    @Override
    public List<PaymentInitialisationResponse> createBulkPayment(BulkPayment bulkPayment, TppInfo tppInfo, String paymentProduct) {
        SpiResponse<List<SpiPaymentInitialisationResponse>> spiResponse = paymentSpi.createBulkPayments(paymentMapper.mapToSpiBulkPayment(bulkPayment), new AspspConsentData());
        return checkAndUpdatePaymentsForRejectionStatus(paymentMapper.mapToPaymentInitializationResponseList(spiResponse));
    }

    private List<PaymentInitialisationResponse> checkAndUpdatePaymentsForRejectionStatus(List<PaymentInitialisationResponse> paymentInitialisationResponseList) {
        return paymentInitialisationResponseList.stream()
                   .peek(resp -> {
                       if (StringUtils.isBlank(resp.getPaymentId()) || resp.getTransactionStatus() == Xs2aTransactionStatus.RJCT) {
                           resp.setTppMessages(new MessageErrorCode[]{PAYMENT_FAILED});
                           resp.setTransactionStatus(Xs2aTransactionStatus.RJCT);
                       }
                   }).collect(Collectors.toList());
    }
}
