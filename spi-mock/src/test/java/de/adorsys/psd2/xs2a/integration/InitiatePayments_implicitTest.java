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

package de.adorsys.psd2.xs2a.integration;


import de.adorsys.aspsp.xs2a.spi.ASPSPXs2aApplication;
import de.adorsys.psd2.aspsp.mock.api.payment.AspspBulkPayment;
import de.adorsys.psd2.aspsp.mock.api.payment.AspspPeriodicPayment;
import de.adorsys.psd2.aspsp.mock.api.payment.AspspSinglePayment;
import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.proto.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisPaymentInfo;
import de.adorsys.psd2.consent.api.service.EventServiceEncrypted;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentServiceEncrypted;
import de.adorsys.psd2.consent.api.service.TppStopListService;
import de.adorsys.psd2.xs2a.config.*;
import de.adorsys.psd2.xs2a.core.event.Event;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.integration.builder.*;
import de.adorsys.psd2.xs2a.integration.builder.payment.AspspBulkPaymentBuilder;
import de.adorsys.psd2.xs2a.integration.builder.payment.AspspPeriodicPaymentBuilder;
import de.adorsys.psd2.xs2a.integration.builder.payment.AspspSinglePaymentBuilder;
import de.adorsys.psd2.xs2a.service.TppService;
import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"integration-test", "mockspi"})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(
    classes = ASPSPXs2aApplication.class)
@ContextConfiguration(classes = {
    CorsConfigurationProperties.class,
    ObjectMapperConfig.class,
    ScaAuthorizationConfig.class,
    WebConfig.class,
    Xs2aEndpointPathConstant.class,
    Xs2aInterfaceConfig.class
})
public class InitiatePayments_implicitTest {
    private static final Charset UTF_8 = Charset.forName("utf-8");
    private static final String SINGLE_PAYMENT_REQUEST_JSON_PATH = "/json/payment/req/SinglePaymentInitiate_request.json";
    private static final String PERIODIC_PAYMENT_REQUEST_JSON_PATH = "/json/payment/req/PeriodicPaymentInitiate_request.json";
    private static final String BULK_PAYMENT_REQUEST_JSON_PATH = "/json/payment/req/BulkPaymentInitiate_request.json";

    private static PaymentType SINGLE_PAYMENT_TYPE = PaymentType.SINGLE;
    private static PaymentType PERIODIC_PAYMENT_TYPE = PaymentType.PERIODIC;
    private static PaymentType BULK_PAYMENT_TYPE = PaymentType.BULK;

    private static String SEPA_PAYMENT_PRODUCT = "sepa-credit-transfers";

    private static String ENCRYPT_PAYMENT_ID = "DfLtDOgo1tTK6WQlHlb-TMPL2pkxRlhZ4feMa5F4tOWwNN45XLNAVfWwoZUKlQwb_=_bS6p6XvTWI";
    private static String PAYMENT_ID = "5c408672d3121704efe90394";
    private static final String ASPSP_ACCOUNT_ID = "33333-33333";
    private static String AUTHORISATION_ID = "e8356ea7-8e3e-474f-b5ea-2b89346cb2dc";

    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final BigDecimal AMOUNT_OPERATION_113 = new BigDecimal("113");
    private static final String DEB_IBAN = "LU280019400644750000";
    private static final String CRED_IBAN = "DE89370400440532013000";

    private final TppInfo TPP_INFO = TppInfoBuilder.buildTppInfo();
    private HttpHeaders httpHeadersImplicit = new HttpHeaders();
    HashMap<Pair<PaymentType, ScaApproach>, String> responseMap = new HashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AspspProfileService aspspProfileService;
    @MockBean
    private TppService tppService;
    @MockBean
    private TppStopListService tppStopListService;
    @MockBean
    private EventServiceEncrypted eventServiceEncrypted;
    @MockBean
    private PisCommonPaymentServiceEncrypted pisCommonPaymentServiceEncrypted;

    @MockBean
    @Qualifier("aspspRestTemplate")
    private RestTemplate aspspRestTemplate;
    @MockBean
    @Qualifier("consentRestTemplate")
    private RestTemplate consentRestTemplate;

