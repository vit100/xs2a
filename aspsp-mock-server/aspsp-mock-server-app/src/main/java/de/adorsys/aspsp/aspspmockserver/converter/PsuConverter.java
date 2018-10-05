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

import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiAmount;
import de.adorsys.aspsp.aspspmockserver.domain.spi.common.SpiAmountPO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.psu.Psu;
import de.adorsys.aspsp.aspspmockserver.domain.spi.psu.PsuPO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.psu.SpiScaMethod;
import de.adorsys.aspsp.aspspmockserver.domain.spi.psu.SpiScaMethodPO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PsuConverter {

    Psu toPsu(PsuPO po);

    PsuPO toPsuPO(Psu psu);

    List<Psu> toPsuList(List<PsuPO> poList);

    SpiScaMethod toScaMethod(SpiScaMethodPO methodPO);

    SpiScaMethodPO toScaMethodPO(SpiScaMethod scaMethod);

    List<SpiScaMethod> toScaMethodList(List<SpiScaMethodPO> list);

    List<SpiScaMethodPO> toScaMethodPOList(List<SpiScaMethod> list);

    SpiAmount toAmount(SpiAmountPO amountPO);

    SpiAmountPO toAmountPO(SpiAmount amountPO);
}
