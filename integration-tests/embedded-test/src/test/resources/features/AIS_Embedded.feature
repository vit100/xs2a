Feature: Account Information Service - Embedded approach
#
#    ####################################################################################################################
#    #                                                                                                                  #
#    # Consent Requests                                                                                                 #
#    #                                                                                                                  #
#    ####################################################################################################################
    Scenario Outline: Successful consent creation with explicit start of authorisation
        Given PSU wants to create a consent <consent-resource>
        And parameter TPP-Explicit-Authorisation-Preferred is <auth-preferred>
        When PSU sends the create consent request
        Then a successful response code and the appropriate consent response data is delivered to the PSU
        And response contains link startAuthorisation
        Examples:
            | consent-resource                     | auth-preferred |
            | consent-dedicated-successful.json    |     true       |
            | consent-all-accounts-successful.json |     true       |

    Scenario Outline: Successful consent creation with implicit start of authorisation
        Given PSU wants to create a consent <consent-resource>
        And parameter TPP-Explicit-Authorisation-Preferred is <auth-preferred>
        When PSU sends the create consent request
        Then a successful response code and the appropriate consent response data is delivered to the PSU
        And response contains link startAuthorisationWIthPsuAuthentication
        Examples:
            | consent-resource                     | auth-preferred |
            | consent-dedicated-successful.json    |     false      |
            | consent-dedicated-successful.json    |     false      |
            | consent-dedicated-successful.json    |     null       |
            | consent-dedicated-successful.json    |     null       |
            | consent-all-accounts-successful.json |     false      |
            | consent-all-accounts-successful.json |     false      |
            | consent-all-accounts-successful.json |     null       |
            | consent-all-accounts-successful.json |     null       |
