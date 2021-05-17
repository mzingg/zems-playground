import { useComponents } from '../../../../modules/zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnusedGlobalSymbols
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