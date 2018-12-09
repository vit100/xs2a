Feature: Confirmation of Funds Service - Common

    Scenario Outline: Request confirmation of funds successfully
        Given PSU initiated card based payment transaction using <data> at a PSU – TPP interface
        And wants to get a confirmation <result> of <funds> on his account
        When confirmation of funds request is received in xs2a
        Then a successful response code 200 and fundsAvailable is returned
        Examples:
            | data                                |funds  |result  |
            | fund-confirmation-successful.json   | 49999 | true   |
            | fund-confirmation-successful.json   | 50000 | true   |
            | fund-confirmation-successful.json   | 50001 | false  |


    Scenario Outline: Request confirmation of funds errorfully
        Given PSU initiated errorFull card based payment transaction using <data> at a PSU – TPP interface
        When confirmation of funds request is received in xs2a
        Then an error response code is displayed and an appropriate error response is shown
        Examples:
            | data                               |
            | fund-confirmation-wrong-iban.json  |
            | fund-confirmation-wrong-currency.json |
            | fund-confirmation-wrong-request-id.json |
