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

import de.adorsys.psd2.consent.api.event.CmsEvent;
import de.adorsys.psd2.consent.api.service.EventService;
import de.adorsys.psd2.consent.config.EventRemoteUrls;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class Xs2aEventServiceRemote implements EventService {
    @Qualifier("consentRestTemplate")
    private final RestTemplate consentRestTemplate;
    private final EventRemoteUrls eventRemoteUrls;

    @Override
    public boolean recordEvent(@NotNull CmsEvent cmsEvent) {
        return consentRestTemplate.postForEntity(eventRemoteUrls.createEvent(), cmsEvent, Boolean.class).getBody();
    }
}
