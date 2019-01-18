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
import de.adorsys.psd2.model.MessageCode400PIS;
import de.adorsys.psd2.model.TppMessage400PIS;
import de.adorsys.psd2.model.TppMessageCategory;
import de.adorsys.psd2.xs2a.domain.TppMessageInformation;
import de.adorsys.psd2.xs2a.exception.MessageError;
import de.adorsys.psd2.xs2a.service.message.MessageService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PIS400ErrorMapper extends Psd2ErrorMapper<MessageError, Error400NGPIS> {

    public PIS400ErrorMapper(MessageService messageService) {
        super(messageService);
    }

    @Override
    public Function<MessageError, Error400NGPIS> getMapper() {
        return this::mapToPsd2Error;
    }

    private Error400NGPIS mapToPsd2Error(MessageError messageError) {
        return new Error400NGPIS().tppMessages(mapToTppMessage400PIS(messageError.getTppMessages()))
                   ._links(Collections.EMPTY_MAP);
    }

    private List<TppMessage400PIS> mapToTppMessage400PIS(Set<TppMessageInformation> tppMessages) {
        return tppMessages.stream()
                   .map(m -> new TppMessage400PIS()
                                 .category(TppMessageCategory.fromValue(m.getCategory().name()))
                                 .code(MessageCode400PIS.fromValue(m.getMessageErrorCode().getName()))
                                 .path(m.getPath())
                                 .text(messageService.getMessage(m.getMessageErrorCode().getName()))
                   ).collect(Collectors.toList());
    }
}
