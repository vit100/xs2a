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

package de.adorsys.aspsp.xs2a.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "de.adorsys.aspsp.xs2a")
public class LayersAccessTest {

    @ArchTest
        public static final ArchRule layeredArchitecture = layeredArchitecture()
               .layer("Config")
                    .definedBy("de.adorsys.aspsp.xs2a.config..")
               .layer("Controller")
                    .definedBy("de.adorsys.aspsp.xs2a.web..")
               .layer("Service")
                    .definedBy("..xs2a.service..", "..xs2a.domain..")
               .layer("SPI")
                    .definedBy("de.adorsys.aspsp.xs2a.spi..")

               .whereLayer("Controller")
                    .mayOnlyBeAccessedByLayers("Config")
               .whereLayer("Service")
                    .mayOnlyBeAccessedByLayers("Controller", "Service", "Config")
               .whereLayer("SPI")
                    .mayOnlyBeAccessedByLayers("Service", "SPI", "Config");
}
