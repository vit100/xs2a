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

package de.adorsys.aspsp.aspspmockserver.converter;

import de.adorsys.aspsp.aspspmockserver.domain.spi.account.SpiExchangeRate;
import de.adorsys.aspsp.aspspmockserver.domain.spi.account.SpiExchangeRatePO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.account.SpiTransaction;
import de.adorsys.aspsp.aspspmockserver.domain.spi.account.SpiTransactionPO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiAmount;
import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiAmountPO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionConverter {

    SpiTransaction toSpiTransaction(SpiTransactionPO po);

    SpiTransactionPO toSpiTransactionPO(SpiTransaction tnx);

    List<SpiTransactionPO> toTransactionPOList(List<SpiTransaction> list);

    List<SpiTransaction> toTransactionList(List<SpiTransactionPO> list);

    SpiAmount toAmount(SpiAmountPO amountPO);

    SpiAmountPO toAmountPO(SpiAmount amountPO);

    SpiExchangeRate toExchangeRate(SpiExchangeRatePO exchangeRatePO);

    SpiExchangeRatePO toExchangeRatePO(SpiExchangeRate exchangeRatePO);
}
