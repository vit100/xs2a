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

package de.adorsys.psd2.consent.server.service;

import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.server.domain.piis.PiisConsent;
import de.adorsys.psd2.consent.server.repository.PiisConsentRepository;
import de.adorsys.psd2.consent.server.service.mapper.PiisConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PiisConsentService {
    private final PiisConsentRepository piisConsentRepository;
    private final PiisConsentMapper piisConsentMapper;

    /**
     * Create PIIS consent
     *
     * @param request request for creating PIIS consent
     * @return consent id if the consent was created
     */
    @Transactional
    public Optional<String> createConsent(CreatePiisConsentRequest request) {
        PiisConsent consent = piisConsentMapper.mapToPiisConsent(request, ConsentStatus.RECEIVED);
        consent.setExternalId(UUID.randomUUID().toString());
        PiisConsent saved = piisConsentRepository.save(consent);
        return saved.getId() != null
                   ? Optional.ofNullable(saved.getExternalId())
                   : Optional.empty();
    }

    /**
     * Get PIIS aspsp consent data by id
     *
     * @param consentId id of the consent
     * @return Response containing aspsp consent data
     */
    public Optional<CmsAspspConsentDataBase64> getAspspConsentData(String consentId) {
        return getActualPiisConsent(consentId)
                   .map(this::getConsentAspspData);
    }

    private CmsAspspConsentDataBase64 getConsentAspspData(PiisConsent consent) {
        CmsAspspConsentDataBase64 response = new CmsAspspConsentDataBase64();
        String aspspConsentDataBase64 = Optional.ofNullable(consent.getAspspConsentData())
                                            .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                                            .orElse(null);
        response.setAspspConsentDataBase64(aspspConsentDataBase64);
        response.setConsentId(consent.getExternalId());
        return response;
    }

    private Optional<PiisConsent> getActualPiisConsent(String consentId) {
        return Optional.ofNullable(consentId)
                   .flatMap(c -> piisConsentRepository.findByExternalIdAndConsentStatusIn(consentId, EnumSet.of(RECEIVED, VALID)));
    }
}
