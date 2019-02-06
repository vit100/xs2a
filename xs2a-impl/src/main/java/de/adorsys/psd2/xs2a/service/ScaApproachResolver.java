/*
 * Copyright 2018-2019 adorsys GmbH & Co KG
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

package de.adorsys.psd2.xs2a.service;

import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.adorsys.psd2.xs2a.core.profile.ScaApproach.REDIRECT;

@Service
@RequiredArgsConstructor
public class ScaApproachResolver {
    private static final int COLLECTION_SIZE_ONE = 1;

    private final AspspProfileService aspspProfileService;
    private final RequestProviderService requestProviderService;

    public ScaApproach resolveScaApproach() {
        boolean tppRedirectPreferred = requestProviderService.resolveTppRedirectPreferred();
        List<ScaApproach> scaApproaches = aspspProfileService.getScaApproaches();

        if (isSingleSca(scaApproaches)) {
            return getFirst(scaApproaches);
        } else if (tppRedirectPreferred && scaApproaches.contains(REDIRECT)) {
            return REDIRECT;
        }
        return getFirst(scaApproaches);
    }

    private ScaApproach getFirst(List<ScaApproach> scaApproaches) {
        return scaApproaches.get(0);
    }

    private boolean isSingleSca(List<ScaApproach> scaApproaches) {
        return COLLECTION_SIZE_ONE == CollectionUtils.size(scaApproaches);
    }
}
