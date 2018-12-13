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

    /**
     * Encrypts given id of payment or consent
     *
     * @param id id to encrypt
     * @return Encrypted id if it was successfully encrypted
     */
    public Optional<String> encryptId(@NotNull String id) {
        Optional<String> encryptedConsentId = securityDataService.encryptId(id);
        if (!encryptedConsentId.isPresent()) {
            log.warn("Couldn't encrypt ID: {}", id);
        }

        return encryptedConsentId;
    }

    /**
     * Decrypts given encrypted id of payment or consent
     *
     * @param encryptedId id to decrypt
     * @return Original id if it was successfully decrypted
     */
    public Optional<String> decryptId(@NotNull String encryptedId) {
        Optional<String> decryptedId = securityDataService.decryptId(encryptedId);
        if (!decryptedId.isPresent()) {
            log.warn("Couldn't decrypt ID: {}", encryptedId);
        }

        return decryptedId;
    }
}
