var capacitorSubscriptions = (function (exports, core) {
    'use strict';

    const Subscriptions = core.registerPlugin('Subscriptions', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.SubscriptionsWeb()),
    });

    class SubscriptionsWeb extends core.WebPlugin {
        setGoogleVerificationDetails(options) {
        }
        async echo(options) {
            console.log('ECHO', options);
            return options;
        }
        async getProductDetails(options) {
            return {
                responseCode: -1,
                responseMessage: 'Incompatible with web',
            };
        }
        async purchaseProduct(options) {
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
            return {
                responseCode: -1,
                responseMessage: 'Incompatible with web',
            };
        }
        manageSubscriptions() {
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        SubscriptionsWeb: SubscriptionsWeb
    });

    exports.Subscriptions = Subscriptions;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