    @Before
    public void init() {
        // common actions for all tests
        responseMap.put(new Pair(PaymentType.SINGLE, ScaApproach.REDIRECT), "/json/payment/res/implicit/SinglePaymentInitiate_redirect_implicit_response.json");
        responseMap.put(new Pair(PaymentType.SINGLE, ScaApproach.EMBEDDED), "/json/payment/res/implicit/SinglePaymentInitiate_embedded_implicit_response.json");

        responseMap.put(new Pair(PaymentType.PERIODIC, ScaApproach.REDIRECT), "/json/payment/res/implicit/PeriodicPaymentInitiate_redirect_implicit_response.json");
        responseMap.put(new Pair(PaymentType.PERIODIC, ScaApproach.EMBEDDED), "/json/payment/res/implicit/PeriodicPaymentInitiate_embedded_implicit_response.json");

        responseMap.put(new Pair(PaymentType.BULK, ScaApproach.REDIRECT), "/json/payment/res/implicit/BulkPaymentInitiate_redirect_implicit_response.json");
        responseMap.put(new Pair(PaymentType.BULK, ScaApproach.EMBEDDED), "/json/payment/res/implicit/BulkPaymentInitiate_embedded_implicit_response.json");

        given(aspspProfileService.getAspspSettings())
            .willReturn(AspspSettingsBuilder.buildAspspSettings());
        given(tppService.getTppInfo())
            .willReturn(TPP_INFO);
        given(tppService.getTppId())
            .willReturn(TPP_INFO.getAuthorisationNumber());
        given(tppStopListService.checkIfTppBlocked(TppInfoBuilder.buildTppUniqueParamsHolder()))
            .willReturn(false);
        given(eventServiceEncrypted.recordEvent(any(Event.class)))
            .willReturn(true);

        PisPaymentInfo pisPaymentInfo = new PisPaymentInfoBuilder().buildPisPaymentInfo(SEPA_PAYMENT_PRODUCT, SINGLE_PAYMENT_TYPE, ASPSP_ACCOUNT_ID);
        given(pisCommonPaymentServiceEncrypted.createCommonPayment(pisPaymentInfo))
            .willReturn(Optional.of(new CreatePisCommonPaymentResponse(ENCRYPT_PAYMENT_ID)));

        given(pisCommonPaymentServiceEncrypted.createAuthorization(ENCRYPT_PAYMENT_ID, CmsAuthorisationType.CREATED, PsuIdDataBuilder.buildPsuIdData()))
            .willReturn(Optional.of(new CreatePisAuthorisationResponse(AUTHORISATION_ID)));

        httpHeadersImplicit.add("Content-Type", "application/json");
        httpHeadersImplicit.add("tpp-qwac-certificate", "qwac certificate");
        httpHeadersImplicit.add("X-Request-ID", "2f77a125-aa7a-45c0-b414-cea25a116035");
        httpHeadersImplicit.add("PSU-ID", "PSU-123");
        httpHeadersImplicit.add("PSU-ID-Type", "Some type");
        httpHeadersImplicit.add("PSU-Corporate-ID", "Some corporate id");
        httpHeadersImplicit.add("PSU-Corporate-ID-Type", "Some corporate id type");
        httpHeadersImplicit.add("PSU-IP-Address", "1.1.1.1");
        httpHeadersImplicit.add("TPP-Implicit-Authorisation-Preferred", "false"); // when Implicit auth mode we need to set 'false'
    }

    @Test
    public void initiateSinglePayment_embedded_successful() throws Exception {
        initiateSinglePayment_successful(ScaApproach.EMBEDDED);
    }

    @Test
    public void initiateSinglePayment_redirect_successful() throws Exception {
        initiateSinglePayment_successful(ScaApproach.REDIRECT);
    }

    @Test
    public void initiatePeriodicPayment_embedded_successful() throws Exception {
        initiatePeriodicPayment_successful(ScaApproach.EMBEDDED);
    }

    @Test
    public void initiatePeriodicPayment_redirect_successful() throws Exception {
        initiatePeriodicPayment_successful(ScaApproach.REDIRECT);
    }

    @Test
    public void initiateBulkPayment_embedded_successful() throws Exception {
        initiateBulkPayment_successful(ScaApproach.EMBEDDED);
    }

