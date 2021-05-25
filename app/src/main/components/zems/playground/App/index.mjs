// noinspection JSUnusedGlobalSymbols

import { jsxLight, React } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { useComponent } from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/

export default function App(props) {
  const { pagePath } = props;

  const Title = useComponent({
    resourceType: 'zems/playground/PageTitle',
    path: pagePath
  })

  const ContentParsys = useComponent({
    resourceType: 'zems/core/Container',
    path: `${pagePath}>contentParsys`
  })

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