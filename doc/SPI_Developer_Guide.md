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

We distinguished between following Interfaces: 

1. **SinglePaymentSpi**:  Interface to be used for the single payment Implementation

    . The following Methods have to be implemented: **initiatePayment** (aims to initiate a Payment), **getPaymentById** (aims to read the pay,ent by id )and 
    **getPaymentStatusById** (aims to read the payment status by id). 
    
    * Response by the methods "initiate Payment" will returns a positive or negative payment initiation response as a part of SpiResponse and will contains the following: 
        * **contextData**: holder of call's context data (e.g. about **PsuData** and **TppInfo** )
   
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
        - "Redirect URI": URI of TPP, where the transaction flow shall be redirected to after a Redirect. Mandated for the **Redirect SCA Approach** (including OAuth2 SCA approach), specially when TPP-Redirect-preferred equals "true". It is recommended to always use this header field. 
        - "Nok redirect URI": if this URI is contained, the TPP is asking to redirect the transaction flow to this address instead of the TPP-Redirect-URI in case of a negative result of the redirect ScaMethod. This might be ignored by the ASPSP
        
        * **payment**: payment, that extends SpiPayment (Single Payment)

        * **initialAspspConsentData** Encrypted data to be stored in the consent management system




   * Response by the methods "getPaymentById" will returns payment as a part of SpiResponse and will contains the following data: 

        **contextData**, **payment** (Single Payment), And 

        **aspspConsentData**: This is used as a container of some binary data to be used on SPI level. Spi developers may save here necessary information, that will be stored and encrypted in consent. This shall not use without consentId!
                         Encrypted data that may be stored in the consent management system in the consent linked to a request.They may be null if consent does not contain such data, or request is not done from a workflow with a consent. 
                         
                         
   * Response by the methods "getPaymentStatusById" will contains the following: **contextData**, **payment** and **aspspConsentData**. This method will return a response object, which contains the transaction status



2. **PeriodicPaymentSpi**: Interface to be used for periodic payment SPI implementation

    The following Methods have to be implemented: **initiatePayment**  **getPaymentById** and **getPaymentStatusById**. 
    
    * Response by the methods "initiate Payment" will returns a positive or negative payment initiation response as a part of SpiResponse and will contains the following: 
        * **contextData**: holder of call's context data (e.g. about **PsuData** and **TppInfo** )
   
        * **payment**: Periordic Payment

        * **initialAspspConsentData** Encrypted data to be stored in the consent management system


   * Response by the methods "getPaymentById" will returns payment as a part of SpiResponse and will contains the following data: 

        **contextData**, 
    
        **payment** (Periordic Payment)

        **aspspConsentData**: This is used as a container of some binary data to be used on SPI level. Spi developers may save here necessary information, that will be stored and encrypted in consent. This shall not use without consentId!
                         Encrypted data that may be stored in the consent management system in the consent linked to a request.They may be null if consent does not contain such data, or request is not done from a workflow with a consent. 
                         
                         
   * Response by the methods "getPaymentStatusById" will contains the following: **contextData**, **payment** and **aspspConsentData**. This method will return a response object, which contains the transaction status


3. **BulkPaymentSpi**: Interface to be used for bulk payment SPI implementation

    The following Methods have to be implemented: **initiatePayment**  **getPaymentById** and **getPaymentStatusById**.

    * Response by the methods "initiate Payment" will returns a positive or negative payment initiation response as a part of SpiResponse and will contains the following: 
        * **contextData**: holder of call's context data (e.g. about **PsuData** and **TppInfo** )
   
        * **payment**: Bulk Payment

        * **initialAspspConsentData** Encrypted data to be stored in the consent management system

    * Response by the methods "getPaymentById" will returns payment as a part of SpiResponse and will contains the following data: 

        **contextData**, 
    
        **payment** (Bulk Payment)

        **aspspConsentData**: This is used as a container of some binary data to be used on SPI level. Spi developers may save here necessary information, that will be stored and encrypted in consent. This shall not use without consentId!
                         Encrypted data that may be stored in the consent management system in the consent linked to a request.They may be null if consent does not contain such data, or request is not done from a workflow with a consent. 
                         
                         
   * Response by the methods "getPaymentStatusById" will contains the following: **contextData**, **payment** and **aspspConsentData**. This method will return a response object, which contains the transaction status


                         
The Payment initiation depends heavily on the **Strong Customer Authentication (SCA)** approach implemented by the ASPSP. The Berlin Group describes four approaches to umplement this, but we currently done this with 
  3 Approaches (REDIRECT, DECOUPLED and EMBEDDED). 
  
#### SCA Approach REDIRECT

-- Prerequisites in case of **consent for payment initiation**

    - PSU initaited a payment by using TPP
    - PSU is authenticated via two factors: for example PsuId and passwort
    - Each Payment initiation needs it own consent!

After the Payment Initiation is created, it has to be authorise from the PSU. In case of redirect approach the authorisation can be explicit or implicit.
    
 1. **The explicit Start of the authorisation** process means that the Payment initiation Request is followed by an explicit Request of the TPP to start the authorisation. This is followed by a redirection to the ASPSP SCA authorisation site. 
 A status request might be requested by the TPP after the session is reredirected to the TPP's system: 
   
    * PSU initiates a payment via a TPP and the TPP make a Payment-Initiation Request to the ASPSP
    * After the Payment Initiation is created, TPP have to send to ASPSP an Authorisation Request for the initiated payment
    * TPP have to inform the customer to authorise the transaction redirectly
    * The PSU will be redirected to an authorisation webpage of ASPSP and authorises the payment with TAN confirmation received via email.
    * The TPP can get the Payment'status from ASPSP by making a Payment-Status Request
    
 2. In case of **implicit Start of the Authorisation process** the ASPSP needed no additional data from TPP. In this case, the redirection of the PSU browser session happens 
 directly after the Payment Initiation Response. In addition an SCA status request can be sent by the TPP to follow the SCA process.
 
   * PSU initiates a payment via a TPP and the TPP make a Payment-Initiation Request to the ASPSP
   * After the Payment Initiation Response, the PSU have to authorise the transaction
   * The PSU will be redirected to an authorisation Webpage of ASPSP and authorises the payment with TAN confirmation received via email.
   * The TPP can get the Payment'status from ASPSP by making a Payment-Status Request
    

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
