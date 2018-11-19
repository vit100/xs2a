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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import de.adorsys.aspsp.xs2a.integtest.model.TestData;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.TestService;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.FeatureFileSteps;
import de.adorsys.aspsp.xs2a.integtest.util.Context;
import de.adorsys.aspsp.xs2a.integtest.util.HttpEntityUtils;
import de.adorsys.psd2.model.Consents;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.TppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

@FeatureFileSteps
public class ConsentDeletionErrorfulSteps {

    @Autowired
    @Qualifier("xs2a")
    private RestTemplate restTemplate;

    @Autowired
    private Context<Consents, TppMessages> context;

    @Autowired
    private TestService testService;

    //@Given("^PSU wants to create a consent (.*)$")
    //    See ConsentRequestSuccessfulSteps

    //@And("^PSU sends the create consent request$")
    //  See ConsentRequestSuccessfulSteps


    //already implemented in Branch AKI_successful_deletion_consent
    @And("^PSU wants to delete the consent (.*)$")
    public void loadTestData(String dataFileName) throws IOException{
        testService.parseJson("/data-input/ais/consent/deletion/" + dataFileName, new TypeReference<TestData<Consents, TppMessages >>() {
        });
    }


    @When("^PSU sends the consent deletion request with errors$")
    public void deleteConsent()throws HttpClientErrorException, IOException{
        HttpEntity entity = HttpEntityUtils.getHttpEntity(
            context.getTestData().getRequest(), context.getAccessToken());

        try {
            restTemplate.exchange(
                context.getBaseUrl() + "/consents/" + context.getConsentId(),
                HttpMethod.DELETE,
                entity,
                TppMessages.class);
        } catch (RestClientResponseException rex) {
            context.handleRequestError(rex);
        }



    }

    //@Then("^an error response code is displayed and an appropriate error response is shown$")
    //See commonStep

}
