// noinspection ES6PreferShortImport

import 'https://cdn.jsdelivr.net/npm/sockjs-client@1.5.1/dist/sockjs.min.js';
import 'https://cdn.jsdelivr.net/npm/@stomp/stompjs@6.1.0/bundles/stomp.umd.min.js';
import { uuidv4 } from "../Lib";

const withContentBusClient = () => {
  return new Promise((resolve, reject) => {
    if (!window.SocketServerUrl) {
      throw Error('Please define global variable window.SocketServerUrl that points to the websocket.');
    }

    let ContentBusClient = window.ContentBusClient;
    if (!ContentBusClient) {
      // noinspection JSUnusedGlobalSymbols
      ContentBusClient = new StompJs.Client({
        webSocketFactory: () => new SockJS(window.SocketServerUrl),
        debug: (message) => {
          // uncomment to see debug messages about the STOMP protocol in the browser console
          console.debug(message);
        },
        connectionTimeout: 100,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      ContentBusClient.GetSubscriptions = {};

      ContentBusClient.onConnect = () => {
        window.ContentBusClient = ContentBusClient;
        ContentBusClient.subscribe('/topic/contentbus', (message) => {
          console.info('Global message handler');
          console.info(message.body);
        });
        resolve(ContentBusClient);
      }

      ContentBusClient.cleanupSubscription = (clientId) => {
        if (ContentBusClient.GetSubscriptions[clientId]) {
          ContentBusClient.GetSubscriptions[clientId].unsubscribe();
          ContentBusClient.GetSubscriptions[clientId] = undefined;
        }
      }

      ContentBusClient.get = ({ path }) => {
        return new Promise((resolve, reject) => {
          const ReadTimeout = 1100;

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
        window.ContentBusClient = undefined;
        reject(frame);
      }

      ContentBusClient.activate();
    } else {
      resolve(ContentBusClient);
    }
  });
}

export const ContentBusLoader = async ({ path }) => {
  let result = await (await withContentBusClient()).get({ path });
  console.info(result);
  return () => result;
}


// const PageModel = {
//   pageTitle: 'Page Title From Contentbus',
//   contentParsys: {
//     components: [
//       {
//         resourceType: 'zems/playground/TextImage',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 1',
//           imageSrc: 'data:image/gif;base64,R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7'
//         })
//       },
//       {
//         resourceType: 'zems/playground/TextImage',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 2',
//           imageSrc: ''
//         })
//       },
//       {
//         resourceType: 'zems/playground/TextImage',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 3',
//           imageSrc: ''
//         })
//       },
//       {
//         resourceType: 'zems/playground/TextImage',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 4',
//           imageSrc: ''
//         })
//       },
//       {
//         resourceType: 'zems/playground/TextImage',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 5',
//           imageSrc: ''
//         })
//       },
//       {
//         resourceType: 'zems/playground/Text',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 6',
//         })
//       },
//       {
//         resourceType: 'zems/core/Container',
//         modelLoader: () => ({
//           components: [
//             {
//               resourceType: 'zems/playground/Text',
//               modelLoader: () => ({
//                 text: 'Container Component 1',
//               })
//             },
//             {
//               resourceType: 'zems/playground/Text',
//               modelLoader: () => ({
//                 text: 'Container Component 2',
//               })
//             },
//           ]
//         })
//       },
//       {
//         resourceType: 'zems/playground/Text',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 8',
//         })
//       },
//       {
//         resourceType: 'zems/playground/Text',
//         modelLoader: () => ({
//           text: 'A lorem ipsum text 9',
//         })
//       },
//       {
//         resourceType: 'zems/playground/Image',
//         modelLoader: () => ({
//           imageSrc: 'data:image/gif;base64,R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7'
//         })
//       },
//     ]
//   }
// }

