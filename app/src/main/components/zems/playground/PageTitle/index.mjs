import {jsxLight} from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import {Configuration} from '../../../../modules/zems/playground/Config/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import {useProperty} from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/

export default function PageTitle(props) {
    const {pageTitlePrefix} = Configuration;
    const [pageTitle, setPageTitle] = useProperty('pageTitle', props)

    //language=HTML
    return jsxLight`<h1>${pageTitlePrefix} | ${pageTitle}</h1>`;
}