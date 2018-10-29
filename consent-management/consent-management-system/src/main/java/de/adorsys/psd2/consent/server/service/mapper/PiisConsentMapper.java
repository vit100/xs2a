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

package de.adorsys.psd2.consent.server.service.mapper;

import de.adorsys.psd2.consent.api.AccountInfo;
import de.adorsys.psd2.consent.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.server.domain.piis.PiisAccountReference;
import de.adorsys.psd2.consent.server.domain.piis.PiisConsent;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PiisConsentMapper {
    private final TppInfoMapper tppInfoMapper;

    public PiisConsent mapToPiisConsent(CreatePiisConsentRequest request, ConsentStatus consentStatus) {
        PiisConsent consent = new PiisConsent();
        consent.setConsentStatus(consentStatus);
        consent.setRequestDateTime(LocalDateTime.now());
        consent.setExpireDate(request.getValidUntil());
        consent.setPsuId(request.getPsuId());
        consent.setTppInfo(tppInfoMapper.mapToTppInfo(request.getTppInfo()));
        consent.setAccounts(mapToPiisAccountReferenceList(request.getAccounts()));
        return consent;
    }

    private List<PiisAccountReference> mapToPiisAccountReferenceList(List<AccountInfo> accountInfos) {
        return accountInfos.stream()
                   .map(this::mapToPiisAccountReference)
                   .collect(Collectors.toList());
    }

    private PiisAccountReference mapToPiisAccountReference(AccountInfo accountInfo) {
        PiisAccountReference reference = new PiisAccountReference();
        reference.setIban(accountInfo.getIban());
        reference.setCurrency(getCurrencyByString(accountInfo.getCurrency()));
        return reference;
    }

    private Currency getCurrencyByString(String currency) {
        return Optional.ofNullable(currency)
                   .map(Currency::getInstance)
                   .orElse(null);
    }
}
