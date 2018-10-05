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

package de.adorsys.aspsp.aspspmockserver.domain.spi.payment;

import de.adorsys.aspsp.aspspmockserver.domain.pis.PisPaymentTypePO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.account.SpiAccountReferencePO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiAmountPO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiTransactionStatusPO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AspspPaymentPO {
    private String paymentId;
    private String endToEndIdentification;
    private SpiAccountReferencePO debtorAccount;
    @Deprecated // Since 1.2
    private String ultimateDebtor;
    private SpiAmountPO instructedAmount;
    private SpiAccountReferencePO creditorAccount;
    private String creditorAgent;
    private String creditorName;
    private SpiAddressPO creditorAddress;
    @Deprecated // Since 1.2
    private String ultimateCreditor;
    @Deprecated // Since 1.2
    private String purposeCode;
    private String remittanceInformationUnstructured;
    @Deprecated // Since 1.2
    private SpiRemittancePO remittanceInformationStructured;
    @Deprecated // Since 1.2
    private LocalDate requestedExecutionDate;
    @Deprecated // Since 1.2
    private LocalDateTime requestedExecutionTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String executionRule;
    private String frequency; // TODO consider using an enum similar to FrequencyCode based on the the "EventFrequency7Code" of ISO 20022
    private int dayOfExecution; //Day here max 31
    private PisPaymentTypePO pisPaymentType;
    private SpiTransactionStatusPO paymentStatus;
    private String bulkId;

    public AspspPaymentPO() {}

    public AspspPaymentPO(PisPaymentTypePO pisPaymentType) {
        this.pisPaymentType = pisPaymentType;
    }
}
