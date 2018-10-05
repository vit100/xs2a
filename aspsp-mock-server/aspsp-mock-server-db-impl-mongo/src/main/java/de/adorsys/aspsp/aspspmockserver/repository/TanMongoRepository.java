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

package de.adorsys.aspsp.aspspmockserver.repository;

import de.adorsys.aspsp.aspspmockserver.domain.spi.psu.TanPO;
import de.adorsys.aspsp.aspspmockserver.domain.spi.psu.TanStatusPO;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Profile({"mongo", "fongo"})
public interface TanMongoRepository extends MongoRepository<TanPO, String> {
    List<TanPO> findByPsuIdAndTanStatus(String psuId, TanStatusPO tanStatus);
}
