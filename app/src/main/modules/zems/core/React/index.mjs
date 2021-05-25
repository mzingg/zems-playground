// noinspection JSUnresolvedFunction,JSUnresolvedVariable

export const isServerSide = typeof load === 'function';
if (isServerSide) {
  load('../../../../../../node_modules/react/umd/react.development.js'); /*$ZEMS_RESOURCE$*/
  load('../../../../../../node_modules/react-dom/umd/react-dom.development.js'); /*$ZEMS_RESOURCE$*/
  load('../../../../../../node_modules/react-dom/umd/react-dom-server.browser.development.js'); /*$ZEMS_RESOURCE$*/
  load('../../../../../../node_modules/htm/dist/htm.umd.js'); /*$ZEMS_RESOURCE$*/
}

const scope = typeof window === 'object' ? window : globalThis;
export const React = scope.React;
export const ReactDOM = scope.ReactDOM;
export const ReactDOMServer = scope.ReactDOMServer;
export const jsxLight = scope.htm.bind(scope.React.createElement);

