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

package de.adorsys.psd2.consent.server.web;

import de.adorsys.psd2.consent.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.api.piis.CreatePiisConsentResponse;
import de.adorsys.psd2.consent.server.service.PiisConsentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PiisConsentControllerTest {
    private static final String CONSENT_ID = "5bcf664f-68ce-498d-9a93-fe0cce32f6b6";

    @Mock
    private PiisConsentService piisConsentService;

    @InjectMocks
    private PiisConsentController piisConsentController;

    @Before
    public void setUp() {
        when(piisConsentService.createConsent(any(CreatePiisConsentRequest.class))).thenReturn(Optional.of(CONSENT_ID));
    }

    @Test
    public void createConsent_Success() {
        //Given
        ResponseEntity<CreatePiisConsentResponse> expected =
            new ResponseEntity<>(new CreatePiisConsentResponse(CONSENT_ID), HttpStatus.CREATED);

        //When
        ResponseEntity<CreatePiisConsentResponse> actual = piisConsentController.createConsent(getCreatePiisConsentRequest());

        //Then
        assertEquals(actual, expected);
    }

    @Test
    public void createConsent_Failure() {
        when(piisConsentService.createConsent(getCreatePiisConsentRequest())).thenReturn(Optional.empty());

        //Given
        ResponseEntity<CreatePiisConsentResponse> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //When
        ResponseEntity<CreatePiisConsentResponse> actual = piisConsentController.createConsent(getCreatePiisConsentRequest());

        //Then
        assertEquals(actual, expected);
    }

    private CreatePiisConsentRequest getCreatePiisConsentRequest() {
        return new CreatePiisConsentRequest();
    }
}
