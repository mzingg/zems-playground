// noinspection ES6PreferShortImport

import { uuidv4 } from '../../../../modules/zems/core/Lib/index.mjs'; /*$ZEMS_RESOURCE$*/

const sendUpdate = (ContentBusClient) => ({ changedPath, payload }) => {
  ContentBusClient.publish({
    destination: '/zems/contentbus/update',
    body: JSON.stringify({ changedPath, payload })
  });
}

const registerUpdateHandler = (ContentBusClient) => ({ contentPath, componentId, handlerFunction }) => {
  // unsubscribe existing subscription
  ContentBusClient.unRegisterUpdateHandler({ componentId });

  ContentBusClient.UpdateSubscriptions[componentId] = ContentBusClient.subscribe('/topic/contentbus', (message) => {
    const data = JSON.parse(message.body);
    const { messageType, changedPath } = data;
    data.contentPath = contentPath;
    // currently we register it it to the exact path - it is possible to extend here
    // to call the handler for a wider range of paths. For example if we change the
    // properties of a container
    if (messageType === 'update' && contentPath === changedPath) {
      handlerFunction(data);
    }
  });
}

const unRegisterUpdateHandler = (ContentBusClient) => ({ componentId }) => {
  if (ContentBusClient.UpdateSubscriptions[componentId]) {
    ContentBusClient.UpdateSubscriptions[componentId].unsubscribe();
    delete ContentBusClient.UpdateSubscriptions[componentId];
  }
}

// returns a loader promise to be used in dynamic component loading (modelLoader prop)
const loader = (ContentBusClient) => ({ path }) => {
  return new Promise((resolve, reject) => {
    const ReadTimeout = 5000;

    // create a unique id to identify this request in the content bus
    const clientId = uuidv4();

    // set a rejection timeout and store its id to be able to clear it later when the call is a success
    const timerId = setTimeout(() => {
      ContentBusClient.cleanupGetSubscription(clientId);

      reject(clientId);
    }, ReadTimeout)

    // subscribe in the content bus and store the subscription object in a table so that we can clean it up correctly
    // (remember, this is asynchronous)
    ContentBusClient.GetSubscriptions[clientId] = ContentBusClient.subscribe('/topic/contentbus', (message) => {
      const data = JSON.parse(message.body);
      const { messageType, clientId: cid } = data;
      if (messageType === 'get' && cid === clientId) {
        // if the message/answer is meant for us we clean up after ourselves and pass the data to the Promise resolve
        ContentBusClient.cleanupGetSubscription(clientId);
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
      ContentBusClient.UpdateSubscriptions = {};

      ContentBusClient.onConnect = () => {
        window.ContentBusClient = ContentBusClient;
        resolve(ContentBusClient);
      }

      ContentBusClient.cleanupGetSubscription = (clientId) => {
        if (ContentBusClient.GetSubscriptions[clientId]) {
          ContentBusClient.GetSubscriptions[clientId].unsubscribe();
          delete ContentBusClient.GetSubscriptions.clientId;
        }
      }

      ContentBusClient.onStompError = (frame) => {
        delete window.ContentBusClient;
        reject(frame);
      }

      // this defines the ContentBus client API:
      ContentBusClient.loader = loader(ContentBusClient);
      ContentBusClient.registerUpdateHandler = registerUpdateHandler(ContentBusClient);
      ContentBusClient.unRegisterUpdateHandler = unRegisterUpdateHandler(ContentBusClient);
      ContentBusClient.sendUpdate = sendUpdate(ContentBusClient);

      ContentBusClient.activate();
    } else {
      resolve(ContentBusClient);
    }
  });
}
