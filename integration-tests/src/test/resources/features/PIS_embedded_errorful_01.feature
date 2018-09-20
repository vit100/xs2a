Feature: Payment Initiation Service - Embedded Approach

    ####################################################################################################################
    #                                                                                                                  #
    # Start authorisation with PSU identification                                                                      #
    #                                                                                                                  #
    ####################################################################################################################
    Scenario Outline: Errorful creation of an authorisation resource
        Given PSU initiated a payment <payment-service> with the payment-id <payment-id>
        And wants to start the authorisation using <authorisation-data>
        When PSU sends the errorful start authorisation request
        Then an error response code and the appropriate error response are received
        Examples:
            | payment-service   | payment-id                           | authorisation-data                             |
            | payments          | a9115f14-4f72-4e4e-8798-10101010xxxx | startAuthorisation-not-existing-paymentId.json |
#            | payments          | a9115f14-4f72-4e4e-8798-202808e85238 | startAuthorisation-no-request-id.json          |
#            | payments          | a9115f14-4f72-4e4e-8798-202808e85238 | startAuthorisation-wrong-format-request-id.json|
#            | recurring-payments| a9115f14-4f72-4e4e-8798-202808e85238 | startAuthorisation-wrong-payment-service.json  |

    #TODO exchange commented payment ids with valid ones

