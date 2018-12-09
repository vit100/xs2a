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

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import cucumber.api.java.en.*;
import de.adorsys.aspsp.xs2a.integtest.model.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.util.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.model.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.io.*;
import java.time.*;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.io.IOUtils.*;

@Slf4j
@FeatureFileSteps
public class ConsentRequestErrorfulSteps {

    @Autowired
    @Qualifier("xs2a")
    private RestTemplate restTemplate;

    @Autowired
    private Context<Consents, TppMessages> context;

    @Autowired
    private ObjectMapper mapper;

    @Given("^PSU wants to create an erroful consent (.*)$")
    public void loadTestData(String dataFileName) throws IOException {

        TestData<Consents, TppMessages> data = mapper.readValue(
            resourceToString("/data-input/ais/consent/" + dataFileName, UTF_8),
            new TypeReference<TestData<Consents, TppMessages>>() {
            });

        context.setTestData(data);

        LocalDate validUntil = context.getTestData().getRequest().getBody().getValidUntil();
        context.getTestData().getRequest().getBody().setValidUntil(validUntil.plusDays(7));
    }

    @Given("^PSU wants to create an expired consent (.*)$")
    public void loadTestDataExpired(String dataFileName) throws IOException {

        TestData<Consents, TppMessages> data = mapper.readValue(
            resourceToString("/data-input/ais/consent/" + dataFileName, UTF_8),
            new TypeReference<TestData<Consents, TppMessages>>() {
            });

        context.setTestData(data);

        LocalDate validUntil = context.getTestData().getRequest().getBody().getValidUntil();
        context.getTestData().getRequest().getBody().setValidUntil(validUntil.minusYears(3));
    }


    @When("^PSU sends the create consent request with error$")
    public void sendErrorfulConsentRequest() throws HttpClientErrorException, IOException {
        HttpEntity entity = HttpEntityUtils.getHttpEntity(
            context.getTestData().getRequest(), context.getAccessToken());
        try {
            restTemplate.exchange(
                context.getBaseUrl() + "/consents",
                HttpMethod.POST,
                entity,
                ConsentsResponse201.class);
        } catch (RestClientResponseException rex) {
            context.handleRequestError(rex);
        }
    }

    //@Then("^an error response code is displayed and an appropriate error response is shown$")
    //See commonStep
}
