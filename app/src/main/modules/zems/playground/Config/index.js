// noinspection ES6PreferShortImport

import { RenderMode } from '../../../../modules/zems/core/Defs'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnresolvedVariable
const { createContext, useContext } = window.React;

const InitialConfigState = {
  renderMode: RenderMode.fromValue(window.sessionStorage.getItem('ZemsRenderMode') || 'author'),
};
const ConfigContext = createContext(InitialConfigState)

export const useConfig = () => {
  return useContext(ConfigContext);
}

