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

package de.adorsys.aspsp.xs2a.service.consent;


import de.adorsys.aspsp.xs2a.spi.domain.consent.AspspConsentData;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityDataServiceTest {
    private SecurityDataService securityDataService = new SecurityDataService();
    private final byte[]  SECRET_DATA = "my secret data".getBytes(StandardCharsets.UTF_8);
    private final String PASSWORD = "secret key";

    @Test
    public void getEncryptedAspspConsentData_success() {
        // When
        AspspConsentData encryptedAspspConsentData = securityDataService.getEncryptedAspspConsentData(SECRET_DATA, PASSWORD);

        // Then
        assertThat(encryptedAspspConsentData.getAspspConsentData()).isNotEqualTo(SECRET_DATA);
    }

    @Test
    public void getDecryptedAspspConsentData_success() {
        // Given
        AspspConsentData encryptedAspspConsentData = securityDataService.getEncryptedAspspConsentData(SECRET_DATA, PASSWORD);

        // When
        Optional<AspspConsentData> decryptedAspspConsentData = securityDataService.getDecryptedAspspConsentData(encryptedAspspConsentData, PASSWORD);

        // Then
        assertThat(decryptedAspspConsentData.isPresent()).isEqualTo(true);
        assertThat(decryptedAspspConsentData.get().getAspspConsentData()).isEqualTo(SECRET_DATA);
    }
}
