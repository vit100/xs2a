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

package de.adorsys.psd2.xs2a.service.mapper.psd2;

import de.adorsys.psd2.xs2a.exception.MessageError;
import de.adorsys.psd2.xs2a.service.mapper.psd2.piis.*;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.psd2.xs2a.service.mapper.psd2.ErrorType.*;

@Component
@RequiredArgsConstructor
public class ErrorMapperContainer {
    private final Map<ErrorType, Psd2ErrorMapper> mapperContainer = new HashMap<>();

    private final PIS400ErrorMapper pis400ErrorMapper;
    private final PIS401ErrorMapper pis401ErrorMapper;

    private final PIIS400ErrorMapper piis400ErrorMapper;
    private final PIIS401ErrorMapper piis401ErrorMapper;

    private final PIIS403ErrorMapper piis403ErrorMapper;
    private final PIIS404ErrorMapper piis404ErrorMapper;
    private final PIIS405ErrorMapper piis405ErrorMapper;
    private final PIIS409ErrorMapper piis409ErrorMapper;

    @PostConstruct
    public void fillErrorMapperContainer() {
        mapperContainer.put(PIS_400, pis400ErrorMapper);
        mapperContainer.put(PIS_401, pis401ErrorMapper);

        mapperContainer.put(PIIS_400, piis400ErrorMapper);
        mapperContainer.put(PIIS_401, piis401ErrorMapper);

        mapperContainer.put(PIIS_403, piis403ErrorMapper);
        mapperContainer.put(PIIS_404, piis404ErrorMapper);
        mapperContainer.put(PIIS_405, piis405ErrorMapper);
        mapperContainer.put(PIIS_409, piis409ErrorMapper);
    }

    @SuppressWarnings("unchecked")
    public ErrorBody getErrorBody(MessageError error) {
        Psd2ErrorMapper psd2ErrorMapper = mapperContainer.get(error.getErrorType());

        return new ErrorBody(psd2ErrorMapper.getMapper()
                                 .apply(error), psd2ErrorMapper.getErrorStatus());
    }

    @Value
    public class ErrorBody {
        private Object body;
        private HttpStatus status;
    }
}
