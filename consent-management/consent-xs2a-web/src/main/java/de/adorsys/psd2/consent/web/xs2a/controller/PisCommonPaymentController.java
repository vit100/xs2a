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

package de.adorsys.psd2.consent.web.xs2a.controller;

import de.adorsys.psd2.consent.api.CmsAuthorisationType;
import de.adorsys.psd2.consent.api.pis.PisConsentStatusResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisCommonPaymentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisCommonPaymentPsuDataResponse;
import de.adorsys.psd2.consent.api.pis.proto.CreatePisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentRequest;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentResponse;
import de.adorsys.psd2.consent.api.pis.proto.PisPaymentInfo;
import de.adorsys.psd2.consent.api.service.PisCommonPaymentService;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/pis/common-payments")
@Api(value = "api/v1/pis/common-payments", tags = "PIS, Common Payment", description = "Provides access to common payment system for PIS")
public class PisCommonPaymentController {
    private final PisCommonPaymentService pisCommonPaymentService;

    @PostMapping(path = "/")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = CreatePisCommonPaymentResponse.class),
        @ApiResponse(code = 400, message = "Bad request")})
    public ResponseEntity<CreatePisCommonPaymentResponse> createCommonPayment(@RequestBody PisPaymentInfo request) {
        return pisCommonPaymentService.createCommonPayment(request)
                   .map(c -> new ResponseEntity<>(c, HttpStatus.CREATED))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(path = "/{payment-id}/status")
    @ApiOperation(value = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = PisConsentStatusResponse.class),
        @ApiResponse(code = 400, message = "Bad request")})
    public ResponseEntity<PisConsentStatusResponse> getConsentStatusById(
        @ApiParam(name = "payment-id", value = "The payment consent identification assigned to the created payment consent.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("payment-id") String consentId) {
        return pisCommonPaymentService.getPisCommonPaymentStatusById(consentId)
                   .map(status -> new ResponseEntity<>(new PisConsentStatusResponse(status), HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(path = "/{payment-id}")
    @ApiOperation(value = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = PisCommonPaymentResponse.class),
        @ApiResponse(code = 400, message = "Bad request")})
    public ResponseEntity<PisCommonPaymentResponse> getConsentById(
        @ApiParam(name = "payment-id", value = "The payment consent identification assigned to the created payment consent.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("payment-id") String paymentId) {
        return pisCommonPaymentService.getCommonPaymentById(paymentId)
                   .map(pc -> new ResponseEntity<>(pc, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping(path = "/{payment-id}/status/{status}")
    @ApiOperation(value = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad request")})
    public ResponseEntity<Void> updateConsentStatus(
        @ApiParam(name = "payment-id", value = "The payment consent identification assigned to the created payment consent.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("payment-id") String consentId,
        @ApiParam(value = "The following code values are permitted 'received', 'valid', 'rejected', 'expired', 'revoked by psu', 'terminated by tpp'. These values might be extended by ASPSP by more values.", allowableValues = "RECEIVED,  REJECTED, VALID, REVOKED_BY_PSU,  EXPIRED,  TERMINATED_BY_TPP")
        @PathVariable("status") String status) {
        return pisCommonPaymentService.updateCommonPaymentStatusById(consentId, ConsentStatus.valueOf(status))
                   .map(updated -> new ResponseEntity<Void>(HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping(path = "/{payment-id}/authorizations")
    @ApiOperation(value = "Create consent authorization for given consent id.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<CreatePisAuthorisationResponse> createConsentAuthorization(
        @ApiParam(name = "payment-id", value = "The consent identification assigned to the created consent authorization.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("payment-id") String paymentId,
        @RequestBody PsuIdData psuData) {
        return pisCommonPaymentService.createAuthorization(paymentId, CmsAuthorisationType.CREATED, psuData)
                   .map(authorization -> new ResponseEntity<>(authorization, HttpStatus.CREATED))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/{payment-id}/cancellation-authorisations")
    @ApiOperation(value = "Create payment authorization cancellation for given payment id.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<CreatePisAuthorisationResponse> createConsentAuthorizationCancellation(
        @ApiParam(name = "payment-id", value = "The payment identification of the related payment.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("payment-id") String paymentId,
        @RequestBody PsuIdData psuData) {
        return pisCommonPaymentService.createAuthorization(paymentId, CmsAuthorisationType.CANCELLED, psuData)
                   .map(authorization -> new ResponseEntity<>(authorization, HttpStatus.CREATED))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/authorizations/{authorization-id}")
    @ApiOperation(value = "Update pis consent authorization.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<UpdatePisCommonPaymentPsuDataResponse> updateConsentAuthorization(
        @ApiParam(name = "authorization-id", value = "The consent authorization identification assigned to the created authorization.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("authorization-id") String authorizationId,
        @RequestBody UpdatePisCommonPaymentPsuDataRequest request) {
        return pisCommonPaymentService.updateCommonPaymentAuthorisation(authorizationId, request)
                   .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/authorizations/{authorization-id}")
    @ApiOperation(value = "Getting pis consent authorization.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<GetPisCommonPaymentAuthorisationResponse> getConsentAuthorization(
        @ApiParam(name = "authorization-id", value = "The consent authorization identification assigned to the created authorization.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("authorization-id") String authorizationId) {
        return pisCommonPaymentService.getPisCommonPaymentAuthorisationById(authorizationId)
                   .map(resp -> new ResponseEntity<>(resp, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/cancellation-authorisations/{cancellation-id}")
    @ApiOperation(value = "Update pis consent cancellation authorisation.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<UpdatePisCommonPaymentPsuDataResponse> updateConsentCancellationAuthorization(
        @ApiParam(name = "cancellation-id", value = "The consent cancellation authorisation identification assigned to the created cancellation authorisation.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("cancellation-id") String cancellationId,
        @RequestBody UpdatePisCommonPaymentPsuDataRequest request) {
        return pisCommonPaymentService.updateCommonPaymentCancellationAuthorisation(cancellationId, request)
                   .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/cancellation-authorisations/{cancellation-id}")
    @ApiOperation(value = "Getting pis consent cancellation authorisation.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<GetPisCommonPaymentAuthorisationResponse> getConsentAuthorizationCancellation(
        @ApiParam(name = "cancellation-id", value = "The consent cancellation authorisation identification assigned to the created cancellation authorisation.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("cancellation-id") String cancellationId) {
        return pisCommonPaymentService.getPisCommonPaymentCancellationAuthorisationById(cancellationId)
                   .map(resp -> new ResponseEntity<>(resp, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/{payment-id}/cancellation-authorisations")
    @ApiOperation(value = "Gets list of payment cancellation authorisation IDs by payment ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<List<String>> getConsentAuthorisationCancellation(
        @ApiParam(name = "payment-id", value = "The payment identification of the related payment.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("payment-id") String paymentId) {
        return pisCommonPaymentService.getAuthorisationsByPaymentId(paymentId, CmsAuthorisationType.CANCELLED)
                   .map(authorisation -> new ResponseEntity<>(authorisation, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/{payment-id}/authorisations")
    @ApiOperation(value = "Gets list of payment authorisation IDs by payment ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<List<String>> getConsentAuthorisation(
        @ApiParam(name = "payment-id", value = "The payment identification of the related payment.", example = "vOHy6fj2f5IgxHk-kTlhw6sZdTXbRE3bWsu2obq54beYOChP5NvRmfh06nrwumc2R01HygQenchEcdGOlU-U0A==_=_iR74m2PdNyE")
        @PathVariable("payment-id") String paymentId) {
        return pisCommonPaymentService.getAuthorisationsByPaymentId(paymentId, CmsAuthorisationType.CREATED)
                   .map(authorisation -> new ResponseEntity<>(authorisation, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // TODO return correct error code in case consent was not found https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/408
    @PutMapping(path = "/{payment-id}/payment")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad request")})
    public ResponseEntity<Void> updatePaymentConsent(@RequestBody PisCommonPaymentRequest request, @PathVariable("payment-id") String paymentId) {
        pisCommonPaymentService.updateCommonPayment(request, paymentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
