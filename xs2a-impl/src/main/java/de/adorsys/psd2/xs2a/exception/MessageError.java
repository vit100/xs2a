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

package de.adorsys.psd2.xs2a.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.domain.ErrorHolder;
import de.adorsys.psd2.xs2a.domain.MessageErrorCode;
import de.adorsys.psd2.xs2a.domain.TppMessageInformation;
import de.adorsys.psd2.xs2a.service.mapper.SourceType;
import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.RJCT;
import static java.util.Collections.singletonList;

@Data
public class MessageError {
    @JsonUnwrapped
    private TransactionStatus transactionStatus;
    private Set<TppMessageInformation> tppMessages = new HashSet<>();
    private SourceType source;
    // TODO move MessageErrorCode here from TppMessageInformation

    public MessageError(SourceType source, TppMessageInformation... tppMessageInformation) {
        this.source = source;
        fillTppMessage(tppMessageInformation);
    }

    public MessageError(TppMessageInformation tppMessage) {
        this(RJCT, tppMessage);
    }

    public MessageError(List<TppMessageInformation> tppMessages) {
        this(RJCT, tppMessages);
    }

    public MessageError(TransactionStatus status, TppMessageInformation tppMessage) {
        this(status, singletonList(tppMessage));
    }

    public MessageError(ErrorHolder errorHolder) {
        this(errorHolder.getErrorCode(), errorHolder.getMessage());
    }

    public MessageError(TransactionStatus status, List<TppMessageInformation> tppMessages) {
        this.transactionStatus = status;
        this.tppMessages.addAll(tppMessages);
    }

    public MessageError(MessageErrorCode errorCode, String message) {
        this(RJCT, singletonList(new TppMessageInformation(MessageCategory.ERROR, errorCode, message)));
    }

    public MessageError(MessageErrorCode errorCode) {
        this(errorCode, null);
    }

    // TODO task: add logic to resolve resulting MessageError https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/211
    @JsonIgnore
    public TppMessageInformation getTppMessage() {
        return tppMessages.iterator().next();
    }

    private void fillTppMessage(TppMessageInformation[] tppMessages) {
        if (isNotEmpty(tppMessages)) {
            this.tppMessages.addAll(Arrays.stream(tppMessages)
                                        .collect(Collectors.toSet()));
        }
    }

    private boolean isNotEmpty(TppMessageInformation[] tppMessages) {
        return tppMessages != null && tppMessages.length >= 1;
    }
}
