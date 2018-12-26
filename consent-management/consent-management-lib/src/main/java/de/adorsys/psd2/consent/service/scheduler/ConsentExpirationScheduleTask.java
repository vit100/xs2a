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

package de.adorsys.psd2.consent.service.scheduler;

import de.adorsys.psd2.consent.domain.account.AisConsent;
import de.adorsys.psd2.consent.repository.AisConsentRepository;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsentExpirationScheduleTask implements Runnable {
    private final AisConsentRepository aisConsentRepository;

    @Transactional
    @Override
    public void run() { //NOPMD short method name from Runnable interface
        log.info("Consent expiration schedule task is run!");

        List<AisConsent> expiredConsents = aisConsentRepository.findByConsentStatusIn(EnumSet.of(RECEIVED))
                                               .stream()
                                               .filter(AisConsent::isExpiredByDate)
                                               .collect(Collectors.toList());

        if (!expiredConsents.isEmpty()) {
            aisConsentRepository.save(obsoleteConsent(expiredConsents));
        }
    }

    private List<AisConsent> obsoleteConsent(List<AisConsent> expiredConsents) {
        return expiredConsents.stream()
                   .map(this::obsoleteConsentParameters)
                   .collect(Collectors.toList());
    }

    private AisConsent obsoleteConsentParameters(AisConsent consent) {
        consent.setConsentStatus(ConsentStatus.EXPIRED);
        consent.getAuthorizations().forEach(auth -> auth.setScaStatus(ScaStatus.FAILED));
        return consent;
    }
}

