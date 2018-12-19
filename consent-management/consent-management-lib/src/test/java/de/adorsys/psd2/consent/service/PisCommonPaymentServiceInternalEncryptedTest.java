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



package de.adorsys.psd2.consent.service;


import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.PisAuthorizationRepository;
import de.adorsys.psd2.consent.repository.PisCommonPaymentDataRepository;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.consent.service.security.EncryptedData;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PisCommonPaymentServiceInternalEncryptedTest {

    @InjectMocks
    private PisCommonPaymentServiceInternal pisCommonPaymentService;
    @Mock
    private PisPaymentDataRepository pisPaymentDataRepository;
    @Mock
    private PisAuthorizationRepository pisAuthorizationRepository;
    @Mock
    SecurityDataService securityDataService;
    @Mock
    private PisCommonPaymentDataRepository pisCommonPaymentDataRepository;

    private PisCommonPaymentData pisCommonPaymentData;
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


    private final long PIS_PAYMENT_DATA_ID = 1;
    private final String EXTERNAL_ID = "4b112130-6a96-4941-a220-2da8a4af2c65";
    private final String EXTERNAL_ID_NOT_EXIST = "4b112130-6a96-4941-a220-2da8a4af2c63";
    private final String paymentId = "5bbde955ca10e8e4035a10c2";
    private final String paymentIdWrong = "5bbdcb28ca10e8e14a41b12f";
    private static final byte[] ENCRYPTED_CONSENT_DATA = "test data".getBytes();
    private PisPaymentData pisPaymentData;
    private List<PisAuthorization> pisAuthorizationList = new ArrayList<>();
    private CmsAspspConsentDataBase64 cmsAspspConsentDataBase64;
    private static final String FINALISED_AUTHORISATION_ID = "9b112130-6a96-4941-a220-2da8a4af2c65";
    private static final String FINALISED_CANCELLATION_AUTHORISATION_ID = "2a112130-6a96-4941-a220-2da8a4af2c65";

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

        cmsAspspConsentDataBase64 = buildUpdateBlobRequest();
        pisCommonPaymentData = buildPisCommonPaymentData();
        pisPaymentData = buildPaymentData(pisCommonPaymentData);
        pisAuthorizationList.add(buildPisCommonPaymentAuthorisation(EXTERNAL_ID));
        when(securityDataService.decryptId(EXTERNAL_ID)).thenReturn(Optional.of(EXTERNAL_ID));
        when(securityDataService.decryptId(EXTERNAL_ID_NOT_EXIST)).thenReturn(Optional.of(EXTERNAL_ID_NOT_EXIST));
        when(securityDataService.encryptConsentData(EXTERNAL_ID, cmsAspspConsentDataBase64.getAspspConsentDataBase64()))
            .thenReturn(Optional.of(new EncryptedData(ENCRYPTED_CONSENT_DATA)));
    }

    @Test
    public void getAuthorisationByPaymentIdSuccess() {
        //When
        when(securityDataService.decryptId(paymentId)).thenReturn(Optional.of(paymentId));
        when(pisPaymentDataRepository.findByPaymentIdAndPaymentDataTransactionStatus(paymentId, TransactionStatus.RCVD)).thenReturn(Optional.of(Collections.singletonList(pisPaymentData)));
        //Then
        Optional<List<String>> authorizationByPaymentId = pisCommonPaymentService.getAuthorisationsByPaymentId(paymentId, CmsAuthorisationType.CANCELLED);
        //Assert
        assertTrue(authorizationByPaymentId.isPresent());
        assertEquals(authorizationByPaymentId.get().size(), pisAuthorizationList.size());
        assertEquals(authorizationByPaymentId.get().get(0), pisAuthorizationList.get(0).getExternalId());
    }

    @Test
    public void getAuthorisationByPaymentIdWrongPaymentId() {
        //When
        when(securityDataService.decryptId(paymentIdWrong)).thenReturn(Optional.empty());
        when(pisPaymentDataRepository.findByPaymentIdAndPaymentDataTransactionStatus(paymentIdWrong, TransactionStatus.RCVD)).thenReturn(Optional.empty());
        when(pisCommonPaymentDataRepository.findByPaymentIdAndTransactionStatus(paymentIdWrong, TransactionStatus.RCVD)).thenReturn(Optional.empty());
        //Then
        Optional<List<String>> authorizationByPaymentId = pisCommonPaymentService.getAuthorisationsByPaymentId(paymentIdWrong, CmsAuthorisationType.CANCELLED);
        //Assert
        assertFalse(authorizationByPaymentId.isPresent());
    }

    @Test
    public void updateConsentAuthorisation_FinalisedStatus_Fail() {
        //Given
        ScaStatus expectedScaStatus = ScaStatus.STARTED;
        ScaStatus actualScaStatus = ScaStatus.FINALISED;

        UpdatePisCommonPaymentPsuDataRequest updatePisCommonPaymentPsuDataRequest = buildUpdatePisCommonPaymentPsuDataRequest(expectedScaStatus);
        PisAuthorization finalisedConsentAuthorization = buildFinalisedConsentAuthorisation(actualScaStatus);

        when(pisAuthorizationRepository.findByExternalIdAndAuthorizationType(FINALISED_AUTHORISATION_ID, CmsAuthorisationType.CREATED))
            .thenReturn(Optional.of(finalisedConsentAuthorization));

        //When
        Optional<UpdatePisCommonPaymentPsuDataResponse> updatePisCommonPaymentPsuDataResponse = pisCommonPaymentService.updatePisAuthorisation(FINALISED_AUTHORISATION_ID, updatePisCommonPaymentPsuDataRequest);

        //Then
        assertTrue(updatePisCommonPaymentPsuDataResponse.isPresent());
        assertNotEquals(updatePisCommonPaymentPsuDataResponse.get().getScaStatus(), expectedScaStatus);
    }

    @Test
    public void updateConsentCancellationAuthorisation_FinalisedStatus_Fail() {
        //Given
        ScaStatus expectedScaStatus = ScaStatus.STARTED;
        ScaStatus actualScaStatus = ScaStatus.FINALISED;

        PisAuthorization finalisedCancellationAuthorization = buildFinalisedConsentAuthorisation(actualScaStatus);
        UpdatePisCommonPaymentPsuDataRequest updatePisCommonPaymentPsuDataRequest = buildUpdatePisCommonPaymentPsuDataRequest(expectedScaStatus);

        when(pisAuthorizationRepository.findByExternalIdAndAuthorizationType(FINALISED_CANCELLATION_AUTHORISATION_ID, CmsAuthorisationType.CANCELLED))
            .thenReturn(Optional.of(finalisedCancellationAuthorization));

        //When
        Optional<UpdatePisCommonPaymentPsuDataResponse> updatePisCommonPaymentPsuDataResponse = pisCommonPaymentService.updatePisCancellationAuthorisation(FINALISED_CANCELLATION_AUTHORISATION_ID, updatePisCommonPaymentPsuDataRequest);

        //Then
        assertTrue(updatePisCommonPaymentPsuDataResponse.isPresent());
        assertNotEquals(updatePisCommonPaymentPsuDataResponse.get().getScaStatus(), expectedScaStatus);

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
        paymentData.setPaymentId(paymentId);
        paymentData.setPaymentData(pisCommonPaymentData);
        return paymentData;
    }
}

