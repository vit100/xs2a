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

package de.adorsys.psd2.xs2a.service.mapper;

import de.adorsys.psd2.model.Error400NGPIS;
import de.adorsys.psd2.xs2a.exception.MessageError;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.adorsys.psd2.xs2a.service.mapper.SourceType.PIS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
@RequiredArgsConstructor
public class ErrorMapperHolder {
    private Map<ErrorKey, Function<MessageError, Error400NGPIS>> errorsContainer = new HashMap<>();

    private final PIS400ErrorMapper pis400ErrorMapper;

    @PostConstruct
    public void fillErrorMapperContainer() {
        errorsContainer.put(new ErrorKey(PIS, BAD_REQUEST), pis400ErrorMapper.getMapper());
    }

    public Object getErrorBody(MessageError error, HttpStatus status) {
        return errorsContainer.get(new ErrorKey(error.getSource(), status))
                   .apply(error);
    }

    @Value
    private class ErrorKey {
        private SourceType sourceType;
        private HttpStatus status;
    }
}
