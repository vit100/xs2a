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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.aspsp.xs2a.integtest.model.TestData;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.TestService;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.FeatureFileSteps;
import de.adorsys.aspsp.xs2a.integtest.util.Context;
import de.adorsys.psd2.model.Consents;
import de.adorsys.psd2.model.ConsentsResponse201;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@FeatureFileSteps
public class ConsentDeletionSuccessfulSteps {


    @Autowired
    private Context<Consents, ConsentsResponse201> context;


    @Autowired
    private TestService testService;


    //    @Given("^PSU sends the single payment initiation request and receives the paymentId$")
    //    See Global Successful Steps

    @Given ("^PSU wants to delete the consent (.*)$")
    public void loadTestData(String dataFileName) throws IOException {
        testService.parseJson("/data-input/ais/consent/deletion/" + dataFileName, new TypeReference<TestData<HashMap, ConsentsResponse201 >>() {
        });
    }

    @When("^PSU deletes consent$")
    public void initiateDeletion () {
        testService.sendRestCall(HttpMethod.DELETE, context.getBaseUrl() + "/" + context.getConsentId());
    }

    @Then("^a successful response code and the appropriate messages get returned$")
    public void checkResponse() {
    ResponseEntity<ConsentsResponse201> actualResponse = context.getActualResponse();
    ConsentsResponse201 givenResponseBody = context.getTestData().getResponse().getBody();

        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
        assertThat(actualResponse.getBody().getConsentStatus(), equalTo(givenResponseBody.getConsentStatus()));
    }


}

