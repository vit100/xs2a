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
import cucumber.api.junit.Cucumber;
import cucumber.runtime.model.CucumberExamples;
import de.adorsys.aspsp.xs2a.integtest.CucumberIT;
import de.adorsys.aspsp.xs2a.integtest.model.TestData;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.TestService;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.FeatureFileSteps;
import de.adorsys.aspsp.xs2a.integtest.util.AisConsentService;
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
    @And("^PSU wants to errorfully delete the consent (.*)$")
    public void loadTestData(String dataFileName) throws IOException{
        testService.parseJson("/data-input/ais/consent/deletion/" + dataFileName, new TypeReference<TestData<Consents, TppMessages >>() {
        });

        if(dataFileName.equals("consent-deletion-not-existing-id.json")){
            context.setConsentId("678316543982NotAConsent");
        }
        if(dataFileName.equals("consent-deletion-with-expired-consent-id.json")){
            makeConsentExpired();
        }
    }

    @When("^PSU sends the consent deletion request with errors$")
    public void deleteConsent()throws HttpClientErrorException, IOException{
        testService.sendErrorfulRestCall(HttpMethod.DELETE,context.getBaseUrl() + "/consents/" + context.getConsentId());
    }

    private void makeConsentExpired(){
        testService.sendRestCall(HttpMethod.GET,context.getBaseUrl()+"/accounts");
    }

    //@Then("^an error response code is displayed and an appropriate error response is shown$")
    //See commonStep

}
