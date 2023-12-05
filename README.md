
# Capacitor Subscription

A capacitor plugin which simplifies subscription handling - implementing StoreKit 2 and Google Billing 5.

## Install

```bash
npm install @capacitor-community/capacitor-subscriptions
ionic cap sync
```

## Summary
  

This plugin is designed to simplify and reduce the workload of a developer when implementing auto-renewing subscriptions for iOS and Android apps.

The plugin primarily uses a promise-based architecture to allow a developer to have greater control over the purchase and validation processes involved when interacting with StoreKit 2 and Google Billing 5.

  

Examples - Subscriptions

  
-   [Initial Android setup (server validation)](#markdown-header-initial-android-setup-server-validation)
-   [Determining if user has an active subscription or not](#markdown-header-determining-if-user-has-an-active-subscription-or-not)
-   [Retrieve the most recent transaction in order to provide user feedback on when their subscription expires/expired](#markdown-header-retrieve-the-most-recent-transaction-regardless-of-whether-or-not-it-is-active-useful-for-providing-feedback-on-when-the-subscription-willhas-expired)
-   [Retrieving product details e.g. price](#markdown-header-retrieving-product-details-eg-price)
-   [Payment initiation and flow (iOS)](#markdown-header-payment-initiation-and-flow-ios)
-   [Payment initiation and flow (Android)](#markdown-header-payment-initiation-and-flow-android)

## API Docs

For a more in-depth look into the different parameters for methods, along with the corresponding types. Please look into a breakdown of the API:

[API Documentation](api-docs.md)

## More in-depth review of the plugin

As it stands, the [current plugin listed on the capacitor website](https://ionicframework.com/docs/native/in-app-purchase-2) can be used to achieve a working solution for subscription processing, however after using that plugin myself, I found many of the listener methods to be redundant, and the server-side transaction verifying tedious, and not very well documented.
  

By changing how the store data is received from listener-based methods to promise-based methods, the overall process of receiving data is a lot more stream-lined - returning only necessary data as opposed to every transaction a user has ever made.

  

This plugin implements capabilities to allow the developer to:

  

-   No longer have to use server-side technology to verify Apple’s horribly-formatted receipt - transactions are now automatically verified on Apple’s end.
-   Retrieve all currently active subscriptions allowing you to determine whether or not the user has access to content with just a single line of code.
-   Not have to worry about handling transactions which are made outside of the purchase-flow (e.g. an auto renewed subscription) as this is taken care of on the native side of the plugin.
-   Create more responsive IAP processes by awaiting promise calls, making the processes synchronous and predictable.

  

## Limitations

-   Google unfortunately still requires a server-side call to verify a transaction’s purchase token in order to find out the expiry date of a subscription (there is currently no way around this).

-  To help make this as painless as can be, a method is available which will perform the request upon passing in your server’s verification endpoint and app bundle details. A guide on how to set up your server to connect to your app can be found [here](#markdown-header-initial-android-setup-server-validation)

-   As this library uses StoreKit 2, any users on anything lower than iOS 15 will have to upgrade in order to access the app.


# Examples

## Initial Android setup (server validation)

Before calling any methods on the plugin, it is essential to pass a few parameters into the "setGoogleVerificationDetails(...)" method. In an Ionic app, this would be most appropriate near the top of the App.tsx file - simply pass in the server endpoint for the google verification call, along with the app bid, e.g:

```javascript 
useEffect(() => {

    SubscriptionController.setGoogleVerificationDetails(
      "https://YOUR-END-POINT.com/verifyGoogleReceipt",
      "com.*TEAM-NAME*.APP-NAME"
    )

	// start making calls to other plugin methods

}, []);
```

**NOTE** - It is NOT required to specifically check that the device is an Android one before executing this code. If this code is executed on an iOS device, the plugin will just ignore it.

## Determining if user has an active subscription or not

Calling getCurrentEntitlements() will return an array of subscription transactions which are still active - if the array length is greater than one, then the user has an active subscription.

```javascript
const [hasActiveSubscription, setHasActiveSubscription] = useState(false)

SubscriptionController.getCurrentEntitlements().then((entitlements: any) => {
	setHasActiveSubscription(entitlements.length > 0);
});

async getCurrentEntitlements() {

	const response: CurrentEntitlementsResponse = await  Subscriptions.getCurrentEntitlements();
	if(response.responseCode == 0){
		return response.data  as  Transaction[];
	} else {
		return [];
	}
}
```

## Retrieve the most recent transaction regardless of whether or not it is active (useful for providing feedback on when the subscription will/has expired)

Using getLatestTransaction(...) and passing the relevant product identifier (linked to your iOS/Android subscription products), will return the most recent transaction the user has made for that product.

```javascript
productIDs = {
	"ios": {
		"oneMonth": "com.your.subscriptionid.monthly",
		"twelveMonth": "com.your.subscriptionid.yearly",
	},
	"android": {
		"oneMonth":  "com.your.subscriptionid.android.1.month",
		"twelveMonth":  "com.your.subscriptionid.android.12.months"
	}
}

async getLatestTransaction(): Promise<Transaction | undefined> {
	  
	try {

		const  platform = (await  Device.getInfo()).platform;

		const  oneMonthResponse: LatestTransactionResponse = await Subscriptions.getLatestTransaction({
			productIdentifier:  productIDs[platform]["oneMonth"]
		});

		const  twelveMonthResponse: LatestTransactionResponse = await  Subscriptions.getLatestTransaction({
		productIdentifier:  productIDs[platform]["twelveMonth"]
		});

		const  oneMonthSuccessful = oneMonthResponse.responseCode == 0;
		const  twelveMonthSuccessful = twelveMonthResponse.responseCode == 0;

		// If user has had both a one month and twelve month subscription in the past
		// we need to check the expiry date of both and return the most recent one.
		if(oneMonthSuccessful && twelveMonthSuccessful) {	  

			const  oneMonthTransactionExpiry = new  Date((oneMonthResponse.data  as  Transaction)?.expiryDate);
			const  twelveMonthTransactionExpiry = new  Date((twelveMonthResponse.data  as  Transaction)?.expiryDate);


			if(oneMonthTransactionExpiry > twelveMonthTransactionExpiry) { return  oneMonthResponse.data  as  Transaction }
			else { return  twelveMonthResponse.data  as  Transaction }

		} else  if (oneMonthSuccessful && !twelveMonthSuccessful) {
			return  oneMonthResponse.data  as  Transaction
		} else  if (!oneMonthSuccessful && twelveMonthSuccessful) {
			return  twelveMonthResponse.data  as  Transaction
		} else {
			return  undefined;
		}
		
	} catch(error: any) {

		console.log("Error when attempting to retrieve transaction info", error);
		return undefined;

	}
},
```

## Retrieving product details e.g. price

Passing in the subscription's product identifier to getProductDetails(...) will return a product object containing relevant information about the product.

```javascript
productIDs = {
	"ios": {
		"oneMonth": "com.your.subscriptionid.monthly",
		"twelveMonth": "com.your.subscriptionid.yearly",
	},
	"android": {
		"oneMonth":  "com.your.subscriptionid.android.1.month",
		"twelveMonth":  "com.your.subscriptionid.android.12.months"
	}
}

const [oneMonthPrice, setOneMonthPrice] = useState("Loading...");
const [twelveMonthPrice, settwelveMonthPrice] = useState("Loading...");

async retrieveProductDetails() {

	const platform = (await Device.getInfo()).platform;  
	  
	let oneMonthProduct: ProductDetailsResponse = await  Subscriptions.getProductDetails({
		productIdentifier:  productIDs[platform]["oneMonth"];
	});

	let  twelveMonthProduct: ProductDetailsResponse = await  Subscriptions.getProductDetails({
		productIdentifier:  productIDs[platform]["twelveMonth"];
	});

	setOneMonthPrice(
		oneMonthProduct.responseCode === 0 ? (oneMonthProduct.data  as  Product).price : 'Failed'
	);

	setTwelveMonthPrice(
		oneTwelveProduct.responseCode === 0 ? (oneTwelveProduct.data  as Product).price : 'Failed'
	);

}
```
## Payment initiation and flow (iOS)
Initiating the payment flow (bringing up the native payment popover) is simple on iOS, it just requires awaiting a call to the purchaseProduct(...) method - passing in the necessary product identifier.

```javascript
productIDs = {
	"ios": {
		"oneMonth": "com.your.subscriptionid.monthly",
		"twelveMonth": "com.your.subscriptionid.yearly",
	},
	"android": {
		"oneMonth":  "com.your.subscriptionid.android.1.month",
		"twelveMonth":  "com.your.subscriptionid.android.12.months"
	}
}

async  purchaseProduct(productType: "oneMonth" | "twelveMonth") {

	const  platform = (await  Device.getInfo()).platform;
	const  response = await  Subscriptions.purchaseProduct({
		productIdentifier:  productIDs[platform][productType];
	})

}
```

In your HTML code, inside a function which is triggered upon clicking a purchase button. You can simply just await a call to the method above, blocking the function until the process has finished (i.e. user finishing paying, or cancelled/closed the popover). The function will then resume, so a call to a validation function can be made to update the app depending on the result of the purchaseProduct(...) call.

```javascript
<div
	className="subscription-btn"
	onClick={async () => {

		setIsInPurchaseProcess(true);
		await  purchaseProduct("twelveMonth"); // <-- waits until native popover is closed 
		await  validateUserAccess(); // <-- Useful to have a easy accessible method which validates user access (either by checking current entitlements or most recent transaction).

		if(isPlatform('ios')) {
			setIsInPurchaseProcess(false);
		}
	}}
>
	<div  className="subscription-btn-txt">
		12 month / {twelveMonthPrice}
	</div>
</div>
```

## Payment initiation and flow (Android)
The payment process on Android is a bit more complex than Apple due to two reasons:

1. The purchaseProduct(...) method does NOT block code, therefore it requires different functionality to receive the result of the native popover process.

2. Although Apple automatically verifies purchases upon the release of StoreKit, Google Billing does NOT. This means server-side technology is still required in order to validate purchases (without making this server-side call to Google, it would be impossible to know the expiry date of a subscription transaction - essentially making it a necessity to perform this step).

The solution for this is to implement a listener which fires whenever the payment process is complete. It is recommended to initilise this listener early on in the app (near the top of your App.tsx file) to ensure that no purchases are missed. This listener should just be used to determine when you need to check for any new transactions to update your app with.

```javascript
useEffect(() => {
	Subscriptions.addListener("ANDROID-PURCHASE-RESPONSE", (response: AndroidPurchasedTrigger) => {
		validateUserAccess();
	});
}, [])
```