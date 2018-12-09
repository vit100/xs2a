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
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


@FeatureFileSteps
public class ConsentDeletionSuccessfulSteps {


    @Autowired
    private Context<Consents, Void> context;


    @Autowired
    private TestService testService;

    @Autowired
    @Qualifier("xs2a")
    private RestTemplate restTemplate;


    //@Given("^PSU wants to create a consent (.*)$")
    //    See ConsentRequestExpliciteStartAuthorizationSuccessfulSteps

    @Given("^PSU wants to delete the consent (.*)$")
    public void loadTestData(String dataFileName) throws IOException {
        testService.parseJson("/data-input/ais/consent/deletion/" + dataFileName, new TypeReference<TestData<HashMap, Void>>() {
        });
    }

    @When("^PSU deletes consent$")
    public void initiateDeletion() {
        HttpEntity entity = HttpEntityUtils.getHttpEntityWithoutBody(
            context.getTestData().getRequest(), context.getAccessToken());
        ResponseEntity<Void> response = restTemplate.exchange(
            context.getBaseUrl() + "/consents/" + context.getConsentId(),
            HttpMethod.DELETE,
            entity,
            Void.class);
        context.setActualResponse(response);

    }

    @Then("^a successful response code and the appropriate messages get returned$")
    public void checkResponse() {
        ResponseEntity<Void> actualResponse = context.getActualResponse();

        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
    }


}

