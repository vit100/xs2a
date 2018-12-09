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
public class CommonFcsSteps {

    @Autowired
    private Context context;

    @Autowired
    @Qualifier("aspsp-profile")
    private RestTemplate restTemplate;



    @When("^confirmation of funds request is received in xs2a$")
    public void fund_confirmation_is_received() throws IOException {
        HttpEntity entity = HttpEntityUtils.getHttpEntity(
            context.getTestData().getRequest(), context.getAccessToken());
        try {
            ResponseEntity<FundsConfirmationResponse> response = restTemplate.exchange(
                context.getBaseUrl() + "/funds-confirmations",
                HttpMethod.POST, entity, FundsConfirmationResponse.class);
            context.setActualResponse(response);
        } catch (RestClientResponseException restclientResponseException) {
            context.handleRequestError(restclientResponseException);
        }
    }

}
