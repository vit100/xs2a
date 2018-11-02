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

package de.adorsys.psd2.consent.service.mapper;

import de.adorsys.psd2.consent.api.ais.CmsAccountReference;
import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.domain.piis.PiisAccountReference;
import de.adorsys.psd2.consent.domain.piis.PiisConsent;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    private final PsuDataMapper psuDataMapper;

    public PiisConsent mapToPiisConsent(@NotNull CreatePiisConsentRequest request, @NotNull ConsentStatus consentStatus) {
        PiisConsent consent = new PiisConsent();
        consent.setConsentStatus(consentStatus);
        consent.setRequestDateTime(LocalDateTime.now());
        consent.setExpireDate(request.getValidUntil());
        consent.setPsuData(psuDataMapper.mapToPsuData(request.getPsuData()));
        consent.setTppInfo(tppInfoMapper.mapToTppInfo(request.getTppInfo()));
        consent.setAccounts(mapToPiisAccountReferences(request.getAccounts()));
        consent.setTppAccessType(request.getTppAccessType());
        return consent;
    }

    private List<PiisAccountReference> mapToPiisAccountReferences(List<CmsAccountReference> cmsAccountReferences) {
        return cmsAccountReferences.stream()
                   .map(this::mapToPiisAccountReference)
                   .collect(Collectors.toList());
    }

    private PiisAccountReference mapToPiisAccountReference(CmsAccountReference cmsAccountReference) {
        return Optional.ofNullable(cmsAccountReference)
                   .map(ref -> {
                       PiisAccountReference piisAccountReference = new PiisAccountReference();
                       piisAccountReference.setIban(ref.getIban());
                       piisAccountReference.setBban(ref.getBban());
                       piisAccountReference.setPan(ref.getPan());
                       piisAccountReference.setMaskedPan(ref.getMaskedPan());
                       piisAccountReference.setMsisdn(ref.getMsisdn());
                       piisAccountReference.setCurrency(ref.getCurrency());

                       return piisAccountReference;
                   }).orElse(null);
    }

    private Currency getCurrencyByString(String currency) {
        return Optional.ofNullable(currency)
                   .map(Currency::getInstance)
                   .orElse(null);
    }
}
