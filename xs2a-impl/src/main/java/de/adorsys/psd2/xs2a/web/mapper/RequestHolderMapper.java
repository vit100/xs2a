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

package de.adorsys.psd2.xs2a.web.mapper;

import de.adorsys.psd2.xs2a.domain.RequestHolder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestHolderMapper {
    public RequestHolder mapToRequestHolder(@NotNull HttpServletRequest request, @NotNull UUID requestId) {
        return mapToRequestHolder(request, requestId, null);
    }

    public RequestHolder mapToRequestHolder(@NotNull HttpServletRequest request,
                                            @NotNull UUID requestId,
                                            @Nullable Object body) {
        RequestHolder requestHolder = new RequestHolder();
        requestHolder.setUri(request.getRequestURI());
        requestHolder.setRequestId(requestId);
        requestHolder.setIp(request.getRemoteAddr());
        requestHolder.setHeaders(mapToRequestHeaders(request));
        requestHolder.setBody(body);
        return requestHolder;
    }

    private Map<String, String> mapToRequestHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                   .stream()
                   .collect(Collectors.toMap(name -> name, request::getHeader));
    }
}
