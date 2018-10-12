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
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityDataService {

    /**
     * Encrypts incoming data and generates salt.
     *
     * @param dataForEncryption  data for encryption
     * @param password  secret key
     * @return   AspspConsentData with encrypted data
     */
    public AspspConsentData getEncryptedAspspConsentData(byte[] dataForEncryption, String password) {
        String salt = KeyGenerators.string().generateKey();
        byte[] encryptedData = encryptData(dataForEncryption, password, salt);

        return new AspspConsentData(encryptedData, null, salt);
    }


    /**
     *  Decrypts data from incoming object
     *
     * @param encryptedData
     * @param password
     * @return
     */
    public Optional<AspspConsentData> getDecryptedAspspConsentData(AspspConsentData encryptedData, String password) {
        String salt = encryptedData.getCryptoSalt();

        if (StringUtils.isNotBlank(salt)) {

            byte[] decryptedData = decryptData(encryptedData.getAspspConsentData(), password, salt);
            return Optional.of(new AspspConsentData(decryptedData, encryptedData.getConsentId(), null));

        }
        return Optional.empty();
    }

    private byte[] encryptData(byte[] dataForEncryption, String password, String salt) {
        BytesEncryptor encryptor = Encryptors.stronger(password, salt);
        return encryptor.encrypt(dataForEncryption);
    }

    private byte[] decryptData(byte[] encryptedData, String password, String salt) {
        BytesEncryptor decryptor = Encryptors.stronger(password, salt);
        return decryptor.decrypt(encryptedData);
    }
}
