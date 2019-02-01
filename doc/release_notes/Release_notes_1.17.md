# Release notes v. 1.17

## Bugfix: Remove PSU Data from endpoint for getting consent by redirect id in CMS-PSU-API
PSU Data is no longer required for getting consent by redirect id.
As a result, headers `psu-id`, `psu-id-type`, `psu-corporate-id` and `psu-corporate-id-type` are no longer used in `psu-api/v1/ais/consent/redirect/{redirect-id}` endpoint.
PsuIdData was also removed as an argument from `de.adorsys.psd2.consent.psu.api.CmsPsuAisService#checkRedirectAndGetConsent` method.
