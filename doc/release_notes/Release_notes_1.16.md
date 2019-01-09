# Release notes v. 1.16

## Removed availablePaymentProducts and availablePaymentTypes from Aspsp-Profile
Now ASPSP supports payment-product/payment-type matrix instead of separate properties availablePaymentProducts and availablePaymentTypes. The properties were removed from Aspsp-Profile.

## Aspsp-Profile supports matrix payment-product/payment-type
ASPSP now has a possibility to chose which payment-product/payment-type to work with. Now to set available payment products for each type, the following table in bank_profile.yaml
should be filled:

**supportedPaymentTypeAndProductMatrix**:

  *SINGLE*:
   - sepa-credit-transfers
   - instant-sepa-credit-transfers
   
  *PERIODIC*:
   - sepa-credit-transfers
   - instant-sepa-credit-transfers
   
  *BULK*:
   - sepa-credit-transfers
   - instant-sepa-credit-transfers
  
Other payment products can be added for every payment type.
