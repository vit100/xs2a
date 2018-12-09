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
import com.fasterxml.jackson.databind.*;
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.io.IOUtils.*;

@FeatureFileSteps
public class BulkPaymentSuccessfulSteps {
    @Autowired
    @Qualifier("xs2a")
    private RestTemplate restTemplate;

    @Autowired
    private Context<BulkPaymentInitiationSctJson, PaymentInitationRequestResponse201> context;

    @Autowired
    private ObjectMapper mapper;

    @Given("^PSU wants to initiate multiple payments (.*) using the payment service (.*) and the payment product (.*)$")
    public void loadTestDataBulkPayment(String dataFileName, String paymentService, String paymentProduct) throws IOException {
        context.setPaymentProduct(paymentProduct);
        context.setPaymentService(paymentService);

        TestData<BulkPaymentInitiationSctJson, PaymentInitationRequestResponse201> data = mapper.readValue(
            resourceToString("/data-input/pis/bulk/" + dataFileName, UTF_8),
            new TypeReference<TestData<BulkPaymentInitiationSctJson, PaymentInitationRequestResponse201>>() {
            });

        context.setTestData(data);
    }

    @When("^PSU sends the bulk payment initiating request$")
    public void sendBulkPaymentInitiatingRequest() {
        HttpEntity entity = HttpEntityUtils.getHttpEntity(
            context.getTestData().getRequest(), context.getAccessToken());

        ResponseEntity<PaymentInitationRequestResponse201> response = restTemplate.exchange(
            context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentProduct(),
            HttpMethod.POST, entity, new ParameterizedTypeReference<PaymentInitationRequestResponse201>() {
            });

        context.setActualResponse(response);
    }

//    @Then("^a successful response code and the appropriate payment response data are received$")
//    see ./GlobalSuccessfulSteps.java

    // @And("^a redirect URL is delivered to the PSU$")
    // See GlobalSuccessfulSteps
}
