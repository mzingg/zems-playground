// noinspection ES6PreferShortImport

import { isServerSide } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_RESOURCE$*/
import { withContentBusClient } from './websocketClient.mjs';
import { withGraalVMClient } from './graalvmClient.mjs';
import { withMockClient } from './mockClient.mjs';

const useMockClient = false;

let isAsyncImplemented = false;
let ContentBusImpl;
if (useMockClient) {
  ContentBusImpl = withMockClient();
} else if (isServerSide) {
  ContentBusImpl = withGraalVMClient();
} else {
  isAsyncImplemented = true;
  ContentBusImpl = withContentBusClient; // this returns a Promise (-> can only be used in async functions)
}

export const contentBusLoader = isAsyncImplemented ? async ({ path }) => {
  return (await ContentBusImpl()).loader({ path });
} : ({ path }) => {
  return ContentBusImpl.loader({ path });
};

export const registerUpdateHandler = isAsyncImplemented ? async ({ contentPath, componentId, handlerFunction }) => {
  (await ContentBusImpl()).registerUpdateHandler({ contentPath, componentId, handlerFunction });
} : ({ contentPath, componentId, handlerFunction }) => {
  ContentBusImpl.registerUpdateHandler({ contentPath, componentId, handlerFunction });
};

export const unRegisterUpdateHandler = isAsyncImplemented ? async ({ componentId }) => {
  (await ContentBusImpl()).unRegisterUpdateHandler({ componentId });
} : ({ componentId }) => {
  ContentBusImpl.unRegisterUpdateHandler({ componentId });
};

export const sendUpdate = isAsyncImplemented ? async ({ changedPath, payload }) => {
  return (await ContentBusImpl()).sendUpdate({ changedPath, payload });
} : ({ changedPath, payload }) => {
  return ContentBusImpl.sendUpdate({ changedPath, payload });
};

export const sendToTopic = isAsyncImplemented ? async ({ topic, payload }) => {
  return (await ContentBusImpl()).sendToTopic({ topic, payload });
} : ({ topic, payload }) => {
  return ContentBusImpl.sendToTopic({ topic, payload });
};