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

package de.adorsys.psd2.xs2a.core.event;

public enum EventType {
    CONFIRM_FUNDS_REQUEST,
    CREATE_AIS_CONSENT_REQUEST,
    DELETE_AIS_CONSENT_REQUEST,
    GET_AIS_CONSENT_REQUEST,
    GET_AIS_CONSENT_STATUS_REQUEST,
    GET_CANCELLATION_AUTHORISATION_REQUEST,
    GET_PAYMENT_REQUEST,
    GET_TRANSACTION_STATUS_REQUEST,
    CANCEL_PAYMENT_REQUEST,
    INITIATE_PAYMENT_REQUEST,
    READ_ACCOUNT_DETAILS_REQUEST,
    READ_ACCOUNT_LIST_REQUEST,
    READ_BALANCE_REQUEST,
    READ_TRANSACTION_DETAILS_REQUEST,
    READ_TRANSACTION_LIST_REQUEST,
    START_AIS_CONSENT_AUTHORISATION_REQUEST,
    START_PIS_CONSENT_AUTHORISATION_REQUEST,
    START_PIS_CONSENT_CANCELLATION_AUTHORISATION_REQUEST,
    UPDATE_AIS_CONSENT_PSU_DATA_REQUEST,
    UPDATE_PAYMENT_CANCELLATION_PSU_DATA_REQUEST,
    UPDATE_PAYMENT_INITIATION_PSU_DATA_REQUEST,
}
