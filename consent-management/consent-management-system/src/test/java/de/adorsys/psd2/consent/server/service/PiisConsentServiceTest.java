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

import de.adorsys.psd2.consent.api.AccountInfo;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.api.piis.PiisConsentTppAccessType;
import de.adorsys.psd2.consent.server.domain.piis.PiisConsent;
import de.adorsys.psd2.consent.server.repository.PiisConsentRepository;
import de.adorsys.psd2.consent.server.service.mapper.PiisConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PiisConsentServiceTest {
    private static final long CONSENT_ID = 1;
    private static final String EXTERNAL_CONSENT_ID = "5bcf664f-68ce-498d-9a93-fe0cce32f6b6";
    private static final String PSU_ID = "PSU-ID-1";
    private static final String APSPS_CONSENT_DATA = "Test Data";

    @Mock
    private PiisConsentRepository piisConsentRepository;
    @Mock
    private PiisConsentMapper piisConsentMapper;

    @InjectMocks
    private PiisConsentService piisConsentService;

    @Before
    public void setUp() {
        when(piisConsentMapper.mapToPiisConsent(getCreatePiisConsentRequest(), ConsentStatus.RECEIVED))
            .thenReturn(getConsent());
    }

    @Test
    public void createConsent_Success() {
        when(piisConsentRepository.save(any(PiisConsent.class))).thenReturn(getConsent());

        // Given
        CreatePiisConsentRequest request = getCreatePiisConsentRequest();

        // When
        Optional<String> actual = piisConsentService.createConsent(request);

        // Then
        assertThat(actual.isPresent()).isTrue();
        assertThat(StringUtils.isNotEmpty(actual.get())).isTrue();
    }

    @Test
    public void createConsent_Failure() {
        when(piisConsentRepository.save(any(PiisConsent.class))).thenReturn(getConsent(null));

        // Given
        CreatePiisConsentRequest request = getCreatePiisConsentRequest();

        // When
        Optional<String> actual = piisConsentService.createConsent(request);

        // Then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void getAspspConsentData() {
        // When
        when(piisConsentRepository.findByExternalIdAndConsentStatusIn(EXTERNAL_CONSENT_ID, EnumSet.of(RECEIVED, VALID)))
            .thenReturn(Optional.ofNullable(getConsent()));
        // Then
        Optional<CmsAspspConsentDataBase64> aspspConsentData = piisConsentService.getAspspConsentData(EXTERNAL_CONSENT_ID);
        // Assert
        assertTrue(aspspConsentData.isPresent());
        String aspspConsentDataBase64 = aspspConsentData.get().getAspspConsentDataBase64();
        byte[] decode = decode(aspspConsentDataBase64);
        assertEquals(new String(decode), APSPS_CONSENT_DATA);
    }

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
        piisConsent.setPsuId(PSU_ID);
        return piisConsent;
    }

    private CreatePiisConsentRequest getCreatePiisConsentRequest() {
        CreatePiisConsentRequest request = new CreatePiisConsentRequest();
        request.setPsuId(PSU_ID);
        request.setAccounts(getAccountInfoList());
        request.setValidUntil(LocalDate.now());
        request.setTppAccessType(PiisConsentTppAccessType.ALL_TPP);
        return request;
    }

    private List<AccountInfo> getAccountInfoList() {
        return Collections.singletonList(new AccountInfo("DE89370400440532013000", "USD"));
    }

    private byte[] decode(String s) {
        return Base64.getDecoder().decode(s);
    }
}
