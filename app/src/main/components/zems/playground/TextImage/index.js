import htm from 'https://unpkg.com/htm?module';
// noinspection ES6PreferShortImport
import { useComponent } from '../../../zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnusedGlobalSymbols
export default function TextAndImage(props) {

  const html = htm.bind(React.createElement);
  const { text, imageSrc } = props;

  const TextComponent = useComponent({
    resourceType: 'zems/playground/Text',
    modelLoader: async () => ({ text })
  })

  const ImageComponent = useComponent({
    resourceType: 'zems/playground/Image',
    modelLoader: async () => ({ imageSrc })
  })

  function isReadyToRender() {
    return ImageComponent && TextComponent;
  }

  if (isReadyToRender()) {
    return html`
      <div>
        ${TextComponent}
        ${ImageComponent}
      </div>`;
  } else {
    return html`
      <div>Loading ...</div>`;
  }
}