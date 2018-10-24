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

package de.adorsys.aspsp.xs2a.service.mapper.spi_xs2a_mappers;

import de.adorsys.aspsp.xs2a.domain.consent.Xs2aAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationType;
import org.springframework.stereotype.Component;

@Component
public class Xs2aToSpiAuthenticationObjectMapper {
    public SpiAuthenticationObject mapToXs2aAuthenticationObject(Xs2aAuthenticationObject authenticationObject) {
        SpiAuthenticationObject object = new SpiAuthenticationObject();
        object.setAuthenticationType(SpiAuthenticationType.valueOf(authenticationObject.getXs2aAuthenticationType().name()));
        object.setAuthenticationMethodId(authenticationObject.getAuthenticationMethodId());
        object.setAuthenticationVersion(authenticationObject.getAuthenticationVersion());
        object.setName(authenticationObject.getName());
        object.setExplanation(authenticationObject.getExplanation());
        return object;
    }
}
