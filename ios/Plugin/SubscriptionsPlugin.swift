import Foundation
import Capacitor
import StoreKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(SubscriptionsPlugin)
public class SubscriptionsPlugin: CAPPlugin {

    // Allows us to execute the actual code from Subscriptions.swift file
    private let implementation = Subscriptions();

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        
        call.resolve([
            "value": implementation.echo(value)
        ])
    }

    
    @available(iOS 15.0.0, *)
    @objc func getProductDetails(_ call: CAPPluginCall) {
        
        guard let productIdentifier = call.getString("productIdentifier") as? String else {
           call.reject("Must provide a productID")
           return;
        }

        async {

            let response = await implementation.getProductDetails(productIdentifier);
            call.resolve(response);

        }
    }

    @available(iOS 15.0.0, *)
    @objc func purchaseProduct(_ call: CAPPluginCall) {
        
        guard let productIdentifier = call.getString("productIdentifier") as? String else {
           call.reject("Must provide a productID")
           return;
        }

        async {

            let response = await implementation.purchaseProduct(productIdentifier);
            call.resolve(response);

        }

    }

    @available(iOS 15.0.0, *)
    @objc func getCurrentEntitlements(_ call: CAPPluginCall) {
        
        async {

            let response = await implementation.getCurrentEntitlements();
            call.resolve(response);

        }

    }

    @available(iOS 15.0.0, *)
    @objc func getLatestTransaction(_ call: CAPPluginCall) {

        guard let productIdentifier = call.getString("productIdentifier") as? String else {
           call.reject("Must provide a productID")
           return;
        }
        
        async {

            let response = await implementation.getLatestTransaction(productIdentifier);
            call.resolve(response);

        }

    }

    @available(iOS 15.0.0, *)
    @objc func manageSubscriptions(_ call: CAPPluginCall) {
        
        async {

            do {
                await implementation.manageSubscriptions();
                call.resolve(["Success": "Opened"])
            } catch {
                print(error.localizedDescription);
                call.reject(error.localizedDescription)
            }

        }

    }
    
}
