import htm from 'https://unpkg.com/htm?module';
// noinspection ES6PreferShortImport
import { useComponents } from '../../../zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnusedGlobalSymbols
export default function Container(props) {

  const html = htm.bind(React.createElement);
  const { components } = props;
  const ComponentList = useComponents(components)

  function isReadyToRender() {
    return ComponentList;
  }

  if (isReadyToRender()) {
    const { title } = props;
    return html`<h3>${title}</h3>
    <div>${ComponentList}</div>`;
  } else {
    return html`
      <div>Loading ...</div>`;
  }
}