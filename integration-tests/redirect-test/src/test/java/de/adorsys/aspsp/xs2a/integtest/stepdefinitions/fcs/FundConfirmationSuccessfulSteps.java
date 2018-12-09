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

package de.adorsys.aspsp.xs2a.integtest.stepdefinitions.fcs;

import com.fasterxml.jackson.core.type.*;
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.model.*;
import de.adorsys.psd2.xs2a.domain.fund.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@FeatureFileSteps
public class FundConfirmationSuccessfulSteps {

    @Autowired
    private Context<ConfirmationOfFunds, FundsConfirmationResponse> context;

    @Autowired
    private TestService testService;

    @Given("^PSU initiated card based payment transaction using (.*) at a PSU – TPP interface$")
    public void load_fund_confirmation_card_data(String dataFileName) throws IOException {
        testService.parseJson("/data-input/fcs/" + dataFileName, new TypeReference<TestData<ConfirmationOfFunds, FundsConfirmationResponse>>() {
        });
    }

    @And("^wants to get a confirmation (.*) of (.*) on his account$")
    public void psu_want_to_get_confirmation_of_fund(boolean result ,String amount) {
        context.getTestData().getResponse().getBody().setFundsAvailable(result);
        context.getTestData().getRequest().getBody().getInstructedAmount().setAmount(amount);
    }

    @Then("^a successful response code 200 and fundsAvailable is returned$")
    public void checkResponseCodeAndFundAvailability() {
        ResponseEntity<FundsConfirmationResponse> actualResponse = context.getActualResponse();
        FundsConfirmationResponse givenResponseBody = context.getTestData().getResponse().getBody();

        assertThat(actualResponse.getStatusCode(), equalTo(context.getTestData().getResponse().getHttpStatus()));
        assertThat(actualResponse.getBody().isFundsAvailable(), equalTo(givenResponseBody.isFundsAvailable()));
    }


}
