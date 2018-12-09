package de.adorsys.aspsp.xs2a.integtest.stepdefinitions;

import cucumber.api.java.*;
import de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.aspsp.profile.domain.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.client.*;

@FeatureFileSteps
public class EmbeddedInitStep {
    @Value("${aspspProfile.baseUrl}")
    private String profileBaseurl;

    @Autowired
    @Qualifier("aspsp-profile")
    private RestTemplate restTemplate;

    @Before
    public void initEmbedded() {
        this.restTemplate.put(profileBaseurl + "/aspsp-profile/for-debug/sca-approach", HttpEntityUtils.getHttpEntity("EMBEDDED"));
        initProfile(true);
    }

    private void initProfile(Boolean signingBasketSupported) {
        AspspSettings settings = restTemplate.getForObject(
            profileBaseurl + "/aspsp-profile", AspspSettings.class);
        settings = new AspspSettings(
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

        this.restTemplate.put(profileBaseurl + "/aspsp-profile/for-debug/aspsp-settings", HttpEntityUtils.getHttpEntity(settings));

    }
}
