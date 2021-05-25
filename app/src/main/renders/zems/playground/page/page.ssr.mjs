// noinspection BadExpressionStatementJS

globalThis.window = {
  env: { NODE_ENV: 'production' },
  SocketServerUrl: 'http://localhost:61734/zems-contentbus',
  IsSSR: true,
}

import { ssr } from 'app/src/main/modules/zems/core/ZemsReact/index.mjs';
ssr; // return the function to Java to be called there with the appropriate parameters