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

import de.adorsys.psd2.consent.service.security.SecurityDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EncryptionDecryptionServiceTest {
    private static final String ENCRYPTED_CONSENT_ID = "encrypted consent id";
    private static final String UNDECRYPTABLE_CONSENT_ID = "undecryptable consent id";
    private static final String CONSENT_ID = "255574b2-f115-4f3c-8d77-c1897749c060";
    private static final String UNENCRYPTABLE_CONSENT_ID = "unencryptable consent id";

    private static final String ENCRYPTED_PAYMENT_ID = "encrypted payment id";
    private static final String UNDECRYPTABLE_PAYMENT_ID = "undecryptable payment id";
    private static final String PAYMENT_ID = "91cd2158-4344-44f4-bdbb-c736ededa436";
    private static final String UNENCRYPTABLE_PAYMENT_ID = "unencryptable consent id";

    @InjectMocks
    private EncryptionDecryptionService encryptionDecryptionService;
    @Mock
    private SecurityDataService securityDataService;

    @Before
    public void setUp() {
        when(securityDataService.encryptId(CONSENT_ID)).thenReturn(Optional.of(ENCRYPTED_CONSENT_ID));
        when(securityDataService.encryptId(UNENCRYPTABLE_CONSENT_ID)).thenReturn(Optional.empty());
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        when(securityDataService.decryptId(UNDECRYPTABLE_CONSENT_ID)).thenReturn(Optional.empty());

        when(securityDataService.encryptId(PAYMENT_ID)).thenReturn(Optional.of(ENCRYPTED_PAYMENT_ID));
        when(securityDataService.encryptId(UNENCRYPTABLE_PAYMENT_ID)).thenReturn(Optional.empty());
        when(securityDataService.decryptId(ENCRYPTED_PAYMENT_ID)).thenReturn(Optional.of(PAYMENT_ID));
        when(securityDataService.decryptId(UNDECRYPTABLE_PAYMENT_ID)).thenReturn(Optional.empty());
    }

    @Test
    public void encryptConsentId_success() {
        // When
        Optional<String> actual = encryptionDecryptionService.encryptConsentId(CONSENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(ENCRYPTED_CONSENT_ID, actual.get());
    }

    @Test
    public void encryptConsentId_failure() {
        // When
        Optional<String> actual = encryptionDecryptionService.encryptConsentId(UNENCRYPTABLE_CONSENT_ID);

        // Then
        assertFalse(actual.isPresent());
    }

    @Test
    public void decryptConsentId_success() {
        // When
        Optional<String> actual = encryptionDecryptionService.decryptConsentId(ENCRYPTED_CONSENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(CONSENT_ID, actual.get());
    }

    @Test
    public void decryptConsentId_failure() {
        // When
        Optional<String> actual = encryptionDecryptionService.decryptConsentId(UNDECRYPTABLE_CONSENT_ID);

        // Then
        assertFalse(actual.isPresent());
    }

    @Test
    public void encryptPaymentId_success() {
        // When
        Optional<String> actual = encryptionDecryptionService.encryptPaymentId(PAYMENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(ENCRYPTED_PAYMENT_ID, actual.get());
    }

    @Test
    public void encryptPaymentId_failure() {
        // When
        Optional<String> actual = encryptionDecryptionService.encryptPaymentId(UNENCRYPTABLE_PAYMENT_ID);

        // Then
        assertFalse(actual.isPresent());
    }

    @Test
    public void decryptPaymentId_success() {
        // When
        Optional<String> actual = encryptionDecryptionService.decryptPaymentId(ENCRYPTED_PAYMENT_ID);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(PAYMENT_ID, actual.get());
    }

    @Test
    public void decryptPaymentId_failure() {
        // When
        Optional<String> actual = encryptionDecryptionService.decryptPaymentId(UNDECRYPTABLE_PAYMENT_ID);

        // Then
        assertFalse(actual.isPresent());
    }
}
