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

package de.adorsys.psd2.xs2a.service.consent;

import de.adorsys.psd2.consent.api.pis.proto.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentRequest;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisPaymentInfo;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentService;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.domain.pis.*;
import de.adorsys.psd2.xs2a.service.mapper.consent.Xs2aToCmsPisConsentRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Xs2aPisConsentService {
    private final PisCommonPaymentService pisCommonPaymentService;
    private final Xs2aToCmsPisConsentRequestMapper xs2aToCmsPisConsentRequestMapper;

    /**
     * Creates PIS consent
     *
     * @param parameters Payment request parameters to get needed payment info
     * @param tppInfo    information about TPP
     * @return String consentId
     */
    // TODO refactoring for orElse(null)
    public CreatePisCommonPaymentResponse createCommonPayment(PaymentInitiationParameters parameters, TppInfo tppInfo) {
        PisPaymentInfo request = new PisPaymentInfo();
        request.setTppInfo(tppInfo);
        request.setPaymentProduct(parameters.getPaymentProduct());
        request.setPaymentType(parameters.getPaymentType());
        request.setPsuData(Collections.singletonList(parameters.getPsuData()));
        request.setTransactionStatus(TransactionStatus.RCVD);
        return pisCommonPaymentService.createCommonPayment(request)
                   .orElse(null);
    }

    public Optional<PisCommonPaymentResponse> getPisCommonPaymentById(String paymentId) {
        return pisCommonPaymentService.getCommonPaymentById(paymentId);
    }

    public void updatePaymentInPisConsent(CommonPayment payment, String paymentId) {
        PisCommonPaymentRequest pisCommonPaymentRequest = xs2aToCmsPisConsentRequestMapper.mapToCmsPisConsentRequest(payment);
        pisCommonPaymentService.updateCommonPayment(pisCommonPaymentRequest, paymentId);
    }

    // TODO  will be deleted!
    public void updateSinglePaymentInPisConsent(SinglePayment singlePayment, PaymentInitiationParameters paymentInitiationParameters, String paymentId) {
        PisCommonPaymentRequest pisCommonPaymentRequest = xs2aToCmsPisConsentRequestMapper.mapToCmsSinglePisConsentRequest(singlePayment, paymentInitiationParameters.getPaymentProduct());
        pisCommonPaymentService.updateCommonPayment(pisCommonPaymentRequest, paymentId);
    }

    public void updatePeriodicPaymentInPisConsent(PeriodicPayment periodicPayment, PaymentInitiationParameters paymentInitiationParameters, String paymentId) {
        PisCommonPaymentRequest pisCommonPaymentRequest = xs2aToCmsPisConsentRequestMapper.mapToCmsPeriodicPisConsentRequest(periodicPayment, paymentInitiationParameters.getPaymentProduct());
        pisCommonPaymentService.updateCommonPayment(pisCommonPaymentRequest, paymentId);
    }

    public void updateBulkPaymentInPisConsent(BulkPayment bulkPayment, PaymentInitiationParameters paymentInitiationParameters, String paymentId) {
        PisCommonPaymentRequest pisCommonPaymentRequest = xs2aToCmsPisConsentRequestMapper.mapToCmsBulkPisConsentRequest(bulkPayment, paymentInitiationParameters.getPaymentProduct());
        pisCommonPaymentService.updateCommonPayment(pisCommonPaymentRequest, paymentId);
    }

    public Optional<Boolean> revokeConsentById(String consentId) {
        return pisCommonPaymentService.updateCommonPaymentStatusById(consentId, TransactionStatus.RJCT);
    }
}
