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


@FeatureFileSteps
public class GlobalErrorfulSteps extends AbstractErrorfulSteps {

    @Autowired
    private Context context;

    @Autowired
    private TestService testService;


    // Global errorful step for checking response code and error response
    @Then("^an error response code and the appropriate error response are received")
    public void checkErrorCodeAndResponse() {
        TppMessages actualTppMessages = context.getTppMessages();
        TppMessages givenTppMessages = (TppMessages) context.getTestData().getResponse().getBody();

        HttpStatus httpStatus = context.getTestData().getResponse().getHttpStatus();
        assertThat(context.getActualResponseStatus(), equalTo(httpStatus));

        assertThat(actualTppMessages.size(), is(equalTo(givenTppMessages.size())));

        for (int i = 0; i < actualTppMessages.size(); i++) {
            assertThat(actualTppMessages.get(i).getCategory(), equalTo(givenTppMessages.get(i).getCategory()));
            assertThat(actualTppMessages.get(i).getCode(), equalTo(givenTppMessages.get(i).getCode()));
        }
    }

    // Global errorful step for loading the data - Embedded Approach
    @And("^PSU prepares the errorful data (.*) with the payment service (.*)$")
    public void loadErrorfulDataEmbedded(String dataFileName, String paymentService) throws IOException {
        testService.parseJson("/data-input/pis/embedded/" + dataFileName, new TypeReference<TestData<HashMap, TppMessages>>() {
        });
        context.setPaymentService(paymentService);
        this.setErrorfulIds(dataFileName);
    }

    // Global errorful step for sending the update authorisation request - Embedded Approach
    @When("^PSU sends the errorful update authorisation data request$")
    public void sendErrorfulUpdateAuthorisationRequest() throws IOException {
        testService.sendErrorfulRestCall(HttpMethod.PUT, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations/" + context.getAuthorisationId());
    }

    // @After **** see GlobalSuccessfulSteps
}
