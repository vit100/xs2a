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
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.psd2.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;

import java.io.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@FeatureFileSteps
public class UpdateAuthorisationWithIdentificationSuccessfulSteps {

    @Autowired
    private TestService testService;
    @Autowired
    private Context context;

    // @Given("^PSU sends the single payment initiation request and receives the paymentId$")
    // See Global Successful Steps

    // @And("^PSU sends the start authorisation request and receives the authorisationId$")
    // See GlobalSuccessfulSteps

    @And("^PSU wants to update the resource with his identification data (.*)$")
    public void loadIdentificationData(String identificationData) throws IOException {
        testService.parseJson("/data-input/pis/embedded/" + identificationData, new TypeReference<TestData<UpdatePsuAuthentication, UpdatePsuAuthenticationResponse>>() {
        });
    }

    @When("^PSU sends the update identification data request$")
    public void sendUpdateAuthorisationWithIdentificationRequest() {
        testService.sendRestCall(HttpMethod.PUT, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations/" + context.getAuthorisationId());
    }

    @Then("PSU checks if the correct SCA status and response code is received$")
    public void checkScaStatusAndResponseCode() {
        ResponseEntity<UpdatePsuAuthenticationResponse> actualResponse = context.getActualResponse();
        UpdatePsuAuthenticationResponse givenResponseBody = (UpdatePsuAuthenticationResponse) context.getTestData().getResponse().getBody();

        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
        assertThat(actualResponse.getBody().getScaStatus(), equalTo(givenResponseBody.getScaStatus()));

        if (actualResponse.getBody().getScaStatus().equals(ScaStatus.PSUAUTHENTICATED)) {
            ScaMethods actualMethods = actualResponse.getBody().getScaMethods();

            assertThat(actualMethods.size(), equalTo(givenResponseBody.getScaMethods().size()));

            for (int i = 0; i < actualMethods.size(); i++) {
                assertThat(actualMethods.get(i).getAuthenticationType(), equalTo(givenResponseBody.getScaMethods().get(i).getAuthenticationType()));
                assertThat(actualMethods.get(i).getAuthenticationMethodId(), notNullValue());
            }
        }
    }
}
