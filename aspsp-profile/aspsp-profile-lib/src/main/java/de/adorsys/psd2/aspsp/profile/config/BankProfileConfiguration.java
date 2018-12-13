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

package de.adorsys.psd2.aspsp.profile.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Slf4j
@Configuration
public class BankProfileConfiguration {

    private static final String DEFAULT_BANK_PROFILE = "classpath:bank_profile.yml";

    @Value("${bank_profile.path:" + DEFAULT_BANK_PROFILE + "}")
    private URL bankProfileUrl;

    @Bean
    public ProfileConfiguration profileConfiguration() throws IOException {
        URL url = Optional.ofNullable(bankProfileUrl)
            .orElse(new URL(DEFAULT_BANK_PROFILE));

        return new Yaml(getDumperOptions()).loadAs(url.openStream(), ProfileConfiguration.class);
    }

    private DumperOptions getDumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

}