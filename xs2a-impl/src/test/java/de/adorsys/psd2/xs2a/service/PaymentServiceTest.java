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
 *//*



package de.adorsys.psd2.xs2a.service;

import de.adorsys.psd2.consent.api.pis.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.PisPayment;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentResponse;
import de.adorsys.psd2.xs2a.config.factory.ReadPaymentFactory;
import de.adorsys.psd2.xs2a.config.factory.ReadPaymentStatusFactory;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.event.EventType;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.core.tpp.TppRedirectUri;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import de.adorsys.psd2.xs2a.domain.*;
import de.adorsys.psd2.xs2a.domain.consent.Xs2aPisCommonPayment;
import de.adorsys.psd2.xs2a.domain.pis.*;
import de.adorsys.psd2.xs2a.exception.MessageError;
import de.adorsys.psd2.xs2a.service.consent.PisAspspDataService;
import de.adorsys.psd2.xs2a.service.consent.PisPsuDataService;
import de.adorsys.psd2.xs2a.service.consent.Xs2aPisCommonPaymentService;
import de.adorsys.psd2.xs2a.service.context.SpiContextDataProvider;
import de.adorsys.psd2.xs2a.service.event.Xs2aEventService;
import de.adorsys.psd2.xs2a.service.mapper.consent.CmsToXs2aPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.consent.Xs2aPisCommonPaymentMapper;
import de.adorsys.psd2.xs2a.service.mapper.psd2.ErrorType;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.Xs2aToSpiPaymentInfoMapper;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.Xs2aToSpiPsuDataMapper;
import de.adorsys.psd2.xs2a.service.payment.*;
import de.adorsys.psd2.xs2a.service.profile.AspspProfileServiceWrapper;
import de.adorsys.psd2.xs2a.service.profile.StandardPaymentProductsResolver;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.*;
import static de.adorsys.psd2.xs2a.domain.MessageErrorCode.RESOURCE_UNKNOWN_404;
import static de.adorsys.psd2.xs2a.domain.MessageErrorCode.SERVICE_INVALID_405;
import static de.adorsys.psd2.xs2a.domain.TppMessageInformation.of;
import static de.adorsys.psd2.xs2a.service.mapper.psd2.ErrorType.PIS_405;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {
    private static final String PAYMENT_ID = "12345";
    private static final String PAYMENT_PRODUCT = "sepa-credit-transfers";
    private static final String IBAN = "DE123456789";
    private static final String AMOUNT = "100";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final AspspConsentData ASPSP_CONSENT_DATA = new AspspConsentData(new byte[0], PAYMENT_ID);
    private static final PsuIdData PSU_ID_DATA = new PsuIdData(null, null, null, null);
    private static final SpiPsuData SPI_PSU_DATA = new SpiPsuData(null, null, null, null);
    private final List<String> ERROR_MESSAGE_TEXT = Arrays.asList("message 1", "message 2", "message 3");
    private final SinglePayment SINGLE_PAYMENT_OK = getSinglePayment(IBAN, AMOUNT);

    private final BulkPayment BULK_PAYMENT_OK = getBulkPayment(SINGLE_PAYMENT_OK, IBAN);

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private CmsToXs2aPaymentMapper cmsToXs2aPaymentMapper;
    @Mock
    private CancelPaymentService cancelPaymentService;
    @Mock
    private ReadPaymentFactory readPaymentFactory;
    @Mock
    private Xs2aPisCommonPaymentService xs2aPisCommonPaymentService;
    @Mock
    private PisAspspDataService pisAspspDataService;
    @Mock
    private TppService tppService;
    @Mock
    private CreateSinglePaymentService createSinglePaymentService;
    @Mock
    private CreatePeriodicPaymentService createPeriodicPaymentService;
    @Mock
    private CreateBulkPaymentService createBulkPaymentService;
    @Mock
    private Xs2aPisCommonPaymentMapper xs2aPisCommonPaymentMapper;
    @Mock
    private SinglePaymentSpi singlePaymentSpi;
    @Mock
    private PeriodicPaymentSpi periodicPaymentSpi;
    @Mock
    private BulkPaymentSpi bulkPaymentSpi;
    @Mock
    private AspspProfileServiceWrapper aspspProfileService;
    @Mock
    private Xs2aToSpiPsuDataMapper psuDataMapper;
    @Mock
    private PisPsuDataService pisPsuDataService;
    @Mock
    private Xs2aEventService xs2aEventService;
    @Mock
    private ReadPaymentService<PaymentInformationResponse> readPaymentService;
    @Mock
    private SpiPaymentFactory spiPaymentFactory;
    @Mock
    private ReadPaymentStatusFactory readPaymentStatusFactory;
    @Mock
    private ReadPaymentStatusService readPaymentStatusService;
    @Mock
    private SpiContextDataProvider spiContextDataProvider;
    @Mock
    private PisCommonPaymentResponse pisCommonPaymentResponse;
    @Mock
    private PisPayment pisPayment;
    @Mock
    private SpiPayment spiPayment;
    @Mock
    private SpiSinglePayment spiSinglePayment;
    @Mock
    private Xs2aUpdatePaymentStatusAfterSpiService updatePaymentStatusAfterSpiService;
    @Mock
    private StandardPaymentProductsResolver standardPaymentProductsResolver;
    @Mock
    private Xs2aToSpiPaymentInfoMapper xs2aToSpiPaymentInfoMapper;
    @Mock
    private CommonPaymentSpi commonPaymentSpi;

    @Before
    public void setUp() {
        //Mapper
        when(xs2aPisCommonPaymentMapper.mapToXs2aPisCommonPayment(any(), any())).thenReturn(getXs2aPisCommonPayment());
        when(psuDataMapper.mapToSpiPsuData(PSU_ID_DATA))
            .thenReturn(SPI_PSU_DATA);
        when(xs2aPisCommonPaymentMapper.mapToXs2aPisCommonPayment(new CreatePisCommonPaymentResponse("TEST"), PSU_ID_DATA)).thenReturn(getXs2aPisCommonPayment());
        when(pisAspspDataService.getInternalPaymentIdByEncryptedString("TEST")).thenReturn("TEST");
        when(readPaymentStatusFactory.getService(anyString())).thenReturn(readPaymentStatusService);
        //Status by ID
        when(createBulkPaymentService.createPayment(BULK_PAYMENT_OK, buildPaymentInitiationParameters(PaymentType.BULK), getTppInfoServiceModified()))
            .thenReturn(getValidResponse());

        when(pisAspspDataService.getAspspConsentData(anyString())).thenReturn(ASPSP_CONSENT_DATA);
        when(tppService.getTppInfo()).thenReturn(getTppInfo());

        when(cancelPaymentService.initiatePaymentCancellation(any(), any(), any()))
            .thenReturn(ResponseObject.<CancelPaymentResponse>builder()
                            .body(getCancelPaymentResponse(true, ACTC))
                            .build());
        when(cancelPaymentService.cancelPaymentWithoutAuthorisation(any(), any(), any()))
            .thenReturn(ResponseObject.<CancelPaymentResponse>builder()
                            .body(getCancelPaymentResponse(false, CANC))
                            .build());

        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(PAYMENT_ID))
            .thenReturn(getPisCommonPayment());
        when(readPaymentFactory.getService(anyString())).thenReturn(readPaymentService);
        when(standardPaymentProductsResolver.isRawPaymentProduct(anyString()))
            .thenReturn(false);
    }

    // TODO Update tests after rearranging order of payment creation with pis consent https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/159
    //Bulk Tests
    @Test
    public void createBulkPayments() {
        //When
        ResponseObject<BulkPaymentInitiationResponse> actualResponse = paymentService.createPayment(BULK_PAYMENT_OK, buildPaymentInitiationParameters(PaymentType.BULK));
        //Then
        assertThat(actualResponse.hasError()).isFalse();
        assertThat(actualResponse.getBody().getPaymentId()).isEqualTo(PAYMENT_ID);
        assertThat(actualResponse.getBody().getTransactionStatus()).isEqualTo(RCVD);
    }

    @Test
    public void cancelPayment_Success_WithAuthorisation() {
        when(aspspProfileService.isPaymentCancellationAuthorizationMandated()).thenReturn(Boolean.TRUE);
        when(pisPsuDataService.getPsuDataByPaymentId(PAYMENT_ID))
            .thenReturn(Collections.singletonList(PSU_ID_DATA));
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(PAYMENT_ID)).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.SINGLE);
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn(PAYMENT_PRODUCT);
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getTransactionStatus()).thenReturn(ACCP);
        doReturn(Optional.of(spiPayment))
            .when(spiPaymentFactory).createSpiPaymentByPaymentType(eq(Collections.singletonList(pisPayment)), eq(PAYMENT_PRODUCT), any(PaymentType.class));

        // When
        ResponseObject<CancelPaymentResponse> actual = paymentService.cancelPayment(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        // Then
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().isStartAuthorisationRequired()).isEqualTo(true);
    }

    @Test
    public void cancelPayment_Success_WithoutAuthorisation() {
        when(aspspProfileService.isPaymentCancellationAuthorizationMandated()).thenReturn(Boolean.FALSE);
        when(pisPsuDataService.getPsuDataByPaymentId(PAYMENT_ID))
            .thenReturn(Collections.singletonList(PSU_ID_DATA));
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.SINGLE);
        when(pisCommonPaymentResponse.getTransactionStatus()).thenReturn(ACCP);
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn(PAYMENT_PRODUCT);
        doReturn(Optional.of(spiPayment))
            .when(spiPaymentFactory).createSpiPaymentByPaymentType(eq(Collections.singletonList(pisPayment)), eq(PAYMENT_PRODUCT), any(PaymentType.class));

        // When
        ResponseObject<CancelPaymentResponse> actual = paymentService.cancelPayment(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        // Then
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().isStartAuthorisationRequired()).isEqualTo(false);
    }

    @Test
    public void cancelPayment_Fail_WithAuthorisation_FinalisedConsentStatus() {
        when(aspspProfileService.isPaymentCancellationAuthorizationMandated()).thenReturn(Boolean.TRUE);
        when(pisPsuDataService.getPsuDataByPaymentId(PAYMENT_ID))
            .thenReturn(Collections.singletonList(PSU_ID_DATA));
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(PAYMENT_ID))
            .thenReturn(getFinalisedPisCommonPayment());
        doReturn(Optional.of(spiPayment))
            .when(spiPaymentFactory).createSpiPaymentByPaymentType(eq(getFinalisedPisPayment()), eq("sepa-credit-transfers"), any(PaymentType.class));

        // When
        ResponseObject<CancelPaymentResponse> actual = paymentService.cancelPayment(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        // Then
        assertThat(actual.getError()).isNotNull();
    }

    @Test
    public void cancelPayment_Fail_WithoutAuthorisation_FinalisedConsentStatus() {
        when(aspspProfileService.isPaymentCancellationAuthorizationMandated()).thenReturn(Boolean.FALSE);
        when(pisPsuDataService.getPsuDataByPaymentId(PAYMENT_ID))
            .thenReturn(Collections.singletonList(PSU_ID_DATA));
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(PAYMENT_ID))
            .thenReturn(getFinalisedPisCommonPayment());
        doReturn(Optional.of(spiPayment))
            .when(spiPaymentFactory).createSpiPaymentByPaymentType(eq(getFinalisedPisPayment()), eq("sepa-credit-transfers"), any(PaymentType.class));

        // When
        ResponseObject<CancelPaymentResponse> actual = paymentService.cancelPayment(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        // Then
        assertThat(actual.getError()).isNotNull();
    }

    @Test
    public void cancelPayment_Success_ShouldRecordEvent() {
        when(aspspProfileService.isPaymentCancellationAuthorizationMandated()).thenReturn(Boolean.FALSE);
        when(pisPsuDataService.getPsuDataByPaymentId(PAYMENT_ID))
            .thenReturn(Collections.singletonList(PSU_ID_DATA));
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisPayment.getTransactionStatus()).thenReturn(TransactionStatus.ACCP);
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("sepa-credit-transfers");
        doReturn(Optional.of(spiPayment))
            .when(spiPaymentFactory).createSpiPaymentByPaymentType(eq(Collections.singletonList(pisPayment)), eq("sepa-credit-transfers"), any(PaymentType.class));

        // Given:
        ArgumentCaptor<EventType> argumentCaptor = ArgumentCaptor.forClass(EventType.class);

        // When
        paymentService.cancelPayment(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        // Then
        verify(xs2aEventService, times(1)).recordPisTppRequest(eq(PAYMENT_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(EventType.PAYMENT_CANCELLATION_REQUEST_RECEIVED);
    }

    @Test
    public void createPayment_Success_ShouldRecordEvent() {
        // Given:
        PaymentInitiationParameters parameters = buildPaymentInitiationParameters(PaymentType.SINGLE);
        ArgumentCaptor<EventType> argumentCaptor = ArgumentCaptor.forClass(EventType.class);

        // When
        paymentService.createPayment(SINGLE_PAYMENT_OK, parameters);

        // Then
        verify(xs2aEventService, times(1)).recordTppRequest(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue()).isEqualTo(EventType.PAYMENT_INITIATION_REQUEST_RECEIVED);
    }

    @Test
    public void getPaymentById_Success_ShouldRecordEvent() {
        when(readPaymentService.getPayment(any(), any(), any(), any()))
            .thenReturn(new PaymentInformationResponse(SINGLE_PAYMENT_OK));
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString()))
            .thenReturn(Optional.of(pisCommonPaymentResponse));
        when(cmsToXs2aPaymentMapper.mapToXs2aCommonPayment(pisCommonPaymentResponse))
            .thenReturn(new CommonPayment());

        // Given:
        ArgumentCaptor<EventType> argumentCaptor = ArgumentCaptor.forClass(EventType.class);

        // When
        paymentService.getPaymentById(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        verify(xs2aEventService, times(1)).recordPisTppRequest(eq(PAYMENT_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(EventType.GET_PAYMENT_REQUEST_RECEIVED);
    }

    @Test
    public void getPaymentById_spi_fail() {
        // Given:
        TppMessageInformation errorMessages = of(RESOURCE_UNKNOWN_404);
        Optional<PisCommonPaymentResponse> pisCommonPaymentOptional = getPisCommonPayment();

        PaymentInformationResponse<SinglePayment> errorResponse = new PaymentInformationResponse<>(
            ErrorHolder.builder(RESOURCE_UNKNOWN_404)
                .messages(ERROR_MESSAGE_TEXT)
                .build());

        when(readPaymentService.getPayment(any(), any(), any(), any()))
            .thenReturn(errorResponse);
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString()))
            .thenReturn(Optional.empty());
        when(cmsToXs2aPaymentMapper.mapToXs2aCommonPayment(pisCommonPaymentOptional.get()))
            .thenReturn(new CommonPayment());

        // When
        ResponseObject actualResponse = paymentService.getPaymentById(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        //Then
        assertThat(actualResponse.hasError()).isTrue();
        assertThat(actualResponse.getError().getTppMessage().getMessageErrorCode()).isEqualTo(RESOURCE_UNKNOWN_404);
        assertThat(actualResponse.getError().getTppMessages()).containsOnly(errorMessages);
    }

    @Test
    public void getPaymentStatusById_spi_fail() {
        // Given:
        TppMessageInformation errorMessages = of(RESOURCE_UNKNOWN_404);

        ReadPaymentStatusResponse errorResponse = new ReadPaymentStatusResponse(
            ErrorHolder.builder(RESOURCE_UNKNOWN_404)
                .messages(ERROR_MESSAGE_TEXT)
                .build());

        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString()))
            .thenReturn(Optional.empty());
        when(readPaymentStatusService.readPaymentStatus(any(), any(), any(), any())).thenReturn(errorResponse);

        // When
        ResponseObject actualResponse = paymentService.getPaymentStatusById(PaymentType.SINGLE, PAYMENT_PRODUCT, PAYMENT_ID);

        // Then
        //Then
        assertThat(actualResponse.hasError()).isTrue();
        assertThat(actualResponse.getError().getTppMessage().getMessageErrorCode()).isEqualTo(RESOURCE_UNKNOWN_404);
        assertThat(actualResponse.getError().getTppMessages()).containsOnly(errorMessages);
    }

    @Test
    public void getPaymentStatusById_Success_ShouldRecordEvent() {
        SpiResponse<TransactionStatus> spiResponse = buildSpiResponseTransactionStatus();
        when(singlePaymentSpi.getPaymentStatusById(any(), any(), any())).thenReturn(spiResponse);
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("sepa-credit-transfers");
        when(readPaymentStatusFactory.getService(anyString())).thenReturn(readPaymentStatusService);
        when(readPaymentStatusService.readPaymentStatus(eq(Collections.singletonList(pisPayment)), eq("sepa-credit-transfers"), any(SpiContextData.class), eq(ASPSP_CONSENT_DATA)))
            .thenReturn(new ReadPaymentStatusResponse(RCVD));
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);
        when(updatePaymentStatusAfterSpiService.updatePaymentStatus(anyString(), any(TransactionStatus.class)))
            .thenReturn(true);

        // Given:
        ArgumentCaptor<EventType> argumentCaptor = ArgumentCaptor.forClass(EventType.class);

        // When
        paymentService.getPaymentStatusById(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        verify(xs2aEventService, times(1)).recordPisTppRequest(eq(PAYMENT_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(EventType.GET_TRANSACTION_STATUS_REQUEST_RECEIVED);
    }

    @Test
    public void createPayment_Failure_No_PSU() {
        when(aspspProfileService.isPsuInInitialRequestMandated()).thenReturn(true);
        //When
        PaymentInitiationParameters parameters = buildPaymentInitiationParameters(PaymentType.SINGLE);
        ResponseObject<BulkPaymentInitiationResponse> actualResponse = paymentService.createPayment(SINGLE_PAYMENT_OK, parameters);
        //Then
        MessageError error = actualResponse.getError();
        assertThat(error).isNotNull();
        assertThat(error.getErrorType()).isEqualTo(ErrorType.PIS_400);
        assertThat(error.getTppMessage().getMessageErrorCode()).isEqualTo(MessageErrorCode.FORMAT_ERROR);
    }

    @Test
    public void getPaymentStatusById_Failure_IncorrectPaymentService() {
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("sepa-credit-transfers");
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.PERIODIC);
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);

        // Given
        ResponseObject expectedResult = ResponseObject.<CancelPaymentResponse>builder()
                                            .fail(PIS_405, of(SERVICE_INVALID_405, "Service invalid for adressed payment"))
                                            .build();

        // When
        ResponseObject actualResult = paymentService.getPaymentStatusById(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        assertThat(actualResult.hasError()).isTrue();
        assertThat(actualResult.getError()).isEqualTo(expectedResult.getError());
    }

    @Test
    public void getPaymentById_Failure_IncorrectPaymentService() {
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("sepa-credit-transfers");
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.PERIODIC);
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);

        // Given
        ResponseObject expectedResult = ResponseObject.<CancelPaymentResponse>builder()
                                            .fail(PIS_405, of(SERVICE_INVALID_405, "Service invalid for adressed payment"))
                                            .build();

        // When
        ResponseObject actualResult = paymentService.getPaymentById(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        assertThat(actualResult.hasError()).isTrue();
        assertThat(actualResult.getError()).isEqualTo(expectedResult.getError());
    }

    @Test
    public void cancelPayment_Failure_IncorrectPaymentService() {
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("sepa-credit-transfers");
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.PERIODIC);
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);

        // Given
        ResponseObject expectedResult = ResponseObject.<CancelPaymentResponse>builder()
                                            .fail(PIS_405, of(SERVICE_INVALID_405, "Service invalid for adressed payment"))
                                            .build();

        // When
        ResponseObject actualResult = paymentService.cancelPayment(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        assertThat(actualResult.hasError()).isTrue();
        assertThat(actualResult.getError()).isEqualTo(expectedResult.getError());
    }

    @Test
    public void getPaymentStatusById_Failure_IncorrectPaymentProduct() {
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("instant-sepa-credit-transfers");
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.PERIODIC);
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);

        // Given
        ResponseObject expectedResult = ResponseObject.<CancelPaymentResponse>builder()
                                            .fail(PIS_405, of(SERVICE_INVALID_405, "Service invalid for adressed payment"))
                                            .build();

        // When
        ResponseObject actualResult = paymentService.getPaymentStatusById(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        assertThat(actualResult.hasError()).isTrue();
        assertThat(actualResult.getError()).isEqualTo(expectedResult.getError());
    }

    @Test
    public void getPaymentById_Failure_IncorrectPaymentProduct() {
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("instant-sepa-credit-transfers");
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.PERIODIC);
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);

        // Given
        ResponseObject expectedResult = ResponseObject.<CancelPaymentResponse>builder()
                                            .fail(PIS_405, of(SERVICE_INVALID_405, "Service invalid for adressed payment"))
                                            .build();

        // When
        ResponseObject actualResult = paymentService.getPaymentById(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        assertThat(actualResult.hasError()).isTrue();
        assertThat(actualResult.getError()).isEqualTo(expectedResult.getError());
    }

    @Test
    public void cancelPayment_Failure_IncorrectPaymentProduct() {
        when(xs2aPisCommonPaymentService.getPisCommonPaymentById(anyString())).thenReturn(Optional.of(pisCommonPaymentResponse));
        when(pisCommonPaymentResponse.getPayments()).thenReturn(Collections.singletonList(pisPayment));
        when(pisCommonPaymentResponse.getPaymentProduct()).thenReturn("instant-sepa-credit-transfers");
        when(pisCommonPaymentResponse.getPaymentType()).thenReturn(PaymentType.PERIODIC);
        doNothing().when(pisAspspDataService).updateAspspConsentData(ASPSP_CONSENT_DATA);

        // Given
        ResponseObject expectedResult = ResponseObject.<CancelPaymentResponse>builder()
                                            .fail(PIS_405, of(SERVICE_INVALID_405, "Service invalid for adressed payment"))
                                            .build();

        // When
        ResponseObject actualResult = paymentService.cancelPayment(PaymentType.SINGLE, "sepa-credit-transfers", PAYMENT_ID);

        // Then
        assertThat(actualResult.hasError()).isTrue();
        assertThat(actualResult.getError()).isEqualTo(expectedResult.getError());
    }


    private SpiResponse<TransactionStatus> buildSpiResponseTransactionStatus() {
        return new SpiResponse<>(TransactionStatus.ACCP, ASPSP_CONSENT_DATA);
    }

    private static SinglePayment getSinglePayment(String iban, String amountToPay) {
        SinglePayment singlePayments = new SinglePayment();
        singlePayments.setEndToEndIdentification(PAYMENT_ID);
        Xs2aAmount amount = new Xs2aAmount();
        amount.setCurrency(CURRENCY);
        amount.setAmount(amountToPay);
        singlePayments.setInstructedAmount(amount);
        singlePayments.setDebtorAccount(getReference(iban));
        singlePayments.setCreditorAccount(getReference(iban));
        singlePayments.setRequestedExecutionDate(LocalDate.now());
        singlePayments.setRequestedExecutionTime(OffsetDateTime.now());
        return singlePayments;
    }

    private static AccountReference getReference(String iban) {
        AccountReference reference = new AccountReference();
        reference.setIban(iban);
        reference.setCurrency(CURRENCY);

        return reference;
    }

    private BulkPaymentInitiationResponse getBulkResponses(TransactionStatus status, MessageErrorCode errorCode) {
        BulkPaymentInitiationResponse response = new BulkPaymentInitiationResponse();
        response.setTransactionStatus(status);

        response.setPaymentId(status == RJCT ? null : PAYMENT_ID);
        if (status == RJCT) {
            response.setTppMessages(new MessageErrorCode[]{errorCode});
        }
        return response;
    }

    private static TppInfo getTppInfo() {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setAuthorisationNumber("registrationNumber");
        tppInfo.setTppName("tppName");
        tppInfo.setTppRoles(Collections.singletonList(TppRole.PISP));
        tppInfo.setAuthorityId("authorityId");
        tppInfo.setAuthorityName("authorityName");
        tppInfo.setCountry("country");
        tppInfo.setOrganisation("organisation");
        tppInfo.setOrganisationUnit("organisationUnit");
        tppInfo.setCity("city");
        tppInfo.setState("state");
        tppInfo.setTppRedirectUri(new TppRedirectUri("redirectUri", "nokRedirectUri"));
        return tppInfo;
    }

    private static TppInfo getTppInfoServiceModified() {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setAuthorisationNumber("registrationNumber");
        tppInfo.setTppName("tppName");
        tppInfo.setTppRoles(Collections.singletonList(TppRole.PISP));
        tppInfo.setAuthorityId("authorityId");
        tppInfo.setAuthorityName("authorityName");
        tppInfo.setCountry("country");
        tppInfo.setOrganisation("organisation");
        tppInfo.setOrganisationUnit("organisationUnit");
        tppInfo.setCity("city");
        tppInfo.setState("state");

        return tppInfo;
    }

    private BulkPayment getBulkPayment(SinglePayment singlePayment1, String iban) {
        BulkPayment bulkPayment = new BulkPayment();
        bulkPayment.setPayments(Collections.singletonList(singlePayment1));
        bulkPayment.setRequestedExecutionDate(LocalDate.now());
        bulkPayment.setDebtorAccount(getReference(iban));
        bulkPayment.setBatchBookingPreferred(false);

        return bulkPayment;
    }

    private Xs2aPisCommonPayment getXs2aPisCommonPayment() {
        return new Xs2aPisCommonPayment("TEST", PSU_ID_DATA);
    }

    private PaymentInitiationParameters buildPaymentInitiationParameters(PaymentType type) {
        PaymentInitiationParameters requestParameters = new PaymentInitiationParameters();
        requestParameters.setPaymentType(type);
        requestParameters.setPaymentProduct("sepa-credit-transfers");
        requestParameters.setPsuData(PSU_ID_DATA);
        return requestParameters;
    }

    private ResponseObject<BulkPaymentInitiationResponse> getValidResponse() {
        return ResponseObject.<BulkPaymentInitiationResponse>builder().body(getBulkResponses(RCVD, null)).build();
    }

    private CancelPaymentResponse getCancelPaymentResponse(boolean authorisationRequired, TransactionStatus transactionStatus) {
        CancelPaymentResponse response = new CancelPaymentResponse();
        response.setStartAuthorisationRequired(authorisationRequired);
        response.setTransactionStatus(transactionStatus);
        return response;
    }

    private Optional<PisCommonPaymentResponse> getPisCommonPayment() {
        PisCommonPaymentResponse response = new PisCommonPaymentResponse();
        response.setPayments(Collections.singletonList(getPisPayment()));
        response.setPaymentProduct(PAYMENT_PRODUCT);
        return Optional.of(response);
    }

    private Optional<PisCommonPaymentResponse> getFinalisedPisCommonPayment() {
        PisCommonPaymentResponse response = new PisCommonPaymentResponse();
        response.setPaymentProduct("sepa-credit-transfers");
        response.setPayments(getFinalisedPisPayment());
        return Optional.of(response);
    }

    private PisPayment getPisPayment() {
        PisPayment pisPayment = new PisPayment();
        pisPayment.setTransactionStatus(TransactionStatus.ACCP);
        return pisPayment;
    }

    private List<PisPayment> getFinalisedPisPayment() {
        PisPayment pisPayment = new PisPayment();
        pisPayment.setTransactionStatus(TransactionStatus.RJCT);
        return Collections.singletonList(pisPayment);
    }
}
*/
