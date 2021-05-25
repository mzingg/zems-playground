import { jsxLight, React } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { useComponents } from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/

export default function Container(props) {
  const { components } = props;

  const ComponentList = useComponents(components)

  if (ComponentList) {
    return ComponentList;
  } else {
    return jsxLight`
      <div>Loading ...</div>`;
  }
}