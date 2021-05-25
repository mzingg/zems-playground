// noinspection ES6PreferShortImport

import { isServerSide } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_RESOURCE$*/
import { withContentBusClient } from './websocketClient.mjs';
import { withGraalVMClient } from './graalvmClient.mjs';
import { withMockClient } from './mockClient.mjs';

const useMockClient = false;

let ContentBusLoaderImpl;
if (useMockClient) {
  ContentBusLoaderImpl = ({ path }) => {
    return withMockClient().loader({ path });
  }
} else if (isServerSide) {
  ContentBusLoaderImpl = ({ path }) => {
    return withGraalVMClient().loader({ path });
  }
} else {
  ContentBusLoaderImpl = async ({ path }) => {
    return (await withContentBusClient()).loader({ path });
  }
}

export const ContentBusLoader = ContentBusLoaderImpl;