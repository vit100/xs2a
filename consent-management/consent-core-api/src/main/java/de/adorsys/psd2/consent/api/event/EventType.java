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

package de.adorsys.psd2.consent.api.event;

public enum EventType {
    CANCEL_PAYMENT,
    CONFIRM_FUNDS,
    CREATE_AIS_CONSENT,
    CREATE_AIS_CONSENT_AUTHORISATION,
    CREATE_PAYMENT,
    CREATE_PIS_CONSENT_AUTHORISATION,
    CREATE_PIS_CONSENT_CANCELLATION_AUTHORISATION,
    DELETE_AIS_CONSENT,
    GET_ACCOUNT_DETAILS,
    GET_ACCOUNT_LIST,
    GET_ACCOUNT_REPORT,
    GET_AIS_CONSENT,
    GET_AIS_CONSENT_STATUS,
    GET_BALANCES,
    GET_PAYMENT,
    GET_PAYMENT_CANCELLATION_AUTHORISATION,
    GET_PAYMENT_STATUS,
    GET_TRANSACTION_REPORT,
    UPDATE_AIS_CONSENT_PSU_DATA,
    UPDATE_PIS_CONSENT_CANCELLATION_PSU_DATA,
    UPDATE_PIS_CONSENT_PSU_DATA,
}
