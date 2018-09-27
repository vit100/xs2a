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

package de.adorsys.aspsp.xs2a.domain.consent;

import de.adorsys.aspsp.xs2a.domain.pis.PeriodicPayment;
import de.adorsys.aspsp.xs2a.domain.pis.SinglePayment;
import de.adorsys.aspsp.xs2a.domain.pis.TppInfo;
import de.adorsys.aspsp.xs2a.spi.domain.consent.AspspConsentData;
import lombok.Value;

import java.util.List;

@Value
public class CreatePisConsentData {
    private SinglePayment singlePayment;
    private List<SinglePayment> bulkPayments;
    private PeriodicPayment periodicPayment;
    private TppInfo tppInfo;
    private String paymentProduct;
    private AspspConsentData aspspConsentData;

    public CreatePisConsentData(SinglePayment singlePayment, TppInfo tppInfo, String paymentProduct, AspspConsentData aspspConsentData) {
        this.singlePayment = singlePayment;
        this.tppInfo = tppInfo;
        this.paymentProduct = paymentProduct;
        this.aspspConsentData = aspspConsentData;
        this.bulkPayments = null;
        this.periodicPayment = null;
    }

    public CreatePisConsentData(List<SinglePayment> bulkPayments, TppInfo tppInfo, String paymentProduct, AspspConsentData aspspConsentData) {
        this.bulkPayments = bulkPayments;
        this.tppInfo = tppInfo;
        this.paymentProduct = paymentProduct;
        this.aspspConsentData = aspspConsentData;
        this.singlePayment = null;
        this.periodicPayment = null;
    }

    public CreatePisConsentData(PeriodicPayment periodicPayment, TppInfo tppInfo, String paymentProduct, AspspConsentData aspspConsentData) {
        this.periodicPayment = periodicPayment;
        this.tppInfo = tppInfo;
        this.paymentProduct = paymentProduct;
        this.aspspConsentData = aspspConsentData;
        this.bulkPayments = null;
        this.singlePayment = null;
    }
}
