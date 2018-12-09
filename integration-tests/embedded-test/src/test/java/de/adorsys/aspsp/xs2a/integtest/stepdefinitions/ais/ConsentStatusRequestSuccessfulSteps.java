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

package de.adorsys.aspsp.xs2a.integtest.stepdefinitions.ais;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.io.*;
import java.time.*;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.*;
import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.io.IOUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@FeatureFileSteps
public class ConsentStatusRequestSuccessfulSteps {

    @Autowired
    @Qualifier("xs2a")
    private RestTemplate restTemplate;

    @Autowired
    private AisConsentService aisConsentService;

    @Autowired
    private Context<Consents, ConsentStatusResponse200> context;

    @Autowired
    private ObjectMapper mapper;

    @Given("^PSU created a consent resource (.*)$")
    public void loadTestData(String dataFileName) throws IOException {

        TestData<Consents, ConsentsResponse201> data = mapper.readValue(
            resourceToString("/data-input/ais/consent/" + dataFileName, UTF_8),
            new TypeReference<TestData<Consents, ConsentsResponse201>>() {
            });
        data.getRequest().getBody().setValidUntil(LocalDate.now().plusYears(5));

        HttpEntity entity = HttpEntityUtils.getHttpEntity(data.getRequest(),
            context.getAccessToken());
        ResponseEntity<ConsentsResponse201> response = restTemplate.exchange(
            context.getBaseUrl() + "/consents",
            HttpMethod.POST,
            entity,
            ConsentsResponse201.class);
        context.setConsentId(response.getBody().getConsentId());
    }

    @And("^AISP wants to get the status (.*) of that consent$")
    public void updateConsentStatus(String dataFileName) throws HttpClientErrorException, IOException {

        TestData<Consents, ConsentStatusResponse200> data = mapper.readValue(
            resourceToString("/data-input/ais/consent/" + dataFileName, UTF_8),
            new TypeReference<TestData<Consents, ConsentStatusResponse200>>() {
            });
        context.setTestData(data);
        de.adorsys.psd2.model.ConsentStatus consentStatus = context.getTestData().getResponse().getBody().getConsentStatus();
        aisConsentService.changeAccountConsentStatus(context.getConsentId(), fromValue(consentStatus.toString()).orElse(null));

    }

    @When("^AISP requests consent status$")
    public void getConsentStatus() throws IOException {
        HttpEntity entity = HttpEntityUtils.getHttpEntityWithoutBody(
            context.getTestData().getRequest(), context.getAccessToken());
        try {
            ResponseEntity<ConsentStatusResponse200> response = restTemplate.exchange(
                context.getBaseUrl() + "/consents/{consentId}/status",
                HttpMethod.GET,
                entity,
                ConsentStatusResponse200.class, context.getConsentId());
            context.setActualResponse(response);
        } catch (RestClientResponseException rex) {
            context.handleRequestError(rex);
        }
    }

    @Then("^a successful response code and the appropriate consent status gets returned$")
    public void checkResponseStatus() {
        ResponseEntity<ConsentStatusResponse200> actualResponse = context.getActualResponse();
        ConsentStatusResponse200 givenResponseBody = context.getTestData().getResponse().getBody();
        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
        assertThat(actualResponse.getBody().getConsentStatus(), notNullValue());
        assertThat(actualResponse.getBody().getConsentStatus(), equalTo(givenResponseBody.getConsentStatus()));
    }

}
