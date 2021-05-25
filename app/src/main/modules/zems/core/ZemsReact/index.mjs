// noinspection ES6PreferShortImport,JSUnresolvedVariable,JSUnusedGlobalSymbols,JSUnresolvedFunction

import { uuidv4 } from '../../../../modules/zems/core/Lib/index.mjs'; /*$ZEMS_RESOURCE$*/
import { React, ReactDOM, ReactDOMServer } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { ContentBusLoader } from "../../../../modules/zems/core/ContentBus.SSR/index.mjs"; /*$ZEMS_SSR_RESOURCE$*/
// import { ContentBusLoader } from '../../../../modules/zems/core/ContentBus/index.mjs'; /*$ZEMS_RESOURCE$*/

const { useState, useEffect, createElement } = React;
const { render } = ReactDOM;
const { renderToString } = ReactDOMServer;

export const useComponent = ({ resourceType, path = undefined, modelLoader = undefined }) => {
  const [component, setComponent] = useState();

  useEffect(() => {
    if (!component) {
      loadReactComponentWithModel({ resourceType, modelLoader: path ? ContentBusLoader({ path }) : modelLoader })
          .then(c => setComponent(c));
    }
  }, []);

  return component;
}

export const useComponents = (componentArray) => {
  const [components, setComponents] = useState();

  useEffect(() => {
    if (!components) {
      loadReactComponentsWithModel(componentArray)
          .then(c => setComponents(c));
    }
  }, []);

  return components;
}

export const useKeyGenerator = () => {
  return () => uuidv4();
}

export const cmp = async ({ resourceType, renderId, modelLoader = null }) => {
  const ReactElement = await loadReactComponentWithModel({ resourceType, modelLoader });
  render(ReactElement, document.getElementById(renderId));
}

export const ssr = async ({ resourceType, path }) => {
  const ReactElement = await loadReactComponentWithModel({
        resourceType,
        modelLoader: () => ({ pagePath: path })
      }
  );
  return renderToString(ReactElement);
}

const loadReactComponentWithModel = async ({ resourceType, modelLoader = null }) => {
  const { default: Component } = await import(`../../../../components/${resourceType}/index.mjs`); /*$ZEMS_RESOURCE$*/
  const model = await (modelLoader ? (await modelLoader)() : {});
  if (model?.key) {
    throw Error('Model must not have \'key\' property as this is used for uniquely identifying React components.')
  }

  const { default: Editor } = await import('../../../../components/zems/core/Editor/index.mjs'); /*$ZEMS_RESOURCE$*/

  const wrappedComponent = createElement(Component, { ...model, key: uuidv4() });
  return createElement(Editor, { component: wrappedComponent, key: uuidv4() });
}

const loadReactComponentsWithModel = async (componentsArray) => {
  let result = [];
  for (let i = 0; i < componentsArray.length; i++) {
    result.push(await loadReactComponentWithModel(componentsArray[i]));
  }
  return result;
}
