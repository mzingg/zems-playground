// noinspection ES6PreferShortImport

import { React } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { ZemsConfiguration } from '../../../../modules/zems/core/Defs/index.mjs'; /*$ZEMS_RESOURCE$*/

const { createContext, useContext } = React;

// this is an example on how to extend the base configuration and make it available to your components
const Configuration = { ...ZemsConfiguration, pageTitlePrefix: 'Playground' };
const ConfigContext = createContext(Configuration)

export const useConfig = () => {
  return useContext(ConfigContext);
}

export const SocketServerUrl = {
  localdev: 'http://localhost:61734/zems-contentbus',
  dev: 'http://localhost:55878/zems-contentbus',
  int: 'http://localhost:55878/zems-contentbus',
};
