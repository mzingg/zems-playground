// noinspection ES6PreferShortImport

import { ZemsConfiguration } from '../../../../modules/zems/core/Defs'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnresolvedVariable
const { createContext, useContext } = window.React;

// this is an example on how to extend the base configuration and make it available to your components
const Configuration = { ...ZemsConfiguration, pageTitlePrefix: 'Playground' };
const ConfigContext = createContext(Configuration)

export const useConfig = () => {
  return useContext(ConfigContext);
}

export const SocketServerUrl = {
  localdev: 'http://localhost:56751/zems-contentbus',
  dev: 'http://localhost:55878/zems-contentbus',
  int: 'http://localhost:55878/zems-contentbus',
};
