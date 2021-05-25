// noinspection ES6PreferShortImport

import { uuidv4 } from '../../../../modules/zems/core/Lib/index.mjs'; /*$ZEMS_RESOURCE$*/

export const withContentBusClient = () => {
  return new Promise((resolve, reject) => {
    if (!window.SocketServerUrl) {
      throw Error('Please define variable window.SocketServerUrl that points to the websocket.');
    }

    let ContentBusClient = window.ContentBusClient;
    if (!ContentBusClient) {
      // noinspection JSUnresolvedFunction,JSUnusedGlobalSymbols,JSUnusedLocalSymbols
      ContentBusClient = new window.StompJs.Client({
        webSocketFactory: () => new window.SockJS(window.SocketServerUrl),
        debug: (message) => {
          // uncomment to see debug messages about the STOMP protocol in the browser console
          // console.debug(message);
        },
        connectionTimeout: 100,
        reconnectDelay: 1000,
        heartbeatIncoming: 0,
        heartbeatOutgoing: 0,
      });

      ContentBusClient.GetSubscriptions = {};

      ContentBusClient.onConnect = () => {
        window.ContentBusClient = ContentBusClient;
        resolve(ContentBusClient);
      }

      ContentBusClient.cleanupSubscription = (clientId) => {
        if (ContentBusClient.GetSubscriptions[clientId]) {
          ContentBusClient.GetSubscriptions[clientId].unsubscribe();
          delete ContentBusClient.GetSubscriptions.clientId;
        }
      }

      ContentBusClient.loader = ({ path }) => {
        return new Promise((resolve, reject) => {
          const ReadTimeout = 5000;

          // create a unique id to identify this request in the content bus
          const clientId = uuidv4();

          // set a rejection timeout and store its id to be able to clear it later when the call is a success
          const timerId = setTimeout(() => {
            ContentBusClient.cleanupSubscription(clientId);

            reject(clientId);
          }, ReadTimeout)

          // subscribe in the content bus and store the subscription object in a table so that we can clean it up correctly
          // (remember, this is asynchronous)
          ContentBusClient.GetSubscriptions[clientId] = ContentBusClient.subscribe('/topic/contentbus', (message) => {
            const data = JSON.parse(message.body);
            const { clientId: cid } = data;
            if (cid === clientId) {
              // if the message/answer is meant for us we clean up after ourselves and pass the data to the Promise resolve
              ContentBusClient.cleanupSubscription(clientId);
              clearTimeout(timerId);

              delete data.clientId;
              resolve(data.properties);
            }
          });

          ContentBusClient.publish({
            destination: '/zems/contentbus/get',
            body: JSON.stringify({ path, clientId })
          });
        });
      }

      ContentBusClient.onStompError = (frame) => {
        delete window.ContentBusClient;
        reject(frame);
      }

      ContentBusClient.activate();
    } else {
      resolve(ContentBusClient);
    }
  });
}
