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

package de.adorsys.psd2.consent.domain.piis;

import de.adorsys.psd2.consent.domain.AccountReferenceEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "piis_consent_acc_reference")
@ApiModel(description = "Piis consent to account reference table", value = "PiisConsentAccountReference")
public class PiisConsentAccountReferenceEntity {
    @EmbeddedId
    private PiisConsentAccountReferenceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("consentId")
    private PiisConsentEntity consent;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private AccountReferenceEntity account;

    @Column(name = "aspsp_account_id", length = 100)
    @ApiModelProperty(value = "Aspsp-Account-ID: Bank specific account ID", example = "26bb59a3-2f63-4027-ad38-67d87e59611a")
    private String aspspAccountId;

    public PiisConsentAccountReferenceEntity(PiisConsentEntity consent, AccountReferenceEntity account, String aspspAccountId) {
        this.id = new PiisConsentAccountReferenceId(consent.getId(), account.getId());
        this.consent = consent;
        this.account = account;
        this.aspspAccountId = aspspAccountId;
    }
}
