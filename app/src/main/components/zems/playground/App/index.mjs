// noinspection JSUnusedGlobalSymbols

import {jsxLight, React} from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import {useComponent} from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/
import {sendToTopic} from "../../../../modules/zems/core/ContentBus/index.mjs"; /*$ZEMS_RESOURCE$*/

const {useEffect} = React;

export default function App(props) {
    const {pagePath} = props;

    const Title = useComponent({
        resourceType: 'zems/playground/PageTitle',
        loadFrom: pagePath
    })

    const ContentParsys = useComponent({
        resourceType: 'zems/core/Container',
        loadFrom: `${pagePath}>contentParsys`
    })

    useEffect(() => {
        document.addEventListener('keydown', (event) => {
            if (event.key === 'F6') {
                sendToTopic({
                    topic: '/contentbus/flush',
                    payload: {}
                })
            }
        });
    }, []);

    return jsxLight`
<div className="container-fluid">
  <div className="row">
    <div className="col">
    </div>
    <div className="col">
    ${Title}
    ${ContentParsys}
    </div>
    <div className="col">
    </div>
  </div>
</div>`;
}