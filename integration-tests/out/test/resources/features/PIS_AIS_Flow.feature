Feature: Payment Initiation Service to Account Initiation Service

#    Scenario Outline: Successful payment initiation request for single payments (redirect)
#        Given PSU wants to initiate a single payment <single-payment> using the payment service <payment-service> and the payment product <payment-product>
#        When PSU sends the single payment initiating request
#        Then a successful response code and the appropriate payment response data are received
#        And a redirect URL is delivered to the PSU
#        Examples:
#            | payment-service | payment-product       | single-payment                |
#            | payments        | sepa-credit-transfers | singlePayInit-successful.json |
