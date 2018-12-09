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

package de.adorsys.aspsp.xs2a.integtest;

import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.aspsp.profile.domain.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.*;


@SpringBootApplication
public class RedirectTestApp implements ApplicationRunner {
    @Value("${aspspProfile.baseUrl}")
    private String profileBaseurl;

    @Autowired
    @Qualifier("aspsp-profile")
    private RestTemplate restTemplate;

    public static void main(String[] args) {

        SpringApplication.run(RedirectTestApp.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments){
        this.restTemplate.put(profileBaseurl+"/aspsp-profile/for-debug/sca-approach",HttpEntityUtils.getHttpEntity("REDIRECT"));
        initProfile(false);
    }




    private void initProfile(Boolean signingBasketSupported)  {
        AspspSettings settings = restTemplate.getForObject(
            profileBaseurl+"/aspsp-profile", AspspSettings.class);
        settings = new AspspSettings (
            settings.getFrequencyPerDay(),
            settings.isCombinedServiceIndicator(),
            settings.getAvailablePaymentProducts(),
            settings.getAvailablePaymentTypes(),
            settings.isTppSignatureRequired(),
            settings.getPisRedirectUrlToAspsp(),
            settings.getAisRedirectUrlToAspsp(),
            settings.getMulticurrencyAccountLevel(),
            settings.isBankOfferedConsentSupport(),
            settings.getAvailableBookingStatuses(),
            settings.getSupportedAccountReferenceFields(),
            settings.getConsentLifetime(),
            settings.getTransactionLifetime(),
            settings.isAllPsd2Support(),
            settings.isTransactionsWithoutBalancesSupported(),
            signingBasketSupported,
            settings.isPaymentCancellationAuthorizationMandated(),
            settings.isPiisConsentSupported(),
            settings.isDeltaReportSupported(),
            settings.getRedirectUrlExpirationTimeMs()

        );

        this.restTemplate.put(profileBaseurl+"/aspsp-profile/for-debug/aspsp-settings", HttpEntityUtils.getHttpEntity(settings));

    }
}
