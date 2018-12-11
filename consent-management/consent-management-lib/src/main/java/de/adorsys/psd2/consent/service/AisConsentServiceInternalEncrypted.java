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

import de.adorsys.psd2.consent.api.ais.*;
import de.adorsys.psd2.consent.api.service.AisConsentService;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AisConsentServiceInternalEncrypted implements AisConsentService {
    private final EncryptionDecryptionService encryptionDecryptionService;
    private final AisConsentService aisConsentService;

    @Override
    @Transactional
    public Optional<String> createConsent(CreateAisConsentRequest request) {
        Optional<String> response = aisConsentService.createConsent(request);
        if (!response.isPresent()) {
            return Optional.empty();
        }

        String consentId = response.get();
        return encryptionDecryptionService.encryptConsentId(consentId);
    }

    @Override
    public Optional<ConsentStatus> getConsentStatusById(String encryptedConsentId) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        return aisConsentService.getConsentStatusById(decryptedConsentId.get());
    }

    @Override
    @Transactional
    public boolean updateConsentStatusById(String encryptedConsentId, ConsentStatus status) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return false;
        }

        return aisConsentService.updateConsentStatusById(decryptedConsentId.get(), status);
    }

    @Override
    public Optional<AisAccountConsent> getAisAccountConsentById(String encryptedConsentId) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        return aisConsentService.getAisAccountConsentById(decryptedConsentId.get());
    }

    @Override
    @Transactional
    public void checkConsentAndSaveActionLog(AisConsentActionRequest encryptedRequest) {
        String consentId = encryptedRequest.getConsentId();
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(consentId);
        if (!decryptedConsentId.isPresent()) {
            return;
        }

        AisConsentActionRequest decryptedRequest = new AisConsentActionRequest(encryptedRequest.getTppId(),
                                                                               decryptedConsentId.get(),
                                                                               encryptedRequest.getActionStatus());
        aisConsentService.checkConsentAndSaveActionLog(decryptedRequest);
    }

    @Override
    @Transactional
    public Optional<String> updateAccountAccess(String encryptedConsentId, AisAccountAccessInfo request) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        Optional<String> response = aisConsentService.updateAccountAccess(decryptedConsentId.get(), request);
        if (!response.isPresent()) {
            return Optional.empty();
        }

        String consentId = response.get();
        return encryptionDecryptionService.encryptConsentId(consentId);
    }

    @Override
    @Transactional
    public Optional<String> createAuthorization(String encryptedConsentId, AisConsentAuthorizationRequest request) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        return aisConsentService.createAuthorization(decryptedConsentId.get(), request);
    }

    @Override
    public Optional<AisConsentAuthorizationResponse> getAccountConsentAuthorizationById(String authorisationId,
                                                                                        String encryptedConsentId) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        return aisConsentService.getAccountConsentAuthorizationById(authorisationId, decryptedConsentId.get());
    }

    @Override
    @Transactional
    public boolean updateConsentAuthorization(String authorizationId, AisConsentAuthorizationRequest request) {
        return aisConsentService.updateConsentAuthorization(authorizationId, request);
    }

    @Override
    public Optional<PsuIdData> getPsuDataByConsentId(String encryptedConsentId) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        return aisConsentService.getPsuDataByConsentId(decryptedConsentId.get());
    }

    @Override
    public Optional<List<String>> getAuthorisationsByConsentId(String encryptedConsentId) {
        Optional<String> decryptedConsentId = encryptionDecryptionService.decryptConsentId(encryptedConsentId);
        if (!decryptedConsentId.isPresent()) {
            return Optional.empty();
        }

        return aisConsentService.getAuthorisationsByConsentId(decryptedConsentId.get());
    }
}
