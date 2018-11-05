# Consent Management Library

This module provides implementation for Consent APIs with an embeddable java-library.

For usage as a separate service please use 
* [consent-management-server](../consent-management-server) for service starter
* [consent-management-remote](../consent-management-remote) for xs2a-client connection.


**WARNING:**  `For encryption\decryption aspsp data we use secret server key ('server_key') which reads from Environment variables.  
So, before start project you need to create environment variables and put value, as example:  'server_key=mySecretKey'` 
