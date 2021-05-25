// noinspection JSUnusedGlobalSymbols

import { React, jsxLight } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { useComponents } from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/
import { RenderMode } from '../../../../modules/zems/core/Defs/index.mjs'; /*$ZEMS_RESOURCE$*/
import { useConfig } from '../../../../modules/zems/playground/Config/index.mjs'; /*$ZEMS_RESOURCE$*/

export default function Container(props) {
  const { components } = props;
  const { renderMode } = useConfig();

  const ComponentList = useComponents(components)

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