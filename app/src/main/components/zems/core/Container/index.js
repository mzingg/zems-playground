import { useComponents } from '../../../../modules/zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/
import { RenderMode } from '../../../../modules/zems/core/Defs'; /*$ZEMS_RESOURCE$*/
import { useConfig } from '../../../../modules/zems/playground/Config'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnusedGlobalSymbols
export default function Container(props) {
  const { components } = props;
  const ComponentList = useComponents(components)
  const { renderMode } = useConfig();

  if (ComponentList) {
    if (renderMode === RenderMode.AUTHOR) {
      return jsxLight`<div>Container:</div>${ComponentList}`
    } else {
      return ComponentList;
    }
  } else {
    return jsxLight`
      <div>Loading ...</div>`;
  }
}