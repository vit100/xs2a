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

package de.adorsys.psd2.consent.domain.event;

import de.adorsys.psd2.consent.api.event.EventType;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@Entity(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_generator")
    @SequenceGenerator(name = "event_generator", sequenceName = "event_id_seq")
    private Long id;

    @Column(name = "date_time", nullable = false)
    private OffsetDateTime dateTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tpp_info_id", nullable = false)
    private TppInfoEntity tppInfo;

    @Column(name = "tpp_ip_address")
    private String tppIpAddress;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Lob
    @Column(name = "payload", nullable = false)
    private byte[] payload;

    @Column(name = "event_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
}
