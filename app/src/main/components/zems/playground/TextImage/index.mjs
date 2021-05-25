// noinspection JSUnusedGlobalSymbols

import { jsxLight } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_RESOURCE$*/

import { useComponent } from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/

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

  if (ImageComponent && TextComponent) {
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