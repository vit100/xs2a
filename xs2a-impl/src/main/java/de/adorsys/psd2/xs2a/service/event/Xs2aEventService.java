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

import de.adorsys.psd2.consent.api.event.CmsEvent;
import de.adorsys.psd2.consent.api.event.CmsEventPayload;
import de.adorsys.psd2.consent.api.event.EventType;
import de.adorsys.psd2.xs2a.domain.RequestHolder;
import de.adorsys.psd2.xs2a.service.TppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class Xs2aEventService {
    private final TppService tppService;
    private final de.adorsys.psd2.consent.api.service.EventService eventService;

    /**
     * Records new AIS event in the CMS for given consent id, request and event type
     *
     * @param consentId     Consent id that will be recorded along with the event
     * @param requestHolder Information about the incoming request
     * @param eventType     Type of event
     */
    public void recordAisEvent(String consentId, RequestHolder requestHolder, EventType eventType) {
        CmsEvent event = buildCmsEvent(requestHolder, eventType);
        event.setConsentId(consentId);

        // TODO check whether request has been recorded https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/490
        eventService.recordEvent(event);
    }

    /**
     * Records new PIS event in the CMS for given payment id, request and event type
     *
     * @param paymentId     Payment id that will be recorded along with the event
     * @param requestHolder Information about the incoming request
     * @param eventType     Type of event
     */
    public void recordPisEvent(String paymentId, RequestHolder requestHolder, EventType eventType) {
        CmsEvent event = buildCmsEvent(requestHolder, eventType);
        event.setPaymentId(paymentId);

        // TODO check whether request has been recorded https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/490
        eventService.recordEvent(event);
    }

    /**
     * Records new event in the CMS for given request and event type
     *
     * @param requestHolder Information about the incoming request
     * @param eventType     Type of event
     */
    public void recordEvent(RequestHolder requestHolder, EventType eventType) {
        CmsEvent event = buildCmsEvent(requestHolder, eventType);

        // TODO check whether request has been recorded https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/490
        eventService.recordEvent(event);
    }

    private CmsEvent buildCmsEvent(RequestHolder requestHolder, EventType eventType) {
        CmsEvent event = new CmsEvent();
        event.setTimestamp(OffsetDateTime.now());
        event.setTppInfo(tppService.getTppInfo());
        event.setTppIpAddress(requestHolder.getIp());
        event.setRequestId(requestHolder.getRequestId());

        CmsEventPayload cmsEventPayload = new CmsEventPayload();
        cmsEventPayload.setUri(requestHolder.getUri());
        cmsEventPayload.setBody(requestHolder.getBody());
        cmsEventPayload.setHeaders(requestHolder.getHeaders());

        event.setPayload(cmsEventPayload);
        event.setEventType(eventType);
        return event;
    }
}
