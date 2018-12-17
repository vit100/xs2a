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

package de.adorsys.psd2.consent.web.xs2a;

import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.PisCommonPaymentDataStatusResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisCommonPaymentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.consent.api.pis.proto.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentService;
import de.adorsys.psd2.consent.web.xs2a.controller.PisCommonPaymentController;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PisCommonPaymentControllerTest {

    private static final String AUTHORISATION_ID = "345-9245-2359";
    private static final String PAYMENT_ID = "33333-999999999";
    private static final String CONSENT_ID = "12345";
    private static final String STATUS_RECEIVED = "RECEIVED";
    private static final String PSU_ID = "testPSU";
    private static final String PASSWORD = "password";

    private static final String WRONG_AUTHORISATION_ID = "3254890-5";
    private static final String WRONG_PAYMENT_ID = "32343-999997777";
    private static final String WRONG_CONSENT_ID = "67890";

    private static final PsuIdData PSU_DATA = new PsuIdData(PSU_ID, null, null, null);

    @InjectMocks
    private PisCommonPaymentController pisCommonPaymentController;

    @Mock
    private PisCommonPaymentService pisCommonPaymentService;

    @Before
    public void setUp() {
        when(pisCommonPaymentService.createCommonPayment(getPisConsentRequest())).thenReturn(Optional.of(getCreatePisConsentResponse()));
        when(pisCommonPaymentService.getPisCommonPaymentStatusById(CONSENT_ID)).thenReturn(Optional.of(RECEIVED));
        when(pisCommonPaymentService.getConsentById(CONSENT_ID)).thenReturn(Optional.of(getPisConsentResponse()));
        when(pisCommonPaymentService.updateCommonPaymentStatusById(CONSENT_ID, RECEIVED)).thenReturn(Optional.of(Boolean.TRUE));
        when(pisCommonPaymentService.createAuthorization(PAYMENT_ID, CmsAuthorisationType.CREATED, PSU_DATA)).thenReturn(Optional.of(getCreatePisConsentAuthorisationResponse()));
        when(pisCommonPaymentService.updateCommonPaymentAuthorisation(AUTHORISATION_ID, getUpdatePisConsentPsuDataRequest())).thenReturn(Optional.of(getUpdatePisConsentPsuDataResponse()));
    }

    @Test
    public void createPaymentConsent_Success() {
        //Given
        ResponseEntity<CreatePisCommonPaymentResponse> expected = new ResponseEntity<>(new CreatePisCommonPaymentResponse(CONSENT_ID), HttpStatus.CREATED);

        //When
        ResponseEntity<CreatePisCommonPaymentResponse> actual = pisCommonPaymentController.createCommonPayment(getPisConsentRequest());

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void createPaymentConsent_Failure() {
        //Given
        when(pisCommonPaymentService.createCommonPayment(getPisConsentRequest())).thenReturn(Optional.empty());
        ResponseEntity<CreatePisCommonPaymentResponse> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //When
        ResponseEntity<CreatePisCommonPaymentResponse> actual = pisCommonPaymentController.createCommonPayment(getPisConsentRequest());

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void getConsentStatusById_Success() {
        //Given
        ResponseEntity<PisCommonPaymentDataStatusResponse> expected = new ResponseEntity<>(new PisCommonPaymentDataStatusResponse(RECEIVED), HttpStatus.OK);

        //When
        ResponseEntity<PisCommonPaymentDataStatusResponse> actual = pisCommonPaymentController.getPisCommonPaymentStatusById(CONSENT_ID);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void getConsentStatusById_Failure() {
        //Given
        when(pisCommonPaymentService.getPisCommonPaymentStatusById(WRONG_CONSENT_ID)).thenReturn(Optional.empty());
        ResponseEntity<PisCommonPaymentDataStatusResponse> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //When
        ResponseEntity<PisCommonPaymentDataStatusResponse> actual = pisCommonPaymentController.getPisCommonPaymentStatusById(WRONG_CONSENT_ID);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void getConsentById_Success() {
        //Given
        ResponseEntity<PisConsentResponse> expected = new ResponseEntity<>(new PisConsentResponse(), HttpStatus.OK);

        //When
        ResponseEntity<PisConsentResponse> actual = pisCommonPaymentController.getCommonPaymentById(CONSENT_ID);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void getConsentById_Failure() {
        //Given
        when(pisCommonPaymentService.getConsentById(WRONG_CONSENT_ID)).thenReturn(Optional.empty());
        ResponseEntity<PisConsentResponse> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //When
        ResponseEntity<PisConsentResponse> actual = pisCommonPaymentController.getCommonPaymentById(WRONG_CONSENT_ID);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void updateConsentStatus_Success() {
        //Given
        ResponseEntity<Void> expected = new ResponseEntity<>(HttpStatus.OK);

        //When
        ResponseEntity<Void> actual = pisCommonPaymentController.updateCommonPaymentStatus(CONSENT_ID, STATUS_RECEIVED);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void updateConsentStatus_Failure() {
        //Given
        when(pisCommonPaymentService.updateCommonPaymentStatusById(WRONG_CONSENT_ID, RECEIVED)).thenReturn(Optional.empty());
        ResponseEntity<Void> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //Then
        ResponseEntity<Void> actual = pisCommonPaymentController.updateCommonPaymentStatus(WRONG_CONSENT_ID, STATUS_RECEIVED);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void createConsentAuthorization_Success() {
        //Given
        ResponseEntity<CreatePisAuthorisationResponse> expected = new ResponseEntity<>(new CreatePisAuthorisationResponse(AUTHORISATION_ID), HttpStatus.CREATED);

        //When
        ResponseEntity<CreatePisAuthorisationResponse> actual = pisCommonPaymentController.createAuthorization(PAYMENT_ID, PSU_DATA);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void createConsentAuthorization_Failure() {
        //Given
        when(pisCommonPaymentService.createAuthorization(WRONG_PAYMENT_ID, CmsAuthorisationType.CREATED, PSU_DATA)).thenReturn(Optional.empty());
        ResponseEntity<CreatePisAuthorisationResponse> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //When
        ResponseEntity<CreatePisAuthorisationResponse> actual = pisCommonPaymentController.createAuthorization(WRONG_PAYMENT_ID, PSU_DATA);

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void updateConsentAuthorization_Success() {
        //Given
        ResponseEntity<UpdatePisCommonPaymentPsuDataResponse> expected =
            new ResponseEntity<>(new UpdatePisCommonPaymentPsuDataResponse(ScaStatus.RECEIVED), HttpStatus.OK);

        //When
        ResponseEntity<UpdatePisCommonPaymentPsuDataResponse> actual = pisCommonPaymentController.updateAuthorization(AUTHORISATION_ID, getUpdatePisConsentPsuDataRequest());

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void updateConsentAuthorization_Failure() {
        //Given
        when(pisCommonPaymentService.updateCommonPaymentAuthorisation(WRONG_AUTHORISATION_ID, getUpdatePisConsentPsuDataRequest())).thenReturn(Optional.empty());
        ResponseEntity<UpdatePisCommonPaymentPsuDataResponse> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //When
        ResponseEntity<UpdatePisCommonPaymentPsuDataResponse> actual = pisCommonPaymentController.updateAuthorization(WRONG_AUTHORISATION_ID, getUpdatePisConsentPsuDataRequest());

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void getConsentAuthorization_Success() {
        GetPisCommonPaymentAuthorisationResponse response = getGetPisConsentAuthorisationResponse();
        when(pisCommonPaymentService.getPisCommonPaymentAuthorisationById(any())).thenReturn(Optional.of(response));

        // Given
        GetPisCommonPaymentAuthorisationResponse expectedResponse = getGetPisConsentAuthorisationResponse();

        // When
        ResponseEntity<GetPisCommonPaymentAuthorisationResponse> result =
            pisCommonPaymentController.getAuthorization(AUTHORISATION_ID);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    public void getConsentAuthorization_Failure() {
        when(pisCommonPaymentService.getPisCommonPaymentAuthorisationById(any())).thenReturn(Optional.empty());

        // When
        ResponseEntity<GetPisCommonPaymentAuthorisationResponse> result =
            pisCommonPaymentController.getAuthorization(AUTHORISATION_ID);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNull();
    }

    private GetPisCommonPaymentAuthorisationResponse getGetPisConsentAuthorisationResponse() {
        GetPisCommonPaymentAuthorisationResponse response = new GetPisCommonPaymentAuthorisationResponse();
        response.setPsuId(PSU_ID);
        response.setScaStatus(ScaStatus.STARTED);
        response.setPaymentId(CONSENT_ID);
        response.setPassword(PASSWORD);
        response.setPayments(Collections.emptyList());
        response.setPaymentType(PaymentType.SINGLE);
        return response;
    }

    private PisConsentRequest getPisConsentRequest() {
        return new PisConsentRequest();
    }

    private CreatePisCommonPaymentResponse getCreatePisConsentResponse() {
        return new CreatePisCommonPaymentResponse(CONSENT_ID);
    }

    private PisConsentResponse getPisConsentResponse() {
        return new PisConsentResponse();
    }

    private CreatePisAuthorisationResponse getCreatePisConsentAuthorisationResponse() {
        return new CreatePisAuthorisationResponse(AUTHORISATION_ID);
    }

    private UpdatePisCommonPaymentPsuDataRequest getUpdatePisConsentPsuDataRequest() {
        UpdatePisCommonPaymentPsuDataRequest request = new UpdatePisCommonPaymentPsuDataRequest();
        request.setPaymentId(PAYMENT_ID);
        return request;
    }

    private UpdatePisCommonPaymentPsuDataResponse getUpdatePisConsentPsuDataResponse() {
        return new UpdatePisCommonPaymentPsuDataResponse(ScaStatus.RECEIVED);
    }
}
