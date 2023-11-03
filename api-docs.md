# capacitor-subscriptions

A capacitor plugin which simplifies subscription handling - implementing StoreKit 2 and Google Billing 5

## Install

```bash
npm install *to-be-changed*
ionic cap sync
```

## API

<docgen-index>

* [`getProductDetails(...)`](#getproductdetails)
* [`purchaseProduct(...)`](#purchaseproduct)
* [`getCurrentEntitlements()`](#getcurrententitlements)
* [`getLatestTransaction(...)`](#getlatesttransaction)
* [`manageSubscriptions(...)`](#managesubscriptions)
* [`setGoogleVerificationDetails(...)`](#setgoogleverificationdetails)
* [`addListener('ANDROID-PURCHASE-RESPONSE', ...)`](#addlistenerandroid-purchase-response)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->


### getProductDetails(...)

```typescript
getProductDetails(options: { productIdentifier: string; }) => Promise<ProductDetailsResponse>
```

Receives a product ID and returns the product details

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`options`** | <code>{ productIdentifier: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#productdetailsresponse">ProductDetailsResponse</a>&gt;</code>

--------------------


### purchaseProduct(...)

```typescript
purchaseProduct(options: { productIdentifier: string; }) => Promise<PurchaseProductResponse>
```

Receives the product ID which the user wants to purchase and returns the transaction ID

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`options`** | <code>{ productIdentifier: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#purchaseproductresponse">PurchaseProductResponse</a>&gt;</code>

--------------------


### getCurrentEntitlements()

```typescript
getCurrentEntitlements() => Promise<CurrentEntitlementsResponse>
```

**Returns:** <code>Promise&lt;<a href="#currententitlementsresponse">CurrentEntitlementsResponse</a>&gt;</code>

--------------------


### getLatestTransaction(...)

```typescript
getLatestTransaction(options: { productIdentifier: string; }) => Promise<LatestTransactionResponse>
```

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`options`** | <code>{ productIdentifier: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#latesttransactionresponse">LatestTransactionResponse</a>&gt;</code>

--------------------


### manageSubscriptions(...)

```typescript
manageSubscriptions(options: { productIdentifier: string; }) => void
```

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`options`** | <code>{ productIdentifier: string; }</code> |

--------------------


### setGoogleVerificationDetails(...)

```typescript
setGoogleVerificationDetails(options: { googleVerifyEndpoint: string; bid: string; }) => void
```

| Param         | Type                                                        |
| ------------- | ----------------------------------------------------------- |
| **`options`** | <code>{ googleVerifyEndpoint: string; bid: string; }</code> |

--------------------


### addListener('ANDROID-PURCHASE-RESPONSE', ...)

```typescript
addListener(eventName: 'ANDROID-PURCHASE-RESPONSE', listenerFunc: (response: AndroidPurchasedTrigger) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                               |
| ------------------ | -------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'ANDROID-PURCHASE-RESPONSE'</code>                                                           |
| **`listenerFunc`** | <code>(response: <a href="#androidpurchasedtrigger">AndroidPurchasedTrigger</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### Interfaces


#### ProductDetailsResponse

| Prop                  | Type                                                                                    |
| --------------------- | --------------------------------------------------------------------------------------- |
| **`responseCode`**    | <code><a href="#productdetailsresponsecode">ProductDetailsResponseCode</a></code>       |
| **`responseMessage`** | <code><a href="#productdetailsresponsemessage">ProductDetailsResponseMessage</a></code> |
| **`data`**            | <code><a href="#product">Product</a></code>                                             |


#### Product

| Prop                    | Type                |
| ----------------------- | ------------------- |
| **`productIdentifier`** | <code>string</code> |
| **`price`**             | <code>string</code> |
| **`displayName`**       | <code>string</code> |
| **`description`**       | <code>string</code> |


#### PurchaseProductResponse

| Prop                  | Type                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`responseCode`**    | <code>0 \| 1 \| 2 \| 5 \| 4 \| 3 \| -1</code>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **`responseMessage`** | <code>'Incompatible with web' \| 'Could not find a product matching the given productIdentifier' \| 'Successfully purchased product' \| '<a href="#product">Product</a> seems to have been purchased but the transaction failed verification' \| 'User closed the native popover before purchasing' \| '<a href="#product">Product</a> request made but is currently pending - likely due to parental restrictions' \| 'An unknown error occurred whilst in the purchasing process' \| 'Successfully opened native popover' \| 'Failed to open native popover'</code> |


#### CurrentEntitlementsResponse

| Prop                  | Type                                                                                              |
| --------------------- | ------------------------------------------------------------------------------------------------- |
| **`responseCode`**    | <code><a href="#currententitlementsresponsecode">CurrentEntitlementsResponseCode</a></code>       |
| **`responseMessage`** | <code><a href="#currententitlementsresponsemessage">CurrentEntitlementsResponseMessage</a></code> |
| **`data`**            | <code>Transaction[]</code>                                                                        |


#### Transaction

| Prop                    | Type                |
| ----------------------- | ------------------- |
| **`productIdentifier`** | <code>string</code> |
| **`expiryDate`**        | <code>string</code> |
| **`originalStartDate`** | <code>string</code> |


#### LatestTransactionResponse

| Prop                  | Type                                                                                          |
| --------------------- | --------------------------------------------------------------------------------------------- |
| **`responseCode`**    | <code><a href="#latesttransactionresponsecode">LatestTransactionResponseCode</a></code>       |
| **`responseMessage`** | <code><a href="#latesttransactionresponsemessage">LatestTransactionResponseMessage</a></code> |
| **`data`**            | <code><a href="#transaction">Transaction</a></code>                                           |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### AndroidPurchasedTrigger

| Prop        | Type                 |
| ----------- | -------------------- |
| **`fired`** | <code>boolean</code> |


### Type Aliases


#### ProductDetailsResponseCode

<code>-1 | 0 | 1</code>


#### ProductDetailsResponseMessage

<code>"Incompatible with web" | "Successfully found the product details for given productIdentifier" | "Could not find a product matching the given productIdentifier"</code>


#### PurchaseProductIOSResponseCode

<code>-1 | 0 | 1 | 2 | 3 | 4 | 5</code>


#### PurchaseProductAndroidResponseCode

<code>-1 | 0 | 1</code>


#### PurchaseProductIOSResponseMessage

<code>"Incompatible with web" | "Successfully purchased product" | "Could not find a product matching the given productIdentifier" | "<a href="#product">Product</a> seems to have been purchased but the transaction failed verification" | "User closed the native popover before purchasing" | "<a href="#product">Product</a> request made but is currently pending - likely due to parental restrictions" | "An unknown error occurred whilst in the purchasing process"</code>


#### PurchaseProductAndroidResponseMessage

<code>"Incompatible with web" | "Successfully opened native popover" | "Failed to open native popover"</code>


#### CurrentEntitlementsResponseCode

<code>-1 | 0 | 1 | 2</code>


#### CurrentEntitlementsResponseMessage

<code>"Incompatible with web" | "Successfully found all entitlements across all product types" | "No entitlements were found" | "Unknown problem trying to retrieve entitlements" | </code>


#### LatestTransactionResponseCode

<code>-1 | 0 | 1 | 2 | 3</code>


#### LatestTransactionResponseMessage

<code>"Incompatible with web" | "Successfully found the latest transaction matching given productIdentifier" | "Could not find a product matching the given productIdentifier" | "No transaction for given productIdentifier, or it could not be verified" | "Unknown problem trying to retrieve latest transaction"</code>

</docgen-api>
