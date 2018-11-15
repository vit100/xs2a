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

package de.adorsys.psd2.consent.api.event;

import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@ApiModel(description = "Event")
public class CmsEvent {
    @ApiModelProperty(value = "Event date", example = "2018-11-15T12:00:00Z")
    private OffsetDateTime dateTime;
    private TppInfo tppInfo;
    @ApiModelProperty(value = "Tpp's ip address", example = "ip")
    private String tppIpAddress;
    @ApiModelProperty(value = "Consent id", example = "b4c7691e-0805-4976-8db0-26eef7c59f5f")
    private String consentId;
    @ApiModelProperty(value = "Payment id", example = "0310318d-c87d-405b-bd2b-166af5124e1f")
    private String paymentId;
    @ApiModelProperty(value = "Request id", example = "2f77a125-aa7a-45c0-b414-cea25a116035")
    private UUID requestId;
    private CmsEventPayload payload;
    @ApiModelProperty(value = "Event's type", example = "CREATE_PAYMENT")
    private EventType eventType;
}
