// noinspection ES6PreferShortImport

import { ZemsConfiguration } from '../../../../modules/zems/core/Defs/index.mjs'; /*$ZEMS_RESOURCE$*/

// this is an example on how to extend the base configuration and make it available to your components
export const Configuration = {
  ...ZemsConfiguration,
  contentBusUrl: 'http://localhost:52047/zems-contentbus', //required
  pageTitlePrefix: 'Playground'
};
