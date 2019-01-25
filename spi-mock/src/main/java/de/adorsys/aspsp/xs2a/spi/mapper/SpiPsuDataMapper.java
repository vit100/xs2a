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

package de.adorsys.aspsp.xs2a.spi.mapper;

import de.adorsys.psd2.aspsp.mock.api.psu.AspspPsuData;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
public class SpiPsuDataMapper {
    public List<AspspPsuData> mapToAspspPsuDataList(List<PsuIdData> psuIdData) {
        return Optional.ofNullable(psuIdData)
                   .map(c -> c.stream()
                                 .map(this::mapToAspspPsuData)
                                 .collect(toList())
                   )
                   .orElseGet(Collections::emptyList);
    }

    public List<PsuIdData> mapToPsuIdDataList(List<AspspPsuData> aspspPsuData) {
        return Optional.ofNullable(aspspPsuData)
                   .map(c -> c.stream()
                                 .map(this::mapToPsuIdData)
                                 .collect(toList())
                   )
                   .orElseGet(Collections::emptyList);
    }

    private AspspPsuData mapToAspspPsuData(PsuIdData psuIdData) {
        return new AspspPsuData(psuIdData.getPsuId(),
                                psuIdData.getPsuIdType(),
                                psuIdData.getPsuCorporateId(),
                                psuIdData.getPsuCorporateIdType()
        );
    }

    private PsuIdData mapToPsuIdData(AspspPsuData aspspPsuData) {
        return new PsuIdData(aspspPsuData.getPsuId(),
                             aspspPsuData.getPsuIdType(),
                             aspspPsuData.getPsuCorporateId(),
                             aspspPsuData.getPsuCorporateIdType()
        );
    }
}
