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

package de.adorsys.psd2.consent.service.scheduler.trigger;

import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class ConsentExpirationScheduleTaskTrigger extends PeriodicTrigger {
    private final static long INITIAL_INVOCATION_PERIOD_MS = 1000;

    private AspspProfileService aspspProfileService;

    @Autowired
    public ConsentExpirationScheduleTaskTrigger(AspspProfileService aspspProfileService) {
        super(INITIAL_INVOCATION_PERIOD_MS, TimeUnit.MILLISECONDS);
        this.aspspProfileService = aspspProfileService;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        long period = aspspProfileService.getAspspSettings().getConsentExpirationSchedulerInvokingPeriodMs();

        if (triggerContext.lastScheduledExecutionTime() == null) {
            return new Date(System.currentTimeMillis() + period);
        }

        return new Date(triggerContext.lastCompletionTime().getTime() + period);
    }
}
