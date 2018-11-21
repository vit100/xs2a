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

package de.adorsys.psd2.xs2a.service.event;

import de.adorsys.psd2.consent.api.service.EventService;
import de.adorsys.psd2.xs2a.core.event.Event;
import de.adorsys.psd2.xs2a.core.event.EventOrigin;
import de.adorsys.psd2.xs2a.core.event.EventType;
import de.adorsys.psd2.xs2a.domain.RequestHolder;
import de.adorsys.psd2.xs2a.domain.event.RequestEventPayload;
import de.adorsys.psd2.xs2a.service.TppService;
import de.adorsys.psd2.xs2a.web.RequestProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class Xs2aEventService {
    private final TppService tppService;
    private final EventService eventService;
    private final RequestProviderService requestProviderService;

    /**
     * Records generic TPP request in form of event for given request and event type
     *
     * @param eventType     Type of event
     */
    public void recordTppRequest(EventType eventType) {
        Event event = buildTppEvent(requestProviderService.getRequest(), eventType);

        recordEventInCms(event);
    }

    /**
     * Records generic TPP request in form of event for given request and event type
     *
     * @param requestHolder Information about the incoming request
     * @param eventType     Type of event
     */
    public void recordTppRequest(RequestHolder requestHolder, EventType eventType) {
        Event event = buildTppEvent(requestHolder, eventType);

        recordEventInCms(event);
    }

    private void recordEventInCms(Event event) {
        boolean recorded = eventService.recordEvent(event);
        if (!recorded) {
            log.error("Couldn't record event from TPP request: {}", event);
        }
    }

    private Event buildTppEvent(RequestHolder requestHolder, EventType eventType) {
        Event event = new Event();
        event.setTimestamp(OffsetDateTime.now());
        event.setEventOrigin(EventOrigin.TPP);
        event.setEventType(eventType);
        event.setPaymentId(requestHolder.getPaymentId());
        event.setConsentId(requestHolder.getConsentId());

        RequestEventPayload requestPayload = new RequestEventPayload();
        requestPayload.setTppInfo(tppService.getTppInfo());
        requestPayload.setTppIp(requestHolder.getIp());
        requestPayload.setRequestId(requestHolder.getRequestId());
        requestPayload.setUri(requestHolder.getUri());
        requestPayload.setBody(requestHolder.getBody());
        requestPayload.setHeaders(requestHolder.getHeaders());
        event.setPayload(requestPayload);

        return event;
    }
}
