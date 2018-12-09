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

package de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis;

import com.fasterxml.jackson.core.type.*;
import cucumber.api.java.*;
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.config.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.model.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.client.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Slf4j
@FeatureFileSteps
public class GlobalSuccessfulSteps {
    @Autowired
    private Context context;

    @Autowired
    @Qualifier("aspsp-mock")
    private RestTemplate template;

    @Autowired
    private AuthConfigProperty authConfigProperty;

    @Autowired
    private TestService testService;

    @Before
    public void loadTestDataIntoDb() {
        template.exchange(
            context.getMockUrl() + "/integration-tests/refresh-testing-data",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            String.class);
    }

    @Given("^PSU request access token for oauth approach$")
    public void requestAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", authConfigProperty.getGrantType());
        map.add("username", authConfigProperty.getUsername());
        map.add("password", authConfigProperty.getPassword());
        map.add("client_id", authConfigProperty.getClientId());
        map.add("client_secret", authConfigProperty.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<HashMap> response = null;
        try {
            response = template.exchange(authConfigProperty.getUrl(), HttpMethod.POST, entity, HashMap.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        context.setScaApproach("oauth");
        context.setAccessToken(Objects.requireNonNull(response).getBody().get("access_token").toString());
    }

    // Global Step for single payment initiation
    @Given("^PSU wants to initiate a single payment (.*) using the payment service (.*) and the payment product (.*)$")
    public void loadTestData(String dataFileName, String paymentService, String paymentProduct) throws IOException {
        context.setPaymentProduct(paymentProduct);
        context.setPaymentService(paymentService);

        testService.parseJson("/data-input/pis/single/" + dataFileName, new TypeReference<TestData<PaymentInitiationSctJson, PaymentInitationRequestResponse201>>() {
        });
    }

    // Global Step for single payment initiation
    @When("^PSU sends the single payment initiating request$")
    public void sendSinglePaymentInitiatingRequest() {
        testService.sendRestCall(HttpMethod.POST, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentProduct());
    }

    // Global Step for the single payment initiation and storage of the paymentId
    @Given("^PSU sends the single payment initiation request and receives the paymentId$")
    public void sendSinglePaymentInitiationRequestAndStoreId() throws IOException {
        context.setPaymentProduct("sepa-credit-transfers");
        context.setPaymentService("payments");

        testService.parseJson("/data-input/pis/single/singlePayInit-successful.json", new TypeReference<TestData<PaymentInitiationSctJson, PaymentInitationRequestResponse201>>() {
        });
        testService.sendRestCall(HttpMethod.POST, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentProduct());
        context.setPaymentId(((PaymentInitationRequestResponse201) context.getActualResponse().getBody()).getPaymentId());
    }


    // Global step for checking the redirect url of the payment initiation response - Redirect Approach
    @And("^a redirect URL is delivered to the PSU$")
    public void checkRedirectUrl() {
        ResponseEntity<PaymentInitationRequestResponse201> actualResponse = context.getActualResponse();

        assertThat(actualResponse.getBody().getLinks().get("scaRedirect"), notNullValue());
    }

    // Global step for checking the response of payment initiation - Redirect Approach
    @Then("^a successful response code and the appropriate payment response data are received$")
    public void checkResponseCode() {
        ResponseEntity<PaymentInitationRequestResponse201> actualResponse = context.getActualResponse();
        PaymentInitationRequestResponse201 givenResponseBody = (PaymentInitationRequestResponse201) context.getTestData().getResponse().getBody();

        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));

        assertThat(actualResponse.getBody().getTransactionStatus(), equalTo(givenResponseBody.getTransactionStatus()));
        assertThat(actualResponse.getBody().getPaymentId(), notNullValue());

        // TODO: Take asserts back in when respective response headers are implemented (https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/289)
        // assertThat(actualResponse.getHeaders().get("Location"), equalTo(context.getBaseUrl() + "/" +
        //    context.getPaymentService() + "/" + actualResponse.getAspspConsentData().getPaymentId()));

        // assertThat(actualResponse.getHeaders().get("X-Request-ID"), equalTo(context.getTestData().getRequest().getHeader().get("x-request-id")));
    }


    //Global Step for starting the authorisation and saving the authorisation id - Embedded Approach
    @And("^PSU sends the start authorisation request and receives the authorisationId$")
    public void startAuthorisationAndStoreId() throws IOException {
        HttpEntity entity = HttpEntityUtils.getHttpEntityWithoutBody(context.getTestData().getRequest(), context.getAccessToken());
        testService.parseJson(("/data-input/pis/embedded/" + "startAuth-successful.json"), new TypeReference<TestData<HashMap, StartScaprocessResponse>>() {
        });
        testService.sendRestCall(HttpMethod.POST, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations", entity);
        extractAuthorisationId(context.getActualResponse());
    }

    private void extractAuthorisationId(ResponseEntity<StartScaprocessResponse> response) {
        String regex = "\\/authorisations\\/(.*?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence) response.getBody().getLinks().get("startAuthorisationWithPsuAuthentication"));
        while (matcher.find()) {
            context.setAuthorisationId(matcher.group(1));
        }
    }

    // Global Step for updating the PSU identification data - Embedded Approach
    @And("^PSU updates his identification data$")
    public void updatePsuIdentification() throws IOException {
        testService.parseJson("/data-input/pis/embedded/" + "updateIdentificationNoSca-successful.json", new TypeReference<TestData<UpdatePsuAuthentication, UpdatePsuAuthenticationResponse>>() {
        });
        testService.sendRestCall(HttpMethod.PUT, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations/" + context.getAuthorisationId());
    }

    @After
    public void afterScenario() {
        log.debug("Cleaning up context");
        context.cleanUp();
    }
}
