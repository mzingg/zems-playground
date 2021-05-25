// noinspection JSUnusedGlobalSymbols

import { jsxLight } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/

export default function Text(props) {
  const { text } = props;
  return jsxLight`
    <div className="text">${text}</div>`;
}