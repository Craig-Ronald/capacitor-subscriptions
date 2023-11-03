import { registerPlugin } from '@capacitor/core';
const Subscriptions = registerPlugin('Subscriptions', {
    web: () => import('./web').then(m => new m.SubscriptionsWeb()),
});
export * from './definitions';
export { Subscriptions };
//# sourceMappingURL=index.js.map