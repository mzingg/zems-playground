import { useComponent } from '../../../../modules/zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnusedGlobalSymbols
export default function TextAndImage(props) {
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
    return jsxLight`
      <div>
        ${TextComponent}
        ${ImageComponent}
      </div>`;
  } else {
    return jsxLight`
      <div>Loading ...</div>`;
  }
}