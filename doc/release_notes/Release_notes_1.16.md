# Release notes v. 1.16


## Security Fix:  webpack-dev-server
webpack-dev-server and corresponding dependencies were updated.

[CVE-2018-14732](https://nvd.nist.gov/vuln/detail/CVE-2018-14732)

An issue was discovered in lib/Server.js in webpack-dev-server before 3.1.11.
Attackers are able to steal developer's code because the origin of requests is not checked by the WebSocket server, which is used for HMR (Hot Module Replacement). Anyone can receive the HMR message sent by the WebSocket server via a ws://127.0.0.1:8080/ connection from any origin.

## Expire AIS consent for TPP made by PSU when new AIS consent is created and authorised
The number of user AIS consents given to TPP is restricted: for one PSU can be only one consent given to the specific TPP.
New conditions:
* when new AIS consent is authorised and its `recurringIndicator` property is `true`, the previous consents statuses are set to `TERMINATED_BY_TPP`;
* if consent `recurringIndicator` property is `false`, the consent status is set to `EXPIRED` after consent has been used;
* when PSU send a consent creation request with the following data: `recurringIndicator` property is `false` AND `frequencyPerDay` is more than `1`, 
the response with `400 FORMAT_ERROR` is returned.

## Added new options to ASPSP profile
| Option                                       | Meaning                                                                                                | Default value | 
|----------------------------------------------|--------------------------------------------------------------------------------------------------------|---------------|
| availableAccountsConsentSupported            | This field indicates if ASPSP supports available accounts for a consent                                | true          |
| scaByOneTimeAvailableAccountsConsentRequired | This field indicates if ASPSP requires usage of SCA to validate a one-time available accounts consent  | true          |

## Extend a list of Transaction Statuses for PIS
New version of API Yaml file published by Berlin Group contains three new statuses for Transactions:
| Code | Value                               | Provided description                                                                                                                                                   |
|------|-------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ACCC | AcceptedSettlementCompletedCreditor | Settlement on the creditor's account has been completed.                                                                                                               |
| ACFC | AcceptedFundsChecked                | Preceeding check of technical validation and customer profile was successful and an automatic funds check was positive.                                                |
| PATC | PartiallyAcceptedTechnicalCorrect   |  The payment initiation needs multiple authentications, where some but not yet all have been performed. Syntactical and semantical validations are successful.         |

XS2A Classes were updated with these new values, so that they may be used in SPI level.

## Bugfix: Fix empty SpiAccountAccess being provided in SpiAccountConsent in some cases
Now `SpiAccountConsent` argument contains proper `SpiAccountAccess` in `de.adorsys.psd2.xs2a.spi.service.AccountSpi#requestAccountList`
method when no accesses were previously provided by the connector in a response to AIS consent initiation.