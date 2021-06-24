// noinspection JSUnusedGlobalSymbols

import {jsxLight, React} from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import {useComponent} from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/
import {sendToTopic} from '../../../../modules/zems/core/ContentBus/index.mjs'; /*$ZEMS_RESOURCE$*/

const {useEffect, useState} = React;

export default function App(props) {
    const [pagePath, setPagePath] = useState(props.pagePath);

    const Title = useComponent({
        resourceType: 'zems/playground/PageTitle',
        loadFrom: pagePath,
        dependencyArray: [pagePath]
    })

    const ContentParsys = useComponent({
        resourceType: 'zems/core/Container',
        loadFrom: `${pagePath}>contentParsys`,
        dependencyArray: [pagePath]
    })

    const TreeBrowser = useComponent({
        resourceType: 'zems/core/TreeBrowser',
        props: {
            onPageSelected: (selectedPath) => {
                setPagePath(selectedPath);
            }
        },
        dependencyArray: []
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

    //language=HTML
    return jsxLight`
        <div className="container-fluid">
            <div className="row">
                <div className="col">
                    ${TreeBrowser}
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