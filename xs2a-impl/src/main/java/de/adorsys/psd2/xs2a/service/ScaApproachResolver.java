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

import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import de.adorsys.psd2.xs2a.service.profile.AspspProfileServiceWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static de.adorsys.psd2.xs2a.core.profile.ScaApproach.REDIRECT;

@Service
@RequiredArgsConstructor
public class ScaApproachResolver {
    private static final String TPP_REDIRECT_PREFERRED_HEADER = "tpp-redirect-preferred";
    private static final int SINGLE_VALUE = 1;

    private final AspspProfileServiceWrapper aspspProfileService;
    private final RequestProviderService requestProviderService;

    public ScaApproach resolveScaApproach() {
        boolean tppRedirectPreferred = resolveTppRedirectPreferred();
        List<ScaApproach> scaApproaches = aspspProfileService.getScaApproaches();

        if (singleSca(scaApproaches)) {
            return getFirst(scaApproaches);
        } else if (tppRedirectPreferred && scaApproaches.contains(REDIRECT)) {
            return REDIRECT;
        }
        return getFirst(scaApproaches);
    }

    private boolean resolveTppRedirectPreferred() {
        Map<String, String> headers = requestProviderService.getRequestData()
                                          .getHeaders();
        if (headers == null || !headers.containsKey(TPP_REDIRECT_PREFERRED_HEADER)) {
            return false;
        }
        return Boolean.valueOf(headers.get(TPP_REDIRECT_PREFERRED_HEADER));
    }

    private ScaApproach getFirst(List<ScaApproach> scaApproaches) {
        return scaApproaches.get(0);
    }

    private boolean singleSca(List<ScaApproach> scaApproaches) {
        return SINGLE_VALUE == CollectionUtils.size(scaApproaches);
    }
}
