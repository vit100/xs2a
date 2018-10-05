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

import de.adorsys.aspsp.aspspmockserver.domain.spi.payment.AspspPaymentPO;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    List<AspspPaymentPO> findByPaymentIdOrBulkId(String paymentId, String bulkId);

    List<AspspPaymentPO> findAll();

    Optional<AspspPaymentPO> findOne(String paymentId);

    boolean exists(String paymentId);

    AspspPaymentPO save(AspspPaymentPO aspspPayment);

    List<AspspPaymentPO> save(List<AspspPaymentPO> list);

    void deleteAll();
}
