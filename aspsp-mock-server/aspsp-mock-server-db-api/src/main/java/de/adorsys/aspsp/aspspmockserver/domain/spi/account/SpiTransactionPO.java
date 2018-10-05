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

package de.adorsys.aspsp.aspspmockserver.domain.spi.account;

import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiAmountPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpiTransactionPO {
    private String transactionId;
    private String entryReference;
    private String endToEndId;
    private String mandateId;
    private String checkId;
    private String creditorId;
    private LocalDate bookingDate;
    private LocalDate valueDate;
    private SpiAmountPO spiAmount;
    private List<SpiExchangeRatePO> exchangeRate;
    private String creditorName;
    private SpiAccountReferencePO creditorAccount;
    private String ultimateCreditor;
    private String debtorName;
    private SpiAccountReferencePO debtorAccount;
    private String ultimateDebtor;
    private String remittanceInformationUnstructured;
    private String remittanceInformationStructured;
    private String purposeCode;
    private String bankTransactionCodeCode;
    private String proprietaryBankTransactionCode;
}
