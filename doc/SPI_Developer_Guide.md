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
  
#### SCA Approach REDIRECT

#### SCA Approach DECOUPLED
* The authorisation of Payment in this case is only with an implicit start of the authorisation process
* The Transaction flow in this approach is similar to the Redirect SCA Approach
* ASPSP is asking the Psu to authorise the payment via dedicated mobile app, or any other application or device which is independent from the online banking frontend.
* The ASPSP is asking the TPP to inform the PSU abut this authentication by sending a corresponding PSU Message like _Please use your xxx App to authorise the payment_.
* After the SCA having been processed between ASPSP and PSU, the TPP then needs to ask for the result of the transaction.


* In case of implicit start the authorisation will be used based on tppExplicitAuthorisationPreferred and signingBasketSupported values.  
    * Implicit authorisation will be used in all the cases where tppExplicitAuthorisationPreferred or signingBasketSupported not equals true
    * Implicit approach is impossible in case of multilevel SCA

* For The decoupled approach the developer need the following methods: 

    * createCommonPaymentAuthorisation: this method will create payment authorisation response and contains:
        * **paymentId**: ASPSP identifier of a payment,
        * **paymentType**: e.g. single payment, periodic payment, bulk payment
        * **psudata**: PsuIdData container of authorisation data about PSU
        
    * updateCommonPaymentPsuData: contains **request** that provides transporting data when updating consent psu data. It will return the update payment authorization response
    
    * getAuthorisationSubResources with the **paymentId** and returns authorisation sub resources
    
    * getAuthorisationScaStatus with **paymentId** (ASPSP identifier of the payment, associated with the authorisation) and 
    **authorisationId** (authorisation identifier). This method will returns SCA status
    
    * getScaApproachServiceTypeProvider: to get sca approach used in current service. This will return the ScaApproach **"Decoupled"**
 

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
