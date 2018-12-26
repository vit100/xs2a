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

package de.adorsys.psd2.consent.config;

import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.consent.repository.AisConsentRepository;
import de.adorsys.psd2.consent.service.scheduler.ConsentExpirationScheduleTask;
import de.adorsys.psd2.consent.service.scheduler.trigger.ConsentExpirationScheduleTaskTrigger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig implements SchedulingConfigurer {
    private final AisConsentRepository aisConsentRepository;
    private final AspspProfileService aspspProfileService;

    @Value("${scheduler.pool.size:20}")
    private int poolSize;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
        threadPool.setPoolSize(poolSize);
        threadPool.setThreadNamePrefix("consent-scheduler-pool");
        threadPool.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPool);

        scheduledTaskRegistrar.addTriggerTask(
            consentExpirationScheduleTask(),
            consentExpirationScheduleTaskTrigger()
        );
    }

    @Bean
    public ConsentExpirationScheduleTask consentExpirationScheduleTask() {
        return new ConsentExpirationScheduleTask(aisConsentRepository);
    }

    @Bean
    public ConsentExpirationScheduleTaskTrigger consentExpirationScheduleTaskTrigger() {
        return new ConsentExpirationScheduleTaskTrigger(aspspProfileService);
    }
}
