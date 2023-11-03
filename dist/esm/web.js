import { WebPlugin } from '@capacitor/core';
export class SubscriptionsWeb extends WebPlugin {
    setGoogleVerificationDetails(options) {
        options;
    }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
    async getProductDetails(options) {
        options;
        return {
            responseCode: -1,
            responseMessage: 'Incompatible with web',
        };
    }
    async purchaseProduct(options) {
        options;
        return {
            responseCode: -1,
            responseMessage: 'Incompatible with web',
        };
    }
    async getCurrentEntitlements() {
        return {
            responseCode: -1,
            responseMessage: 'Incompatible with web',
        };
    }
    async getLatestTransaction(options) {
        options;
        return {
            responseCode: -1,
            responseMessage: 'Incompatible with web',
        };
    }
    manageSubscriptions() {
    }
}
//# sourceMappingURL=web.js.map