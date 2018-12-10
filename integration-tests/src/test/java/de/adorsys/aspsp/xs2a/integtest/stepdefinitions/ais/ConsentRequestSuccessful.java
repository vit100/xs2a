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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.aspsp.xs2a.integtest.model.TestData;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.FeatureFileSteps;
import de.adorsys.aspsp.xs2a.integtest.util.AisConsentService;
import de.adorsys.aspsp.xs2a.integtest.util.Context;
import de.adorsys.aspsp.xs2a.integtest.util.HttpEntityUtils;
import de.adorsys.psd2.model.ConsentInformationResponse200Json;
import de.adorsys.psd2.model.Consents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.resourceToString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@FeatureFileSteps
public class ConsentRequestSuccessful {


    @Qualifier("xs2a")
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AisConsentService aisConsentService;

    @Autowired
    private Context<Consents, ConsentInformationResponse200Json> context;

    @Autowired
    private ObjectMapper mapper;


   @And("^PSU want to get consent using (.*)$")
    public void psu_want_to_get_consent_using(String dataFileName) throws HttpClientErrorException , IOException{
        TestData<Consents, ConsentInformationResponse200Json> data = mapper.readValue(
            resourceToString("/data-input/ais/consent/" + dataFileName, UTF_8),
            new TypeReference<TestData<Consents, ConsentInformationResponse200Json>>() {});
        context.setTestData(data);
        }
    @When("^PSU requests consent$")
    public void requestConsent()  {
        HttpEntity entity = HttpEntityUtils.getHttpEntityWithoutBody(context.getTestData().getRequest(),
            context.getAccessToken());
        ResponseEntity<ConsentInformationResponse200Json> response = restTemplate.exchange(
            context.getBaseUrl() + "/consents/"+context.getConsentId(),
            HttpMethod.GET,
            entity,
            ConsentInformationResponse200Json.class);
        context.setActualResponse(response);
    }
    @Then("^a successful response code and the appropriate consent gets returned$")
    public void checkResponseCode() {
        ResponseEntity<ConsentInformationResponse200Json> actualResponse = context.getActualResponse();
        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
    }

}
