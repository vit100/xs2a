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

import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.server.service.PiisConsentService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/piis/consent")
@Api(value = "api/v1/piis/consent", tags = "PIIS, Aspsp Consent Data", description = "Provides access to consent management system for PIIS AspspDataConsent")
public class PiisAspspConsentDataController {
    private final PiisConsentService piisConsentService;

    @GetMapping(path = "/{consent-id}/aspsp-consent-data")
    @ApiOperation(value = "Get aspsp consent data identified by given consent id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<CmsAspspConsentDataBase64> getAspspConsentData(
        @ApiParam(name = "consent-id", value = "The piis consent identification assigned to the created piis consent.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("consent-id") String consentId) {
        return piisConsentService.getAspspConsentData(consentId)
                   .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
