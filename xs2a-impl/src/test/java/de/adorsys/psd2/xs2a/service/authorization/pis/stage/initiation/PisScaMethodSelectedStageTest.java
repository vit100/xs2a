/*
 * Copyright 2018-2019 adorsys GmbH & Co KG
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

package de.adorsys.psd2.xs2a.service.authorization.pis.stage.initiation;

import de.adorsys.psd2.consent.api.pis.authorisation.GetPisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisPaymentInfo;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.domain.consent.pis.Xs2aUpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.xs2a.service.consent.PisAspspDataService;
import de.adorsys.psd2.xs2a.service.context.SpiContextDataProvider;
import de.adorsys.psd2.xs2a.service.mapper.consent.Xs2aPisCommonPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.SpiErrorMapper;
import de.adorsys.psd2.xs2a.service.payment.Xs2aUpdatePaymentStatusAfterSpiService;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPaymentInfo;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PisScaMethodSelectedStageTest {
    private static final String PAYMENT_ID = "Test payment id";
    private static final String PAYMENT_PRODUCT = "Test payment product";
    private static final String PSU_ID = "Test psuId";
    private static final TransactionStatus ACCP_TRANSACTION_STATUS = TransactionStatus.ACCP;
    private static final PaymentType SINGLE_PAYMENT_TYPE = PaymentType.SINGLE;
    private static final byte[] PAYMENT_DATA = "Test payment data".getBytes();
    private static final PsuIdData PSU_ID_DATA = new PsuIdData(PSU_ID, null, null, null);
    private static final SpiPsuData SPI_PSU_DATA = new SpiPsuData(PSU_ID, null, null, null);
    private static final SpiContextData SPI_CONTEXT_DATA = new SpiContextData(SPI_PSU_DATA, new TppInfo());
    private static final AspspConsentData ASPSP_CONSENT_DATA = new AspspConsentData(new byte[0], "Some Consent ID");
    private static final PisPaymentInfo PAYMENT_INFO = buildPisPaymentInfo();
    private static final SpiPaymentInfo SPI_PAYMENT_INFO = buildSpiPaymentInfo();

    @InjectMocks
    private PisScaMethodSelectedStage pisScaMethodSelectedStage;

    @Mock
    private PisAspspDataService pisAspspDataService;
    @Mock
    private Xs2aUpdatePaymentStatusAfterSpiService updatePaymentStatusAfterSpiService;
    @Mock
    private SpiContextDataProvider spiContextDataProvider;
    @Mock
    private SpiErrorMapper spiErrorMapper;
    @Mock
    private Xs2aPisCommonPaymentMapper xs2aPisCommonPaymentMapper;

    @Mock
    private Xs2aUpdatePisCommonPaymentPsuDataRequest request;
    @Mock
    private GetPisAuthorisationResponse response;

    @Before
    public void setUp() {
        when(response.getPaymentType())
            .thenReturn(SINGLE_PAYMENT_TYPE);

        when(response.getPaymentProduct())
            .thenReturn(PAYMENT_PRODUCT);

        when(response.getPayments())
            .thenReturn(Collections.emptyList());

        when(response.getPaymentInfo())
            .thenReturn(PAYMENT_INFO);

        when(request.getPsuData())
            .thenReturn(PSU_ID_DATA);


    }

    private static PisPaymentInfo buildPisPaymentInfo() {
        PisPaymentInfo paymentInfo = new PisPaymentInfo();
        paymentInfo.setPaymentData(PAYMENT_DATA);
        paymentInfo.setPaymentId(PAYMENT_ID);
        paymentInfo.setPaymentProduct(PAYMENT_PRODUCT);
        paymentInfo.setPaymentType(SINGLE_PAYMENT_TYPE);
        paymentInfo.setTransactionStatus(ACCP_TRANSACTION_STATUS);
        return paymentInfo;
    }

    private static SpiPaymentInfo buildSpiPaymentInfo() {
        SpiPaymentInfo paymentInfo = new SpiPaymentInfo(PAYMENT_PRODUCT);
        paymentInfo.setPaymentData(PAYMENT_DATA);
        paymentInfo.setPaymentId(PAYMENT_ID);
        paymentInfo.setPaymentType(SINGLE_PAYMENT_TYPE);
        paymentInfo.setStatus(ACCP_TRANSACTION_STATUS);
        return paymentInfo;
    }
}
