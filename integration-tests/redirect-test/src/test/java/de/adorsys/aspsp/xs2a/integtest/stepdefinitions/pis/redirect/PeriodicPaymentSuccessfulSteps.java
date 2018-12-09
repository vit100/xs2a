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

package de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.redirect;

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
import java.time.*;

@FeatureFileSteps
public class PeriodicPaymentSuccessfulSteps {

    private static final long DAYS_OFFSET = 100L;

    @Autowired
    private Context<PeriodicPaymentInitiationSctJson, PaymentInitationRequestResponse201> context;

    @Autowired
    private TestService testService;

    @Given("^PSU wants to initiate a recurring payment (.*) using the payment service (.*) and the payment product (.*)$")
    public void loadTestDataForSuccessfulPeriodicPayment(String dataFileName, String paymentService, String paymentProduct) throws IOException {
        context.setPaymentProduct(paymentProduct);
        context.setPaymentService(paymentService);
        testService.parseJson("/data-input/pis/recurring/" + dataFileName, new TypeReference<TestData<PeriodicPaymentInitiationSctJson, PaymentInitationRequestResponse201>>() {
        });
        context.getTestData().getRequest().getBody().setEndDate(LocalDate.now().plusDays(DAYS_OFFSET));
    }

    @When("^PSU sends the recurring payment initiating request$")
    public void sendSuccessfulPeriodicPaymentInitiatingRequest() {
        testService.sendRestCall(HttpMethod.POST, context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentProduct());
    }

//    @Then("^a successful response code and the appropriate payment response data are received$")
//    see ./GlobalSuccessfulSteps.java

    // @And("^a redirect URL is delivered to the PSU$")
    // See GlobalSuccessfulSteps
}
