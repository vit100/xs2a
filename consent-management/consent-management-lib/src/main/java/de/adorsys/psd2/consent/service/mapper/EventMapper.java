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

import de.adorsys.psd2.consent.api.event.CmsEvent;
import de.adorsys.psd2.consent.api.event.CmsEventPayload;
import de.adorsys.psd2.consent.component.JsonConverter;
import de.adorsys.psd2.consent.domain.event.EventEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final TppInfoMapper tppInfoMapper;
    private final JsonConverter jsonConverter;

    public List<CmsEvent> mapToCmsEventList(@NotNull List<EventEntity> eventEntities) {
        return eventEntities.stream()
                   .map(this::mapToCmsEvent)
                   .collect(Collectors.toList());
    }

    public EventEntity mapToEventEntity(@NotNull CmsEvent cmsEvent) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setTimestamp(cmsEvent.getTimestamp());
        eventEntity.setConsentId(cmsEvent.getConsentId());
        eventEntity.setPaymentId(cmsEvent.getPaymentId());
        byte[] payload = jsonConverter.toJsonBytes(cmsEvent.getPayload())
                             .orElse(null);
        eventEntity.setPayload(payload);
        // eventEntity.setEventType(cmsEvent.getEventType()); TODO stop using CmsEvent
        return eventEntity;
    }

    private CmsEvent mapToCmsEvent(@NotNull EventEntity eventEntity) {
        CmsEvent cmsEvent = new CmsEvent();
        cmsEvent.setTimestamp(eventEntity.getTimestamp());
        cmsEvent.setConsentId(eventEntity.getConsentId());
        cmsEvent.setPaymentId(eventEntity.getPaymentId());
        CmsEventPayload payload = jsonConverter.toObject(eventEntity.getPayload(), CmsEventPayload.class)
                                      .orElse(null);
        cmsEvent.setPayload(payload);
        // cmsEvent.setEventType(eventEntity.getEventType()); TODO stop using CmsEvent
        return cmsEvent;
    }
}
