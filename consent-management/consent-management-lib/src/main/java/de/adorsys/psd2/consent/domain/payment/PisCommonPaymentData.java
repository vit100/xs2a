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

package de.adorsys.psd2.consent.domain.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "pis_common_payment_data")
@ApiModel(description = "pis common payment entity", value = "PisCommonPaymentData")
public class PisCommonPaymentData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pis_common_payment_data_generator")
    @SequenceGenerator(name = "pis_common_payment_data_generator", sequenceName = "pis_common_payment_data_id_seq")
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private String payment_id;

    @Column(name = "payment", nullable = false)
    @ApiModelProperty(value = "All data about the payment", required = true)
    private byte[] payment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", nullable = false)
    @ApiModelProperty(value = "Detailed information about consent", required = true)
    private PisConsent consent_id;
}
