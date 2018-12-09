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

import cucumber.api.java.en.*;
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
public class CommonSteps {

    @Autowired
    private Context context;
    @Autowired
    private AisConsentService aisConsentService;
    @Autowired
    @Qualifier("aspsp-profile")
    private RestTemplate restTemplate;

    // Common errorful step for checking response code and error response
    @Then("^an error response code is displayed and an appropriate error response is shown$")
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

    @Given("^PSU already has an existing (.*) consent (.*)$")
    public void psu_already_has_an_existing_consent(String consentStatus, String dataFileName) throws IOException {
        String consentId = aisConsentService.createConsentWithStatus(consentStatus, dataFileName);
        context.setConsentId(consentId);
    }


    @And("^ASPSP-profile contains parameter signingBasketSupported is (.*)$")
    public void signingBasketSupportedIsTrue(Boolean signingBasketSupported) {
        HttpEntity entity = HttpEntityUtils.getHttpEntity(signingBasketSupported);
        this.restTemplate.put(context.getProfileUrl() + "/aspsp-profile/for-debug/signing-basket-supported", entity);

    }

    @And("^parameter TPP-Explicit-Authorisation-Preferred is (.*)$")
    public void tppExplicitAuthorisationPreferredHeaderIsTrue(Boolean authPreferred) throws HttpClientErrorException {
        context.getTestData().getRequest().getHeader()
            .put("TPP-Explicit-Authorisation-Preferred", Objects.nonNull(authPreferred) ? authPreferred.toString() : null);
    }

}
