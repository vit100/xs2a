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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EncryptionDecryptionService {
    private final SecurityDataService securityDataService;

    public Optional<String> encryptConsentId(@NotNull String consentId) {
        Optional<String> encryptedConsentId = securityDataService.encryptId(consentId);
        if (!encryptedConsentId.isPresent()) {
            log.warn("Couldn't encrypt consent ID: {}", consentId);
        }

        return encryptedConsentId;
    }

    public Optional<String> decryptConsentId(@NotNull String encryptedConsentId) {
        Optional<String> decryptedId = securityDataService.decryptId(encryptedConsentId);
        if (!decryptedId.isPresent()) {
            log.warn("Couldn't decrypt consent ID: {}", encryptedConsentId);
        }

        return decryptedId;
    }

    public Optional<String> encryptPaymentId(@NotNull String paymentId) {
        Optional<String> encryptedPaymentId = securityDataService.encryptId(paymentId);
        if (!encryptedPaymentId.isPresent()) {
            log.warn("Couldn't encrypt payment ID: {}", paymentId);
        }

        return encryptedPaymentId;
    }

    public Optional<String> decryptPaymentId(@NotNull String encryptedPaymentId) {
        Optional<String> decryptedId = securityDataService.decryptId(encryptedPaymentId);
        if (!decryptedId.isPresent()) {
            log.warn("Couldn't decrypt payment ID: {}", encryptedPaymentId);
        }

        return decryptedId;
    }
}
