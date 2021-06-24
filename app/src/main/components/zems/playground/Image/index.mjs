import {jsxLight} from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/

export default function Image(props) {
    const {imageSrc} = props;

    //language=HTML
    return jsxLight`<img src="${imageSrc}"/>`;
}