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

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.When;
import de.adorsys.aspsp.xs2a.integtest.util.Context;
import de.adorsys.aspsp.xs2a.integtest.util.PaymentUtils;
import de.adorsys.psd2.model.TppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

@FeatureFileSteps
public class SinglePaymentEmbeddedUpdateAuthErrorfulSteps {

    @Autowired
    @Qualifier("xs2a")
    private RestTemplate restTemplate;

    @Autowired
    private Context<HashMap, TppMessages> context;

    @Autowired
    private ObjectMapper mapper;

    @When("^PSU sends the errorful authorisation udpate request$")
    public void sendErrorfulUpdateAuthorisationRequest() throws IOException {
        HttpEntity entity = PaymentUtils.getHttpEntity(context.getTestData().getRequest(), context.getAccessToken());

        // TODO: take back in when successful steps have the authorisationId saved in the context
//        try {
//            restTemplate.exchange(
//                context.getBaseUrl() + "/" + context.getPaymentService() + "/" + context.getPaymentId() + "/authorisations"
//                + "/" + context.getAuthorisationId,
//                HttpMethod.PUT,
//                entity,
//                HashMap.class);
//        } catch (RestClientResponseException rex) {
//            context.handleRequestError(rex);
//        }
    }

    // @Then("^an error response code and the appropriate error response are received$")
    // See GlobalErrorfulSteps
}
