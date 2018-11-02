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

import de.adorsys.psd2.consent.api.service.PiisConsentService;
import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.aspsp.api.service.CmsAspspPiisService;
import de.adorsys.psd2.consent.domain.piis.PiisConsent;
import de.adorsys.psd2.consent.repository.PiisConsentRepository;
import de.adorsys.psd2.consent.service.mapper.PiisConsentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PiisConsentServiceInternal implements PiisConsentService, CmsAspspPiisService {
    private final PiisConsentRepository piisConsentRepository;
    private final PiisConsentMapper piisConsentMapper;

    @Override
    @Transactional
    public Optional<String> createConsent(CreatePiisConsentRequest request) {
        PiisConsent consent = piisConsentMapper.mapToPiisConsent(request, RECEIVED);
        consent.setExternalId(UUID.randomUUID().toString());
        PiisConsent saved = piisConsentRepository.save(consent);
        return saved.getId() != null
                   ? Optional.ofNullable(saved.getExternalId())
                   : Optional.empty();
    }
}
