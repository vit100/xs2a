Feature: Account Information Service - Embedded approach
#
#    ####################################################################################################################
#    #                                                                                                                  #
#    # Consent Requests                                                                                                 #
#    #                                                                                                                  #
#    ####################################################################################################################
    Scenario Outline: Successful consent creation with explicit start of authorisation
        Given PSU wants to create a consent <consent-resource>
        And  ASPSP-profile contains parameter signingBasketSupported is <signingBasketSupported>
        And parameter TPP-Explicit-Authorisation-Preferred is <auth-preferred>
        When PSU sends the create consent request
        Then a successful response code and the appropriate consent response data is delivered to the PSU
        And response contains link startAuthorisation
        Examples:
            | consent-resource                     | signingBasketSupported | auth-preferred |
            | consent-dedicated-successful.json    |         true           |     true       |
            | consent-all-accounts-successful.json |         true           |     true       |

    Scenario Outline: Successful consent creation with implicit start of authorisation
        Given PSU wants to create a consent <consent-resource>
        And  ASPSP-profile contains parameter signingBasketSupported is <signingBasketSupported>
        And parameter TPP-Explicit-Authorisation-Preferred is <auth-preferred>
        When PSU sends the create consent request
        Then a successful response code and the appropriate consent response data is delivered to the PSU
        And response contains link startAuthorisationWIthPsuAuthentication
        Examples:
            | consent-resource                     | signingBasketSupported | auth-preferred |
            | consent-dedicated-successful.json    |         false          |     true       |
            | consent-dedicated-successful.json    |         true           |     false      |
            | consent-dedicated-successful.json    |         false          |     false      |
            | consent-dedicated-successful.json    |         true           |     null       |
            | consent-dedicated-successful.json    |         false          |     null       |
            | consent-all-accounts-successful.json |         false          |     true       |
            | consent-all-accounts-successful.json |         true           |     false      |
            | consent-all-accounts-successful.json |         false          |     false      |
            | consent-all-accounts-successful.json |         true           |     null       |
            | consent-all-accounts-successful.json |         false          |     null       |


#    ####################################################################################################################
#    #                                                                                                                  #
#    # SCA Status for consent creation                                                                                  #
#    #                                                                                                                  #
#    ####################################################################################################################
    Scenario Outline: Successful SCA status request for consent creation
        Given PSU created a consent resource <consentId>
        And get authorisation sub-resource
        And AISP wants to get SCA status
        When AISP request SCA status for consent creation
        Then a successful response code and sca status in response gets returned
        Examples:
            | consentId |
            | consent-dedicated-successful.json|
            | Get-Successful-SCA-status-for-consent-creation.json|



