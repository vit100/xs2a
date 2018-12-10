# Release notes v. 1.14

## Supported payment creation with pain.001 XML message
Now, an endpoint POST /v1/payments/{payment-product} supports creation of a single payment in pain.001 format. 
A TPP needs to provide body in pain.001 XML format and provide a required payment-product(pain.001-sepa-credit-transfers, etc). 
GET /v1/payments/{payment-id} endpoint was updated as well to support a single payment in pain.001 format and returns the whole XML body of it.
For this type of payment, HttpServletRequest body is read and transferred as a byte array. 
New Spi interface was added - CommomPaymentInterface, responsible for the creation of all types of pain.001 XML payments.
An Spi Developer should provide an implementaion of this interface to be able to operate pain.001 XML payments.  
