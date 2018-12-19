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
 *//*



package de.adorsys.psd2.consent.service;

import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.consent.api.pis.proto.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisPaymentInfo;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentService;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PisConsentServiceInternalEncryptedTest {
    private static final String ENCRYPTED_CONSENT_ID = "encrypted consent id";
    private static final String UNDECRYPTABLE_CONSENT_ID = "undecryptable consent id";
    private static final String DECRYPTED_COMMON_PAYMENT_ID = "255574b2-f115-4f3c-8d77-c1897749c060";
    private static final TransactionStatus TRANSACTION_STATUS = TransactionStatus.RCVD;
    private static final ScaStatus SCA_STATUS = ScaStatus.SCAMETHODSELECTED;

    private static final String ENCRYPTED_PAYMENT_ID = "encrypted payment id";
    private static final String UNDECRYPTABLE_PAYMENT_ID = "undecryptable payment id";
    private static final String DECRYPTED_PAYMENT_ID = "1856e4fa-8af8-427b-85ec-4caf515ce074";
    private static final CmsAuthorisationType AUTHORISATION_TYPE = CmsAuthorisationType.CREATED;
    private static final String AUTHORISATION_ID = "46f2e3a7-1855-4815-8755-5ca76769a1a4";

    @InjectMocks
    private PisCommonPaymentServiceInternalEncrypted pisConsentServiceInternalEncrypted;
    @Mock
    private PisCommonPaymentService pisConsentService;
    @Mock
    private SecurityDataService securityDataService;

    @Before
    public void setUp() {
        when(securityDataService.encryptId(DECRYPTED_COMMON_PAYMENT_ID))
            .thenReturn(Optional.of(ENCRYPTED_CONSENT_ID));
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID))
            .thenReturn(Optional.of(DECRYPTED_COMMON_PAYMENT_ID));
        when(securityDataService.decryptId(UNDECRYPTABLE_CONSENT_ID))
            .thenReturn(Optional.empty());
        when(securityDataService.encryptId(DECRYPTED_PAYMENT_ID))
            .thenReturn(Optional.of(ENCRYPTED_PAYMENT_ID));
        when(securityDataService.decryptId(ENCRYPTED_PAYMENT_ID))
            .thenReturn(Optional.of(DECRYPTED_PAYMENT_ID));
        when(securityDataService.decryptId(UNDECRYPTABLE_PAYMENT_ID))
            .thenReturn(Optional.empty());

        when(pisConsentService.createCommonPayment(buildPisPaymentInfoRequest()))
            .thenReturn(Optional.of(buildCreatePisCommonPaymentResponse(DECRYPTED_COMMON_PAYMENT_ID)));
        when(pisConsentService.getPisCommonPaymentStatusById(DECRYPTED_COMMON_PAYMENT_ID))
            .thenReturn(Optional.of(TRANSACTION_STATUS));
        when(pisConsentService.getCommonPaymentById(DECRYPTED_COMMON_PAYMENT_ID))
            .thenReturn(Optional.of(buildPisCommonPaymentResponse(DECRYPTED_COMMON_PAYMENT_ID)));
        when(pisConsentService.updateCommonPaymentStatusById(DECRYPTED_COMMON_PAYMENT_ID, TRANSACTION_STATUS))
            .thenReturn(Optional.of(true));
        when(pisConsentService.createAuthorization(DECRYPTED_PAYMENT_ID, AUTHORISATION_TYPE, buildPsuIdData()))
            .thenReturn(Optional.of(buildCreatePisConsentAuthorisationResponse()));
        when(pisConsentService.createAuthorizationCancellation(DECRYPTED_PAYMENT_ID, AUTHORISATION_TYPE, buildPsuIdData()))
            .thenReturn(Optional.of(buildCreatePisConsentAuthorisationResponse()));
        when(pisConsentService.updatePisAuthorisation(AUTHORISATION_ID, buildUpdatePisCommonPaymentPsuDataRequest()))
            .thenReturn(Optional.of(buildUpdatePisConsentPsuDataResponse()));
        when(pisConsentService.updatePisCancellationAuthorisation(AUTHORISATION_ID, buildUpdatePisCommonPaymentPsuDataRequest()))
            .thenReturn(Optional.of(buildUpdatePisConsentPsuDataResponse()));
        when(pisConsentService.getPisAuthorisationById(AUTHORISATION_ID))
            .thenReturn(Optional.of(buildGetPisConsentAuthorisationResponse()));
        when(pisConsentService.getPisCancellationAuthorisationById(AUTHORISATION_ID))
            .thenReturn(Optional.of(buildGetPisConsentAuthorisationResponse()));
        when(pisConsentService.getAuthorisationsByPaymentId(DECRYPTED_PAYMENT_ID, CmsAuthorisationType.CREATED))
            .thenReturn(Optional.of(buildPaymentAuthorisations()));
        when(pisConsentService.getPsuDataListByPaymentId(DECRYPTED_PAYMENT_ID))
            .thenReturn(Optional.of(buildPsuIdData()));
    }

    @Test
    public void createPaymentConsent_success() {
        // Given
        PisConsentRequest request = buildPisPaymentInfoRequest();
        CreatePisConsentResponse expected = buildCreatePisCommonPaymentResponse(ENCRYPTED_CONSENT_ID);

        // When
        Optional<CreatePisConsentResponse> actual = pisConsentServiceInternalEncrypted.createPaymentConsent(request);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1)).createPaymentConsent(request);
    }

    @Test
    public void getConsentStatusById_success() {
        // When
        Optional<ConsentStatus> actual = pisConsentServiceInternalEncrypted.getConsentStatusById(ENCRYPTED_CONSENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(TRANSACTION_STATUS, actual.get());
        verify(pisConsentService, times(1)).getConsentStatusById(DECRYPTED_COMMON_PAYMENT_ID);
    }

    @Test
    public void getConsentById_success() {
        // Given
        PisConsentResponse expected = buildPisCommonPaymentResponse(DECRYPTED_COMMON_PAYMENT_ID);

        // When
        Optional<PisConsentResponse> actual = pisConsentServiceInternalEncrypted.getConsentById(ENCRYPTED_CONSENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1)).getConsentById(DECRYPTED_COMMON_PAYMENT_ID);
    }

    @Test
    public void updateConsentStatusById_success() {
        // When
        Optional<Boolean> actual = pisConsentServiceInternalEncrypted.updateConsentStatusById(ENCRYPTED_CONSENT_ID, TRANSACTION_STATUS);

        // Then
        assertTrue(actual.isPresent());
        assertTrue(actual.get());
        verify(pisConsentService, times(1)).updateConsentStatusById(DECRYPTED_COMMON_PAYMENT_ID, TRANSACTION_STATUS);
    }

    @Test
    public void getDecryptedId_success() {
        // When
        Optional<String> actual = pisConsentServiceInternalEncrypted.getDecryptedId(ENCRYPTED_PAYMENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(DECRYPTED_PAYMENT_ID, actual.get());
    }

    @Test
    public void createAuthorization_success() {
        // Given
        PsuIdData psuIdData = buildPsuIdData();
        CreatePisConsentAuthorisationResponse expected = buildCreatePisConsentAuthorisationResponse();

        // When
        Optional<CreatePisConsentAuthorisationResponse> actual =
            pisConsentServiceInternalEncrypted.createAuthorization(ENCRYPTED_PAYMENT_ID, AUTHORISATION_TYPE, psuIdData);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1))
            .createAuthorization(DECRYPTED_PAYMENT_ID, AUTHORISATION_TYPE, psuIdData);
    }

    @Test
    public void createAuthorizationCancellation_success() {
        // Given
        PsuIdData psuIdData = buildPsuIdData();
        CreatePisConsentAuthorisationResponse expected = buildCreatePisConsentAuthorisationResponse();

        // When
        Optional<CreatePisConsentAuthorisationResponse> actual =
            pisConsentServiceInternalEncrypted.createAuthorizationCancellation(ENCRYPTED_PAYMENT_ID, AUTHORISATION_TYPE, psuIdData);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1))
            .createAuthorizationCancellation(DECRYPTED_PAYMENT_ID, AUTHORISATION_TYPE, psuIdData);
    }

    @Test
    public void updateConsentAuthorisation_success() {
        // Given
        UpdatePisConsentPsuDataRequest request = buildUpdatePisCommonPaymentPsuDataRequest();
        UpdatePisConsentPsuDataResponse expected = buildUpdatePisConsentPsuDataResponse();

        // When
        Optional<UpdatePisConsentPsuDataResponse> actual =
            pisConsentServiceInternalEncrypted.updateConsentAuthorisation(AUTHORISATION_ID, request);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1))
            .updateConsentAuthorisation(AUTHORISATION_ID, request);
    }

    @Test
    public void updateConsentCancellationAuthorisation_success() {
        // Given
        UpdatePisConsentPsuDataRequest request = buildUpdatePisCommonPaymentPsuDataRequest();
        UpdatePisConsentPsuDataResponse expected = buildUpdatePisConsentPsuDataResponse();

        // When
        Optional<UpdatePisConsentPsuDataResponse> actual =
            pisConsentServiceInternalEncrypted.updateConsentCancellationAuthorisation(AUTHORISATION_ID, request);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1))
            .updateConsentCancellationAuthorisation(AUTHORISATION_ID, request);
    }

    @Test
    public void updatePaymentConsent_success() {
        // Given
        PisConsentRequest request = buildPisPaymentInfoRequest();

        // When
        pisConsentServiceInternalEncrypted.updatePaymentConsent(request, ENCRYPTED_CONSENT_ID);

        // Then
        verify(pisConsentService, times(1))
            .updatePaymentConsent(request, DECRYPTED_COMMON_PAYMENT_ID);
    }

    @Test
    public void getPisConsentAuthorisationById_success() {
        // Given
        GetPisConsentAuthorisationResponse expected = buildGetPisConsentAuthorisationResponse();

        // When
        Optional<GetPisConsentAuthorisationResponse> actual =
            pisConsentServiceInternalEncrypted.getPisConsentAuthorisationById(AUTHORISATION_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1)).getPisConsentAuthorisationById(AUTHORISATION_ID);
    }

    @Test
    public void getPisConsentCancellationAuthorisationById_success() {
        // Given
        GetPisConsentAuthorisationResponse expected = buildGetPisConsentAuthorisationResponse();

        // When
        Optional<GetPisConsentAuthorisationResponse> actual =
            pisConsentServiceInternalEncrypted.getPisConsentCancellationAuthorisationById(AUTHORISATION_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1))
            .getPisConsentCancellationAuthorisationById(AUTHORISATION_ID);
    }

    @Test
    public void getAuthorisationsByPaymentId_success() {
        // Given
        List<String> expected = buildPaymentAuthorisations();

        // When
        Optional<List<String>> actual = pisConsentServiceInternalEncrypted.getAuthorisationsByPaymentId(ENCRYPTED_PAYMENT_ID,
                                                                                                        CmsAuthorisationType.CREATED);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1))
            .getAuthorisationsByPaymentId(DECRYPTED_PAYMENT_ID, CmsAuthorisationType.CREATED);
    }

    @Test
    public void getPsuDataByPaymentId_success() {
        // Given
        PsuIdData expected = buildPsuIdData();

        // When
        Optional<PsuIdData> actual = pisConsentServiceInternalEncrypted.getPsuDataByPaymentId(ENCRYPTED_PAYMENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1)).getPsuDataByPaymentId(DECRYPTED_PAYMENT_ID);
    }

    @Test
    public void getPsuDataByConsentId_success() {
        // Given
        PsuIdData expected = buildPsuIdData();

        // When
        Optional<PsuIdData> actual = pisConsentServiceInternalEncrypted.getPsuDataByConsentId(ENCRYPTED_CONSENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(pisConsentService, times(1)).getPsuDataByConsentId(DECRYPTED_COMMON_PAYMENT_ID);
    }

    private UpdatePisCommonPaymentPsuDataRequest buildUpdatePisCommonPaymentPsuDataRequest(ScaStatus status) {
        UpdatePisCommonPaymentPsuDataRequest request = new UpdatePisCommonPaymentPsuDataRequest();
        request.setAuthorizationId(FINALISED_AUTHORISATION_ID);
        request.setScaStatus(status);
        return request;
    }

    private PisAuthorization buildFinalisedConsentAuthorisation(ScaStatus status) {
        PisAuthorization pisAuthorization = new PisAuthorization();
        pisAuthorization.setExternalId(FINALISED_AUTHORISATION_ID);
        pisAuthorization.setScaStatus(status);
        return pisAuthorization;
    }

    private PisCommonPaymentData buildPisCommonPaymentData() {
        PisCommonPaymentData pisCommonPaymentData = new PisCommonPaymentData();
        pisCommonPaymentData.setId(PIS_PAYMENT_DATA_ID);
        pisCommonPaymentData.setTransactionStatus(TransactionStatus.RCVD);
        pisCommonPaymentData.setAuthorizations(pisAuthorizationList);
        return pisCommonPaymentData;
    }

    private CmsAspspConsentDataBase64 buildUpdateBlobRequest() {
        return new CmsAspspConsentDataBase64("encryptedId",
                                             Base64.getEncoder().encodeToString("decrypted consent data".getBytes()));
    }

    private PisAuthorization buildPisCommonPaymentAuthorisation(String externalId) {
        PisAuthorization pisAuthorization = new PisAuthorization();
        pisAuthorization.setExternalId(externalId);
        pisAuthorization.setAuthorizationType(CmsAuthorisationType.CANCELLED);
        return pisAuthorization;
    }

    private PisPaymentData buildPaymentData(PisCommonPaymentData pisCommonPaymentData) {
        PisPaymentData paymentData = new PisPaymentData();
        paymentData.setPaymentId(DECRYPTED_PAYMENT_ID);
        paymentData.setPaymentData(pisCommonPaymentData);
        return paymentData;
    }


    private PisPaymentInfo buildPisPaymentInfoRequest() {
        return new PisPaymentInfo();
    }

    private CreatePisCommonPaymentResponse buildCreatePisCommonPaymentResponse(String id) {
        return new CreatePisCommonPaymentResponse(id);
    }

    private PisCommonPaymentResponse buildPisCommonPaymentResponse(String id) {
        PisCommonPaymentResponse response = new PisCommonPaymentResponse();
        response.setExternalId(id);
        return response;
    }

    private PsuIdData buildPsuIdData() {
        return new PsuIdData(null, null, null, null);
    }

    private CreatePisAuthorisationResponse buildCreatePisConsentAuthorisationResponse() {
        return new CreatePisAuthorisationResponse(AUTHORISATION_ID);
    }

    private UpdatePisCommonPaymentPsuDataRequest buildUpdatePisCommonPaymentPsuDataRequest() {
        UpdatePisCommonPaymentPsuDataRequest request = new UpdatePisCommonPaymentPsuDataRequest();
        request.setAuthorizationId(AUTHORISATION_ID);
        return request;
    }

    private UpdatePisCommonPaymentPsuDataResponse buildUpdatePisConsentPsuDataResponse() {
        return new UpdatePisCommonPaymentPsuDataResponse(SCA_STATUS);
    }

    private GetPisAuthorisationResponse buildGetPisConsentAuthorisationResponse() {
        return new GetPisAuthorisationResponse();
    }

    private List<String> buildPaymentAuthorisations() {
        return Collections.singletonList(AUTHORISATION_ID);
    }
}

*/
