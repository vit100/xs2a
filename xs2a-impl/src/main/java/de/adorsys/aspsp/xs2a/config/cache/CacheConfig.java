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

package de.adorsys.aspsp.xs2a.config.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {
    public final static String ASPSP_PROFILE_CACHE = "aspspProfileCash";
    public final static String SCA_METHODS_CACHE = "scaMethodCash";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        GuavaCache profileCache = new GuavaCache(ASPSP_PROFILE_CACHE, CacheBuilder.newBuilder()
                                                                    .expireAfterWrite(60, TimeUnit.SECONDS)
                                                                    .build());

        GuavaCache scaMethodsCache = new GuavaCache(SCA_METHODS_CACHE, CacheBuilder.newBuilder()
                                                                    .expireAfterWrite(60, TimeUnit.SECONDS)
                                                                    .build());
        simpleCacheManager.setCaches(Arrays.asList(profileCache, scaMethodsCache));
        return simpleCacheManager;
    }
}
