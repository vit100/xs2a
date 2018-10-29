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

package de.adorsys.psd2.consent.api.piis;

import de.adorsys.psd2.consent.api.AccountInfo;
import de.adorsys.psd2.consent.api.CmsTppInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel(description = "Piis consent request", value = "PiisConsentRequest")
public class CreatePiisConsentRequest {

    @ApiModelProperty(value = "ID of the corresponding PSU", required = true, example = "32aad578-58a6-4d5d-8b0c-45546dd88f07")
    private String psuId;

    @ApiModelProperty(value = "Tpp for which the consent will be created. If the property is omitted, the consent will be created for all TPPs")
    private CmsTppInfo tppInfo;

    @ApiModelProperty(value = "Accounts for which the consent is created")
    private List<AccountInfo> accounts;

    @ApiModelProperty(value = "Consent`s expiration date. The content is the local ASPSP date in ISODate Format", required = true, example = "2020-10-10")
    private LocalDate validUntil;

    @ApiModelProperty(value = "Type of the tpp access: SINGLE_TPP or ALL_TPP.", required = true, example = "ALL_TPP")
    private PiisConsentTppAccessType tppAccessType;
}
