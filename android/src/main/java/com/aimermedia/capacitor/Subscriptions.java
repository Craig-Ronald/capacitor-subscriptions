package com.capicitor_subscriptions.capacitor;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;

import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.getcapacitor.App;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Subscriptions {

    private Activity activity = null;
    public Context context = null;
    private SubscriptionsPlugin plugin;
    private BillingClient billingClient;
    private int billingClientIsConnected = 0;

    private String googleVerifyEndpoint = "";
    private String googleBid = "";

    public Subscriptions(SubscriptionsPlugin plugin, BillingClient billingClient) {

        this.plugin = plugin;
        this.billingClient = billingClient;
        this.billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    billingClientIsConnected = 1;
                } else {
                    billingClientIsConnected = billingResult.getResponseCode();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
        this.activity = plugin.getActivity();
        this.context = plugin.getContext();

    }

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public void setGoogleVerificationDetails(String googleVerifyEndpoint, String bid) {
        this.googleVerifyEndpoint = googleVerifyEndpoint;
        this.googleBid = bid;

        Log.i("SET-VERIFY", "Verification values updated");
    }

    public void getProductDetails(String productIdentifier, PluginCall call) {

        JSObject response = new JSObject();

        if(billingClientIsConnected == 1) {

            QueryProductDetailsParams.Product productToFind = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productIdentifier)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build();

            QueryProductDetailsParams queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                            .setProductList(List.of(productToFind))
                            .build();

            billingClient.queryProductDetailsAsync(
                    queryProductDetailsParams,
                    (billingResult, productDetailsList) -> {

                        try {

                            ProductDetails productDetails = productDetailsList.get(0);
                            String productId = productDetails.getProductId();
                            String title = productDetails.getTitle();
                            String desc = productDetails.getDescription();
                            Log.i("productIdentifier", productId);
                            Log.i("displayName", title);
                            Log.i("desc", desc);

                            List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();

                            String price = subscriptionOfferDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
//                            String currency = subscriptionOfferDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();

                            JSObject data = new JSObject();
                            data.put("productIdentifier", productId);
                            data.put("displayName", title);
                            data.put("description", desc);
                            data.put("price", price);
//                            data.put("currency", currency);

                            response.put("responseCode", 0);
                            response.put("responseMessage", "Successfully found the product details for given productIdentifier");
                            response.put("data", data);

                        } catch(Exception e) {
                            Log.e("Err", e.toString());
                            response.put("responseCode", 1);
                            response.put("responseMessage", "Could not find a product matching the given productIdentifier");
                        }

                        call.resolve(response);
                    }
            );
            
        } else if(billingClientIsConnected == 2) {

            response.put("responseCode", 500);
            response.put("responseMessage", "Android: BillingClient failed to initialise");
            call.resolve(response);

        } else {

            response.put("responseCode", billingClientIsConnected);
            response.put("responseMessage", "Android: BillingClient failed to initialise");

            response.put("responseCode", 503);
            response.put("responseMessage", "Android: BillingClient is still initialising");
            call.resolve(response);

        }
    }

    public void getLatestTransaction(String productIdentifier, PluginCall call) {

        JSObject response = new JSObject();

        if(billingClientIsConnected == 1) {

            QueryPurchaseHistoryParams queryPurchaseHistoryParams =
                    QueryPurchaseHistoryParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build();


            billingClient.queryPurchaseHistoryAsync(queryPurchaseHistoryParams, (BillingResult billingResult, List<PurchaseHistoryRecord> list) -> {

                // Try to loop through the list until we find a purchase history record associated with the passed in productIdentifier.
                // If we do, then set found to true to break out of the loop, then compile a response with necessary data. Otherwise compile
                // a response saying that the there were not transactions for the given productIdentifier.
                int i = 0;
                boolean found = false;
                while (list != null && (i < list.size() && !found)) {
                    try {

                        JSObject currentPurchaseHistoryRecord = new JSObject(list.get(i).getOriginalJson());
                        Log.i("PurchaseHistory", currentPurchaseHistoryRecord.toString());

                        if (currentPurchaseHistoryRecord.get("productId").equals(productIdentifier)) {

                            found = true;

                            JSObject data = new JSObject();
                            String expiryDate = getExpiryDateFromGoogle(productIdentifier, currentPurchaseHistoryRecord.get("purchaseToken").toString());
                            if(expiryDate != null) {
                                data.put("expiryDate", expiryDate);
                            }

                            String dateFormat = "dd-MM-yyyy hh:mm";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong((currentPurchaseHistoryRecord.get("purchaseTime").toString())));

                            data.put("productIdentifier", currentPurchaseHistoryRecord.get("productId"));
                            data.put("originalId", currentPurchaseHistoryRecord.get("orderId"));
                            data.put("transactionId", currentPurchaseHistoryRecord.get("orderId"));

                            response.put("responseCode", 0);
                            response.put("responseMessage", "Successfully found the latest transaction matching given productIdentifier");
                            response.put("data", data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // This should never really be caught, but compile a response saying an unknown error occurred anyway.
                    }

                    i++;

                }

                // If after looping through the list of purchase history records, no records are found to be associated with
                // the given product identifier, return a response saying no transactions found
                if (!found) {
                    response.put("responseCode", 3);
                    response.put("responseMessage", "No transaction for given productIdentifier, or it could not be verified");
                }

                call.resolve(response);

            });

        }

    }

    public void getCurrentEntitlements(PluginCall call) {

        JSObject response = new JSObject();

        if(billingClientIsConnected == 1) {

            QueryPurchasesParams queryPurchasesParams =
                    QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build();

            billingClient.queryPurchasesAsync(
                    queryPurchasesParams,
                    (billingResult, purchaseList) -> {

                        try {

                            Integer amountOfPurchases = purchaseList.size();

                            if(amountOfPurchases > 0 ) {

                                ArrayList<JSObject> entitlements = new ArrayList<JSObject>();
                                for(int i = 0; i < purchaseList.size(); i++) {

                                    Purchase currentPurchase = purchaseList.get(i);

                                    // if(currentPurchase.isAutoRenewing()) {

                                        String expiryDate = this.getExpiryDateFromGoogle(currentPurchase.getProducts().get(0), currentPurchase.getPurchaseToken());
                                        String orderId = currentPurchase.getOrderId();

                                        String dateFormat = "dd-MM-yyyy hh:mm";
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTimeInMillis(Long.parseLong((String.valueOf(currentPurchase.getPurchaseTime()))));

                                        entitlements.add(
                                                new JSObject()
                                                        .put("productIdentifier", currentPurchase.getProducts().get(0))
                                                        .put("expiryDate", expiryDate)
                                                        .put("originalStartDate", simpleDateFormat.format(calendar.getTime()))
                                                        .put("originalId", orderId)
                                                        .put("transactionId", orderId)
                                        );
                                    // }
                                }

                                response.put("responseCode", 0);
                                response.put("responseMessage", "Successfully found all entitlements across all product types");
                                response.put("data", entitlements);


                            } else {
                                Log.i("No Purchases", "No active subscriptions found");
                                response.put("responseCode", 1);
                                response.put("responseMessage", "No entitlements were found");
                            }


                            call.resolve(response);

                        } catch(Exception e) {
                            Log.e("Error", e.toString());
                            response.put("responseCode", 2);
                            response.put("responseMessage", e.toString());
                        }

                        call.resolve(response);

                    }
            );

        }

    }

    public void purchaseProduct(String productIdentifier, PluginCall call) {

        JSObject response = new JSObject();

        if(billingClientIsConnected == 1) {

            QueryProductDetailsParams.Product productToFind = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productIdentifier)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build();

            QueryProductDetailsParams queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                            .setProductList(List.of(productToFind))
                            .build();

            billingClient.queryProductDetailsAsync(
                    queryProductDetailsParams,
                    (billingResult1, productDetailsList) -> {

                        try {
                            ProductDetails productDetails = productDetailsList.get(0);
                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(
                                            List.of(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            .setProductDetails(productDetails)
                                                            .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                                            .build()
                                            )
                                    )
                                    .build();
                            BillingResult result = billingClient.launchBillingFlow(this.activity, billingFlowParams);
                            Log.i("RESULT", result.toString());
                            response.put("responseCode", 0);
                            response.put("responseMessage", "Successfully opened native popover");

                        } catch (Exception e) {
                            e.printStackTrace();
                            response.put("responseCode", 1);
                            response.put("responseMessage", "Failed to open native popover");
                        }

                        call.resolve(response);

                    });
        }

    }

    private String getExpiryDateFromGoogle(String productIdentifier, String purchaseToken) {

        try {

            // Compile request to verify purchase token
            URL obj = new URL(this.googleVerifyEndpoint + "?bid=" + this.googleBid + "&subId=" + productIdentifier + "&purchaseToken=" + purchaseToken);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // Try to receive response from server
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {

                StringBuilder googleResponse = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    googleResponse.append(responseLine.trim());
                    Log.i("Response Line", responseLine);
                }

                // If the response was successful, extract expiryDate and put it in our response data property
                if (con.getResponseCode() == 200) {

                    JSObject postResponseJSON = new JSObject(googleResponse.toString());
                    JSObject googleResponseJSON = new JSObject(postResponseJSON.get("googleResponce").toString()); // <-- note the typo in response object from server
                    JSObject payloadJSON = new JSObject(googleResponseJSON.get("payload").toString());

                    String dateFormat = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z '('z')'";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong((payloadJSON.get("expiryTimeMillis").toString())));

                    Log.i("EXPIRY", simpleDateFormat.format(calendar.getTime()));

                    return simpleDateFormat.format(calendar.getTime());

                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // If the method manages to each this far before already returning, just return null
        // because something went wrong
        return null;
    }

    public void startTrial(String bid, Integer lengthInDays, PluginCall call) {

        try {
            String advertisingID = AdvertisingIdClient.getAdvertisingIdInfo(this.context.getApplicationContext()).toString();

            // Update server using endpoint call with bid, ad_id and the date {lengthInDays} from now
            Integer result = this.writeTrialDataToServer(bid, advertisingID);

            JSObject response = new JSObject();
            if(result == 0) {
                response.put("responseCode", 0);
                response.put("responseMessage", "Started trial successfully");
            } else {
                response.put("responseCode", 1);
                response.put("responseMessage", "Trial failed to start");
            }

            call.resolve(response);
            Log.i("advertisingID", advertisingID);

        } catch (Exception e) {
            Log.i("ADID-ERROR", e.toString());
        }

    }
}
