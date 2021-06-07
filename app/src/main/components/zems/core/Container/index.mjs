import {jsxLight, React} from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import {useComponents, useKeyGenerator} from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/
import {RenderMode} from '../../../../modules/zems/core/Defs/index.mjs'; /*$ZEMS_RESOURCE$*/
import {Configuration} from '../../../../modules/zems/playground/Config/index.mjs'; /*$ZEMS_RESOURCE$*/

export default function Container(props) {
    const {components} = props;
    const {renderMode} = Configuration;
    const createKey = useKeyGenerator();

    const ComponentList = useComponents(components)

    if (ComponentList && ComponentList.length > 0) {
        if (renderMode === RenderMode.AUTHOR) {
            return jsxLight`<div key="${createKey()}">Container:</div>${ComponentList}`
        } else {
            return ComponentList;
        }
    } else {
        return jsxLight`
      <div>Loading ...</div>`;
    }
}