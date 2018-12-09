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

package de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.embedded;

import com.fasterxml.jackson.core.type.*;
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.psd2.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class StartAuthorisationSuccessfulSteps {

    @Autowired
    private Context context;

    @Autowired
    private TestService testService;

//    @Given("^PSU sends the single payment initiation request and receives the paymentId$")
//    See Global Successful Steps

    @And("^PSU wants to start the authorisation using the authorisation data (.*)$")
    public void loadAuthorisationData(String authorisationData) throws IOException {
        testService.parseJson("/data-input/pis/embedded/" + authorisationData, new TypeReference<TestData<HashMap, StartScaprocessResponse>>() {
        });
    }

    @When("^PSU sends the start authorisation request$")
    public void sendAuthorisationRequest() {
        testService.sendRestCall(HttpMethod.POST, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations");
    }

    @Then("^PSU checks if a link is received and the SCA status is correct$")
    public void checkLinkAndScaStatusEmbedded() {
        ResponseEntity<StartScaprocessResponse> actualResponse = context.getActualResponse();
        StartScaprocessResponse givenResponseBody = (StartScaprocessResponse) context.getTestData().getResponse().getBody();

        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
        assertThat(actualResponse.getBody().getScaStatus(), equalTo(givenResponseBody.getScaStatus()));
        assertThat(actualResponse.getBody().getLinks().get("startAuthorisationWithPsuAuthentication"), notNullValue());
    }
}
