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

import cucumber.api.java.en.When;

import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.TestService;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.AbstractErrorfulSteps;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.FeatureFileSteps;
import de.adorsys.aspsp.xs2a.integtest.util.Context;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import java.io.IOException;


@FeatureFileSteps
public class GetAuthorisationIdsErrorfulSteps extends AbstractErrorfulSteps {
    @Autowired
    private Context context;

    @Autowired
    private TestService testService;

    //  @Given("^PSU wants to initiate a single payment (.*) using the payment service (.*) and the payment product (.*)$")
    // See SinglePaymentSuccessfulSteps

    // @And("^PSU sends the single payment initiating request and receives the paymentId$")
    // See GlobalSuccessfulSteps

    // @And("^PSU sends the start authorisation request and receives the authorisationId$")
    // See GlobalSuccessfulSteps

    // @And("^PSU prepares errorful request for a list of authorisation subressources (.*) with the payment service (.*)$")
    // See GlobalErrorfulSteps

    @When("^PSU sends the errorful request for a list of authorisation subressources$")
    public void sendErrorfulRequest() throws HttpClientErrorException, IOException {
        testService.sendErrorfulRestCall(HttpMethod.GET, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations");
        }

    // @Then("^an error response code and the appropriate error response are received$")
    // See GlobalErrorfulSteps

}
