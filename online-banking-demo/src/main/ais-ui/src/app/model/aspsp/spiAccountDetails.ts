/**
 * XS2A REST API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0
 * Contact: fpo@adorsys.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { Balance } from './balance';
import { Links } from './links';


/**
 * SpiAccountDetails information
 */
export interface SpiAccountDetails {
    /**
     * links: inks to the account, which can be directly used for retrieving account information from the dedicated account
     */
    links?: Links;
    /**
     * Account Type: Product Name of the Bank for this account
     */
    accountType?: string;
    /**
     * Balances
     */
    balances?: Array<Balance>;
    /**
     * BBAN: This data element can be used in the body of the CreateConsentReq Request Message for retrieving account access consent from this account, for payment accounts which have no IBAN.
     */
    bban?: string;
    /**
     * BIC: The BIC associated to the account.
     */
    bic?: string;
    /**
     * Cash Account Type: PExternalCashAccountType1Code from ISO20022
     */
    cashAccountType?: SpiAccountDetails.CashAccountTypeEnum;
    /**
     * Currency Type
     */
    currency: string;
    /**
     * IBAN: This data element can be used in the body of the CreateConsentReq Request Message for retrieving account access consent from this payment account
     */
    iban?: string;
    /**
     * ID: This is the data element to be used in the path when retrieving data from a dedicated account
     */
    id: string;
    /**
     * MASKEDPAN: Primary Account Number (PAN) of a card in a masked form.
     */
    maskedPan?: string;
    /**
     * MSISDN: An alias to access a payment account via a registered mobile phone number.
     */
    msisdn?: string;
    /**
     * Name: Name given by the bank or the Psu in Online- Banking
     */
    name?: string;
    /**
     * PAN: Primary Account Number (PAN) of a card, can be tokenized by the ASPSP due to PCI DSS requirements
     */
    pan?: string;
}
export namespace SpiAccountDetails {
    export type CashAccountTypeEnum = 'CURRENT_ACCOUNT';
    export const CashAccountTypeEnum = {
        ACCOUNT: 'CURRENT_ACCOUNT' as CashAccountTypeEnum
    };
}
