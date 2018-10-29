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
import de.adorsys.psd2.consent.server.domain.piis.PiisConsent;
import de.adorsys.psd2.consent.server.repository.PiisConsentRepository;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PiisConsentServiceTest {
    @InjectMocks
    private PiisConsentService piisConsentService;
    @Mock
    private PiisConsentRepository piisConsentRepository;

    private PiisConsent piisConsent;
    private final String EXTERNAL_CONSENT_ID = "4b112130-6a96-4941-a220-2da8a4af2c65";
    private final String APSPS_CONSENT_DATA = "Test Data";
    private final String PSU_ID = "6843514564";

    @Before
    public void setUp() {
        piisConsent = buildConsent();
    }

    @Test
    public void getAspspConsentData() {
        // When
        when(piisConsentRepository.findByExternalIdAndConsentStatusIn(EXTERNAL_CONSENT_ID, EnumSet.of(RECEIVED, VALID))).thenReturn(Optional.ofNullable(piisConsent));
        // Then
        Optional<CmsAspspConsentDataBase64> aspspConsentData = piisConsentService.getAspspConsentData(EXTERNAL_CONSENT_ID);
        // Assert
        assertTrue(aspspConsentData.isPresent());
        String aspspConsentDataBase64 = aspspConsentData.get().getAspspConsentDataBase64();
        byte[] decode = decode(aspspConsentDataBase64);
        assertEquals(new String(decode), APSPS_CONSENT_DATA);
    }

    private PiisConsent buildConsent() {
        PiisConsent piisConsent = new PiisConsent();
        piisConsent.setExternalId(UUID.randomUUID().toString());
        piisConsent.setRequestDateTime(LocalDateTime.now().minusMinutes(5));
        piisConsent.setPsuId(PSU_ID);
        piisConsent.setConsentStatus(ConsentStatus.RECEIVED);
        piisConsent.setAspspConsentData(APSPS_CONSENT_DATA.getBytes());
        piisConsent.setExpireDate(LocalDate.now().plusDays(100));
        return piisConsent;
    }

    private byte[] decode(String s) {
        return Base64.getDecoder().decode(s);
    }
}
