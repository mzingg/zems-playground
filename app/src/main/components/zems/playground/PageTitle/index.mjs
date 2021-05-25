import { jsxLight } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/

import { useConfig } from '../../../../modules/zems/playground/Config/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/

export default function PageTitle(props) {
  const { pageTitlePrefix } = useConfig();
  const { pageTitle } = props;

  return jsxLight`<h1>${pageTitlePrefix} | ${pageTitle}</h1>`;
}