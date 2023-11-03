#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(SubscriptionsPlugin, "Subscriptions",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getProductDetails, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(purchaseProduct, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getCurrentEntitlements, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getLatestTransaction, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(manageSubscriptions, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getTrialStatus, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startTrial, CAPPluginReturnPromise);
)