    @Test
    public void initiateBulkPayment_redirect_successful() throws Exception {
        initiateBulkPayment_successful(ScaApproach.REDIRECT);
    }

    private void initiateSinglePayment_successful(ScaApproach scaApproach) throws Exception {
        // Given
        given(aspspProfileService.getScaApproach()).willReturn(scaApproach);
        AspspSinglePayment testPayment = new AspspSinglePaymentBuilder().buildAspspSinglePayment(PAYMENT_ID, DEB_IBAN, CRED_IBAN, CURRENCY, AMOUNT_OPERATION_113);

        given(aspspRestTemplate.postForEntity(any(String.class), any(AspspSinglePayment.class), any(Class.class)))
            .willReturn(ResponseEntity.ok(testPayment));
        given(consentRestTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(String.class)))
            .willReturn(ResponseEntity.ok(Void.class));

        MockHttpServletRequestBuilder requestBuilder = post(UrlBuilder.buildInitiatePaymentUrl(SINGLE_PAYMENT_TYPE.getValue(), SEPA_PAYMENT_PRODUCT));
        requestBuilder.headers(httpHeadersImplicit);
        requestBuilder.content(IOUtils.resourceToString(SINGLE_PAYMENT_REQUEST_JSON_PATH, UTF_8));

        // When
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Then
        resultActions.andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(IOUtils.resourceToString(responseMap.get(new Pair<>(PaymentType.SINGLE, scaApproach)), UTF_8)));
    }

    private void initiatePeriodicPayment_successful(ScaApproach scaApproach) throws Exception {
        // Given
        given(aspspProfileService.getScaApproach()).willReturn(scaApproach);
        AspspPeriodicPayment testPayment = new AspspPeriodicPaymentBuilder().buildAspspPeriodicPayment(PAYMENT_ID, DEB_IBAN, CRED_IBAN, CURRENCY, AMOUNT_OPERATION_113);

        given(aspspRestTemplate.postForEntity(any(String.class), any(AspspPeriodicPayment.class), any(Class.class)))
            .willReturn(ResponseEntity.ok(testPayment));
        given(consentRestTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(String.class)))
            .willReturn(ResponseEntity.ok(Void.class));

        MockHttpServletRequestBuilder requestBuilder = post(UrlBuilder.buildInitiatePaymentUrl(PERIODIC_PAYMENT_TYPE.getValue(), SEPA_PAYMENT_PRODUCT));
        requestBuilder.headers(httpHeadersImplicit);
        requestBuilder.content(IOUtils.resourceToString(PERIODIC_PAYMENT_REQUEST_JSON_PATH, UTF_8));

        // When
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Then
        resultActions.andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(IOUtils.resourceToString(responseMap.get(new Pair<>(PaymentType.PERIODIC, scaApproach)), UTF_8)));
    }

    private void initiateBulkPayment_successful(ScaApproach scaApproach) throws Exception {
        // Given
        HashMap<String, BigDecimal> amountMap = new HashMap<>();
        amountMap.put("DE21500105176194357737", new BigDecimal("666"));
        amountMap.put("DE54500105173424724776", new BigDecimal("888"));

        given(aspspProfileService.getScaApproach()).willReturn(scaApproach);
        AspspBulkPayment testPayment = new AspspBulkPaymentBuilder().buildAspspBulkPayment(PAYMENT_ID, DEB_IBAN, CURRENCY, amountMap);

        given(aspspRestTemplate.postForEntity(any(String.class), any(AspspBulkPayment.class), any(Class.class)))
            .willReturn(ResponseEntity.ok(testPayment));
        given(consentRestTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(String.class)))
            .willReturn(ResponseEntity.ok(Void.class));

        MockHttpServletRequestBuilder requestBuilder = post(UrlBuilder.buildInitiatePaymentUrl(BULK_PAYMENT_TYPE.getValue(), SEPA_PAYMENT_PRODUCT));
        requestBuilder.headers(httpHeadersImplicit);
        requestBuilder.content(IOUtils.resourceToString(BULK_PAYMENT_REQUEST_JSON_PATH, UTF_8));

        // When
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Then
        resultActions.andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(IOUtils.resourceToString(responseMap.get(new Pair<>(PaymentType.BULK, scaApproach)), UTF_8)));
    }
}

