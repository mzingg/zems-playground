// noinspection JSUnusedGlobalSymbols,ES6PreferShortImport

import {React} from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import {useContentBus} from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/

const {useState} = React;

const useNotification = () => {
    const componentId = '';
    const [notifications, publishNotification] = useContentBus(
        '/playground/notifications',
        componentId,
        [],
        {
            onChange: () => {
            }
        });
}
