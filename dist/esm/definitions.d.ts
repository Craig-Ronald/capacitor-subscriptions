import { PluginListenerHandle } from "@capacitor/core";
export interface SubscriptionsPlugin {
    /**
     * A test method which just returns what is passed in
     */
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    /**
     * Receives a product ID and returns the product details
     * @param options.productId The product ID to lookup
     */
    getProductDetails(options: {
        productIdentifier: string;
    }): Promise<ProductDetailsResponse>;
    /**
     * Receives the product ID which the user wants to purchase and returns the transaction ID
     * @param options.productId contains the productIdentifier
     */
    purchaseProduct(options: {
        productIdentifier: string;
    }): Promise<PurchaseProductResponse>;
    getCurrentEntitlements(): Promise<CurrentEntitlementsResponse>;
    getLatestTransaction(options: {
        productIdentifier: string;
    }): Promise<LatestTransactionResponse>;
    manageSubscriptions(): any;
    setGoogleVerificationDetails(options: {
        googleVerifyEndpoint: string;
        bid: string;
    }): void;
    addListener(eventName: 'ANDROID-PURCHASE-RESPONSE', listenerFunc: (response: AndroidPurchasedTrigger) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
}
export interface Product {
    productIdentifier: string;
    price: string;
    displayName: string;
    description: string;
}
export interface Transaction {
    productIdentifier: string;
    expiryDate: string;
    originalId: string;
    transactionId: string;
    originalStartDate: string;
    isTrial?: boolean;
}
export interface LatestTransactionResponse {
    responseCode: LatestTransactionResponseCode;
    responseMessage: LatestTransactionResponseMessage;
    data?: Transaction;
}
export declare type LatestTransactionResponseCode = -1 | 0 | 1 | 2 | 3;
export declare type LatestTransactionResponseMessage = "Incompatible with web" | "Successfully found the latest transaction matching given productIdentifier" | "Could not find a product matching the given productIdentifier" | "No transaction for given productIdentifier, or it could not be verified" | "Unknown problem trying to retrieve latest transaction";
export interface CurrentEntitlementsResponse {
    responseCode: CurrentEntitlementsResponseCode;
    responseMessage: CurrentEntitlementsResponseMessage;
    data?: Transaction[];
}
export declare type CurrentEntitlementsResponseCode = -1 | 0 | 1 | 2;
export declare type CurrentEntitlementsResponseMessage = "Incompatible with web" | "Successfully found all entitlements across all product types" | "No entitlements were found" | "Unknown problem trying to retrieve entitlements";
export interface PurchaseProductResponse {
    responseCode: PurchaseProductIOSResponseCode | PurchaseProductAndroidResponseCode;
    responseMessage: PurchaseProductIOSResponseMessage | PurchaseProductAndroidResponseMessage;
}
export declare type PurchaseProductIOSResponseCode = -1 | 0 | 1 | 2 | 3 | 4 | 5;
export declare type PurchaseProductIOSResponseMessage = "Incompatible with web" | "Successfully purchased product" | "Could not find a product matching the given productIdentifier" | "Product seems to have been purchased but the transaction failed verification" | "User closed the native popover before purchasing" | "Product request made but is currently pending - likely due to parental restrictions" | "An unknown error occurred whilst in the purchasing process";
export declare type PurchaseProductAndroidResponseCode = -1 | 0 | 1;
export declare type PurchaseProductAndroidResponseMessage = "Incompatible with web" | "Successfully opened native popover" | "Failed to open native popover";
export interface ProductDetailsResponse {
    responseCode: ProductDetailsResponseCode;
    responseMessage: ProductDetailsResponseMessage;
    data?: Product;
}
export declare type ProductDetailsResponseCode = -1 | 0 | 1;
export declare type ProductDetailsResponseMessage = "Incompatible with web" | "Successfully found the product details for given productIdentifier" | "Could not find a product matching the given productIdentifier";
export interface AndroidPurchasedTrigger {
    fired: boolean;
}
