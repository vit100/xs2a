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

import de.adorsys.psd2.consent.domain.PsuData;
import de.adorsys.psd2.consent.domain.piis.PiisConsent;
import de.adorsys.psd2.consent.repository.PiisConsentRepository;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@RunWith(MockitoJUnitRunner.class)
public class PiisConsentServiceInternalTest {
    private static final long CONSENT_ID = 1;
    private static final String EXTERNAL_CONSENT_ID = "5bcf664f-68ce-498d-9a93-fe0cce32f6b6";
    private static final String PSU_ID = "PSU-ID-1";
    private static final String APSPS_CONSENT_DATA = "Test Data";

    @Mock
    private PiisConsentRepository piisConsentRepository;

    @InjectMocks
    private PiisConsentServiceInternal piisConsentServiceInternal;

    private PiisConsent getConsent() {
        return getConsent(CONSENT_ID);
    }

    private PiisConsent getConsent(Long id) {
        PiisConsent piisConsent = new PiisConsent();
        piisConsent.setId(id);
        piisConsent.setExternalId(EXTERNAL_CONSENT_ID);
        piisConsent.setRequestDateTime(LocalDateTime.now());
        piisConsent.setExpireDate(LocalDate.now().plusDays(100));
        piisConsent.setAspspConsentData(APSPS_CONSENT_DATA.getBytes());
        piisConsent.setPsuData(getPsuData());
        return piisConsent;
    }

    private PsuData getPsuData() {
        return new PsuData(PSU_ID, null, null, null);
    }

    private byte[] decode(String s) {
        return Base64.getDecoder().decode(s);
    }
}
