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

import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.domain.PsuData;
import de.adorsys.psd2.consent.domain.piis.PiisConsent;
import de.adorsys.psd2.consent.repository.PiisConsentRepository;
import de.adorsys.psd2.consent.service.mapper.PiisConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PiisConsentServiceInternalTest {
    private static final long CONSENT_ID = 1;
    private static final String EXTERNAL_CONSENT_ID = "5bcf664f-68ce-498d-9a93-fe0cce32f6b6";
    private static final String PSU_ID = "PSU-ID-1";
    private static final String APSPS_CONSENT_DATA = "Test Data";

    @Mock
    private PiisConsentRepository piisConsentRepository;
    @Mock
    private PiisConsentMapper piisConsentMapper;

    @InjectMocks
    private PiisConsentServiceInternal piisConsentServiceInternal;

    @Before
    public void setUp() {
        when(piisConsentMapper.mapToPiisConsent(any(), eq(ConsentStatus.RECEIVED)))
            .thenReturn(getConsent(ConsentStatus.RECEIVED));
    }

    @Test
    public void createConsent_Success() {
        when(piisConsentRepository.save(any(PiisConsent.class)))
            .thenReturn(getConsent(ConsentStatus.RECEIVED));

        // Given
        CreatePiisConsentRequest request = getCreatePiisConsentRequest();

        // When
        Optional<String> actual = piisConsentServiceInternal.createConsent(request);

        // Then
        assertThat(actual.isPresent()).isTrue();
        String id = actual.get();
        assertThat(StringUtils.isNotBlank(id)).isTrue();
    }

    @Test
    public void createConsent_Failure() {
        when(piisConsentRepository.save(any(PiisConsent.class)))
            .thenReturn(getConsent(null, ConsentStatus.RECEIVED));

        // Given
        CreatePiisConsentRequest request = getCreatePiisConsentRequest();

        // When
        Optional<String> actual = piisConsentServiceInternal.createConsent(request);

        // Then
        assertThat(actual.isPresent()).isFalse();
    }

    private CreatePiisConsentRequest getCreatePiisConsentRequest() {
        return new CreatePiisConsentRequest();
    }


    private PiisConsent getConsent(ConsentStatus status) {
        return getConsent(CONSENT_ID, status);
    }

    private PiisConsent getConsent(Long id, ConsentStatus status) {
        PiisConsent piisConsent = new PiisConsent();
        piisConsent.setId(id);
        piisConsent.setExternalId(EXTERNAL_CONSENT_ID);
        piisConsent.setRequestDateTime(OffsetDateTime.now());
        piisConsent.setExpireDate(LocalDate.now().plusDays(100));
        piisConsent.setPsuData(getPsuData());
        piisConsent.setConsentStatus(status);
        return piisConsent;
    }

    private PsuData getPsuData() {
        return new PsuData(PSU_ID, null, null, null);
    }

    private byte[] decode(String s) {
        return Base64.getDecoder().decode(s);
    }
}
