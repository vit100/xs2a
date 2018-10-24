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

package de.adorsys.aspsp.xs2a.service.mapper;

import de.adorsys.aspsp.xs2a.domain.consent.Xs2aAuthenticationObject;
import de.adorsys.aspsp.xs2a.domain.consent.Xs2aAuthenticationType;
import de.adorsys.psd2.model.AuthenticationObject;
import de.adorsys.psd2.model.AuthenticationType;
import org.springframework.stereotype.Component;

@Component
public class AuthorisationModelMapper {
    public Xs2aAuthenticationObject mapToXs2aAuthenticationObject(AuthenticationObject authenticationObject) {
        Xs2aAuthenticationObject object = new Xs2aAuthenticationObject();
        object.setXs2aAuthenticationType(Xs2aAuthenticationType.valueOf(authenticationObject.getAuthenticationType().name()));
        object.setAuthenticationMethodId(authenticationObject.getAuthenticationMethodId());
        object.setAuthenticationVersion(authenticationObject.getAuthenticationVersion());
        object.setName(authenticationObject.getName());
        object.setExplanation(authenticationObject.getExplanation());
        return object;
    }

    public AuthenticationObject mapToAuthenticationObject(Xs2aAuthenticationObject authenticationObject) {
        AuthenticationObject object = new AuthenticationObject();
        object.setAuthenticationType(AuthenticationType.valueOf(authenticationObject.getXs2aAuthenticationType().name()));
        object.setAuthenticationMethodId(authenticationObject.getAuthenticationMethodId());
        object.setAuthenticationVersion(authenticationObject.getAuthenticationVersion());
        object.setName(authenticationObject.getName());
        object.setExplanation(authenticationObject.getExplanation());
        return object;
    }
}
