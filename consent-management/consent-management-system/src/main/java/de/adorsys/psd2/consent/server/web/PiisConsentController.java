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

package de.adorsys.psd2.consent.server.web;

import de.adorsys.psd2.consent.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.api.piis.CreatePiisConsentResponse;
import de.adorsys.psd2.consent.server.service.PiisConsentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/piis/consent")
@Api(value = "api/v1/piis/consent", tags = "PIIS, Consents", description = "Provides access to consent management system for PIIS")
public class PiisConsentController {
    private final PiisConsentService piisConsentService;

    @PostMapping(path = "/")
    @ApiOperation(value = "Create consent for given psu id and accesses.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = String.class),
        @ApiResponse(code = 204, message = "No Content")})
    public ResponseEntity<CreatePiisConsentResponse> createConsent(@RequestBody CreatePiisConsentRequest request) {
        return piisConsentService.createConsent(request)
                   .map(consentId -> new ResponseEntity<>(new CreatePiisConsentResponse(consentId), HttpStatus.CREATED))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
