# SPI Developer Guide

## Purpose and Scope

## Choosing the deployment layout

### Microservices Deployment

### Embedded Deployment

### Embedding XS2A Library

### Embedding CMS Library

### Embedding Profile library

## Setting up ASPSP Profile options

### Using debug interface

## Implementing SPI-API

### General requirements

#### SpiResponse

#### Work with ASPSP-Consent-Data object

### Implementation of AisConsentSpi

#### Providing account resources to consent 

#### SCA Approach REDIRECT

#### SCA Approach DECOUPLED

#### SCA Approach EMBEDDED

### Implementation of AccountSpi

### Implementation of PaymentSpi(s)


     
**SPI** means Single Payment Interface. that is an API intended to be implemented or extended by a third party. 

To implement this you need to initiate a payment like in the following code: 

```
public interface SinglePaymentSpi extends PaymentSpi<SpiSinglePayment, SpiSinglePaymentInitiationResponse> {
    @Override
    @NotNull
    SpiResponse<SpiSinglePaymentInitiationResponse> initiatePayment(@NotNull SpiContextData contextData, @NotNull SpiSinglePayment payment, @NotNull AspspConsentData initialAspspConsentData);

    @Override
    @NotNull
    SpiResponse<SpiSinglePayment> getPaymentById(@NotNull SpiContextData contextData, @NotNull SpiSinglePayment payment, @NotNull AspspConsentData aspspConsentData);

    @Override
    @NotNull
    SpiResponse<SpiTransactionStatus> getPaymentStatusById(@NotNull SpiContextData contextData, @NotNull SpiSinglePayment payment, @NotNull AspspConsentData aspspConsentData);
}
```


 1. **SpiContextData**: This object represents known Context of call, provided by this or previous requests in scope of one process (e.g. one payment).
   It contains **PsuData** and **tppInfo**.
   
   - **PsuData** contains data about PSU known in scope of the request: 
   
     | Attribute         |  Type   |      Condition    | Description       |
     | :---              |  :---:  |          :---:    |          :---     |
     | PsuId             | String  |      Conditional  | Client ID of the PSU in the ASPSP client interface.|
     | psuIdType         | String  |      Conditional  | Type of the PsuId, needed in scenarios where PSUs have several PsuIds as access possibility|
     | psuCorporateId    | String  |      Conditional  | Identification of a Corporate in the Online Channels. Might be mandated in the ASPSP’s documentation. Only used in a corporate context.|
     | psuCorporateIdType| String  |      Conditional  | This is describing the type of the identification needed by the ASPSP to identify the psuCorporateId|
     
     
   - **tppInfo** contains information about the Tpp's certificate:
        - "Registration number": example = "1234_registrationNumber",
        - "Tpp name": example = "Tpp company",
        - "National competent authority": example = "Bafin",
        - "Redirect URI": URI of TPP, where the transaction flow shall be redirected to after a Redirect. Mandated for the **Redirect SCA Approach** (including OAuth2 SCA approach), specially when TPP-Redirect-prefferred equals "true". It is recommended to always use this header field. 
        - "Nok redirect URI": if this URI is contained, the TPP is asking to redirect the transaction flow to this address instead of the TPP-Redirect-URI in case of a negative result of the redirect ScaMethod. This might be ignored by the ASPSP
  
   
2. **AspspConsentData**: This is used as a container of some binary data to be used on SPI level. Spi developers may save here necessary information, that will be stored and encrypted in consent. This shall not use without consentId!
                         Encrypted data that may be stored in the consent management system in the consent linked to a request.They may be null if consent does not contain such data, or request is not done from a workflow with a consent. 
    
                         
When a payment is initiated the response code is generated with following data: 
  
 
  | Data                        |           Type                  |      Condition  | Description       |
  | :---                        |     :---:                       |          :---:  |          :---     |
  | transactionStatus           | Transaction Status              | Mandatory       | This Status can be **RCVD** = Payment initiation has been received by the receiving agent. **PDNG** = Payment initiation or individual transaction included in the payment initiation is pending. Further checks and status update will be performed or **RJCT** = Payment initiation or individual transaction included in the payment initiation has been rejected.|
  | paymentId                   | String                          | Mandatory       | Resource identification of the generated payment initiation resource. It can  help us to check, if the status of the initiated payment|
  | spiTransaFees               | Amount                          | Optional        | Cost, that must pay by processing an electronic payment for a customer. Can be used by the ASPSP to transport transaction. Fees are relevant for the underlying payments|
  | spiTransactionFeeIndicator  | Boolean                         | Optional        | Resource identification of the generated payment initiation resource. It can  help us to check, if the status of the initiated payment|
  | scaMethods                  | Array of authentication objects | Conditional     | This data element might be contained, if SCA is required and if PSU has a choice between different authentication methods. Depending on the risk management of the ASPSP this choice might be offered before or after the PSU has been identified with thr first relevant factor, or if an access token is transported|
  | chosenScaMethod             | Authentication object           | Conditional     | This data element is only contained in the response if the ASPSP has chosen the Embedded SCA Approach, if the PSU is already identified e.g. with the first relevant factor or alternatively an access token, if SCA is required and if the authentication method is implicitly selected|
  | challengeData               | Challenge                       | Conditional     | It is contained in addition to the data element "chosenScaMethod" if challenge data is needed for SCA|
  | psuMessage                  | Max512Text                      | Optional        | Text to be displayed to the PSU                 |
  | tppMessages                 | Array of TPP Message Information| Optional        | Messages to the TPP on operational issues       |
  | aspspAccountId              |                                 |                 |                                                 |
  
  
#### SCA Approach REDIRECT



#### SCA Approach DECOUPLED

#### SCA Approach EMBEDDED

### Implementation of FundsConfirmationSpi

## Working with CMS

### Using the CMS-PSU-API

#### SCA Approaches REDIRECT and DECOUPLED

#### SCA Approach EMBEDDED

### Using the CMS-ASPSP-API

#### Using the Tpp locking interface

#### Using the Consents/Payments export interface

#### Using the FundsConfirmation Consent interface

## Special modes

### Multi-tenancy support
