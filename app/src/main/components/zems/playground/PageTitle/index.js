// noinspection ES6PreferShortImport

import { useConfig } from '../../../../modules/zems/playground/Config'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnusedGlobalSymbols
export default function PageTitle(props) {
  const { pageTitlePrefix } = useConfig();
  const { pageTitle } = props;
  return jsxLight`<h1>${pageTitlePrefix} | ${pageTitle}</h1>`;
}