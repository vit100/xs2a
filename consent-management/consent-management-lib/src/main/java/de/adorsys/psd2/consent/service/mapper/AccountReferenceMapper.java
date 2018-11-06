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
import de.adorsys.psd2.consent.domain.AccountReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountReferenceMapper {
    CmsAccountReference mapToCmsAccountReference(AccountReference accountReference) {
        return Optional.ofNullable(accountReference)
                   .map(ref -> new CmsAccountReference(null,
                                                       ref.getIban(),
                                                       ref.getBban(),
                                                       ref.getPan(),
                                                       ref.getMaskedPan(),
                                                       ref.getMsisdn(),
                                                       ref.getCurrency())
                   ).orElse(null);
    }

    List<AccountReference> mapToAccountReferences(List<CmsAccountReference> cmsAccountReferences) {
        return cmsAccountReferences.stream()
                   .map(this::mapToAccountReference)
                   .collect(Collectors.toList());
    }

    AccountReference mapToAccountReference(CmsAccountReference cmsAccountReference) {
        return Optional.ofNullable(cmsAccountReference)
                   .map(ref -> {
                       AccountReference accountReference = new AccountReference();
                       accountReference.setIban(cmsAccountReference.getIban());
                       accountReference.setBban(cmsAccountReference.getBban());
                       accountReference.setPan(cmsAccountReference.getPan());
                       accountReference.setMaskedPan(cmsAccountReference.getMaskedPan());
                       accountReference.setMsisdn(cmsAccountReference.getMsisdn());
                       accountReference.setCurrency(cmsAccountReference.getCurrency());

                       return accountReference;
                   }).orElse(null);
    }
}
