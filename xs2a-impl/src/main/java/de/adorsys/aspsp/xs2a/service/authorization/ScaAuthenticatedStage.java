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

package de.adorsys.aspsp.xs2a.service.authorization;

import de.adorsys.aspsp.xs2a.config.factory.ScaStage;
import de.adorsys.aspsp.xs2a.service.authorization.pis.PisAuthorisationService;
import de.adorsys.aspsp.xs2a.service.consent.PisConsentDataService;
import de.adorsys.aspsp.xs2a.service.mapper.consent.SpiCmsPisMapper;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisConsentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisConsentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisConsentPsuDataResponse;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaMethod;
import de.adorsys.psd2.xs2a.spi.domain.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.service.PaymentAuthorizationSpi;
import de.adorsys.psd2.xs2a.spi.service.PaymentSpi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static de.adorsys.psd2.consent.api.CmsScaStatus.SCAMETHODSELECTED;

@Slf4j
@Service("PSUAUTHENTICATED")
public class ScaAuthenticatedStage extends ScaStage<UpdatePisConsentPsuDataRequest,
    GetPisConsentAuthorisationResponse, UpdatePisConsentPsuDataResponse> {

    public ScaAuthenticatedStage(PaymentSpi paymentSpi, PaymentAuthorizationSpi authorisationSpi,
                                 PisAuthorisationService pisAuthorisationService, SpiCmsPisMapper spiCmsPisMapper,
                                 PisConsentDataService pisConsentDataService) {
        super(paymentSpi, authorisationSpi, pisAuthorisationService, spiCmsPisMapper, pisConsentDataService);
    }

    @Override
    public UpdatePisConsentPsuDataResponse apply(UpdatePisConsentPsuDataRequest request,
                                                 GetPisConsentAuthorisationResponse pisConsentAuthorisationResponse) {
        // TODO get it from XS2A Interface https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/332
        SpiPsuData psuData = new SpiPsuData(request.getPsuId(), null, null, null);
        SpiScaMethod method = getMethod(request.getAuthenticationMethodId());
        AspspConsentData aspspConsentData =
            pisConsentDataService.getAspspConsentDataByPaymentId(request.getPaymentId());

        aspspConsentData = authorisationSpi.requestAuthorisationCode(psuData, method, aspspConsentData).getAspspConsentData();
        pisConsentDataService.updateAspspConsentData(aspspConsentData);

        request.setScaStatus(SCAMETHODSELECTED);
        return pisAuthorisationService.doUpdatePisConsentAuthorisation(request);
    }

    private SpiScaMethod getMethod(String method) { //TODO: https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/332
        SpiScaMethod scaMethod = SpiScaMethod.SMS_OTP;
        try {
            scaMethod = SpiScaMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            log.error("Sca Method could not be parsed", e.getLocalizedMessage());
        }
        return scaMethod;
    }
}
