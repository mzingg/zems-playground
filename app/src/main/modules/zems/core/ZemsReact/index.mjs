// noinspection ES6PreferShortImport,JSUnusedGlobalSymbols

import { uuidv4 } from '../../../../modules/zems/core/Lib/index.mjs'; /*$ZEMS_RESOURCE$*/
import { isServerSide, React, ReactDOM, ReactDOMServer } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
// import { ContentBusLoader } from "../../../../modules/zems/core/ContentBus.SSR/index.mjs"; /*$ZEMS_SSR_RESOURCE$*/
import { ContentBusLoader } from '../../../../modules/zems/core/ContentBus/index.mjs'; /*$ZEMS_RESOURCE$*/

const { useState, useEffect, createElement } = React;
const { render } = ReactDOM;
const { renderToString } = ReactDOMServer;

const SupportedComponents = {};

export const useComponent = ({ resourceType, path = null, modelLoader = null }) => {
  const usedModelLoader = path ? ContentBusLoader({ path }) : modelLoader;
  const [component, setComponent] = useState(
      getReactComponentWithModel({ resourceType, modelLoader: usedModelLoader })
  );

  useEffect(() => {
    loadReactComponentWithModel({ resourceType, modelLoader: usedModelLoader })
        .then(c => setComponent(c));
  }, []);

  return component;
}

export const useComponents = (componentDefinitionArray) => {
  const [components, setComponents] = useState(getReactComponentsWithModel(componentDefinitionArray));

  useEffect(() => {
    loadReactComponentsWithModel(componentDefinitionArray)
        .then(c => setComponents(c));
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
  await loadSupportedReactComponent({ resourceType: 'zems/playground/App' })
  await loadSupportedReactComponent({ resourceType: 'zems/playground/PageTitle' })
  await loadSupportedReactComponent({ resourceType: 'zems/core/Container' })
  await loadSupportedReactComponent({ resourceType: 'zems/playground/Text' })
  await loadSupportedReactComponent({ resourceType: 'zems/playground/Image' })
  await loadSupportedReactComponent({ resourceType: 'zems/playground/TextImage' })

  const ReactElement = getReactComponentWithModel({
        resourceType,
        modelLoader: () => ({ pagePath: path })
      }
  );
  return renderToString(ReactElement);
}

const loadSupportedReactComponent = async ({ resourceType }) => {
  const { default: Component } = await import(`../../../../components/${resourceType}/index.mjs`); /*$ZEMS_RESOURCE$*/
  SupportedComponents[resourceType] = Component;

  return Component;
}

const getReactComponentWithModel = ({ resourceType, modelLoader = null }) => {
  if (!isServerSide) {
    return null;
  }
  const Component = SupportedComponents[resourceType];
  if (!Component) {
    throw Error(`Resource type ${resourceType} not supported for synchronous loading. Please call loadSupportedReactComponent first.`)
  }
  const modelLoaderToUse = modelLoader ? modelLoader : () => {
  };
  if (typeof modelLoaderToUse !== 'function') {
    throw Error('Only functions are supported as modelLoader')
  }
  const model = modelLoaderToUse();
  if (model?.key) {
    throw Error('Model must not have \'key\' property as this is used for uniquely identifying React components.')
  }

  return createElement(Component, { ...model, key: uuidv4() });
}

const loadReactComponentWithModel = async ({ resourceType, modelLoader = null }) => {
  const { default: Component } = await import(`../../../../components/${resourceType}/index.mjs`); /*$ZEMS_RESOURCE$*/

  const modelLoaderToUse = modelLoader ? modelLoader : () => {
  };
  const model = typeof modelLoaderToUse === 'function' ? modelLoaderToUse() : (modelLoaderToUse instanceof Promise ? await modelLoaderToUse : {});
  if (model?.key) {
    throw Error('Model must not have \'key\' property as this is used for uniquely identifying React components.')
  }

  const { default: Editor } = await import('../../../../components/zems/core/Editor/index.mjs'); /*$ZEMS_RESOURCE$*/

  const wrappedComponent = createElement(Component, { ...model, key: uuidv4() });
  return createElement(Editor, { component: wrappedComponent, key: uuidv4() });
}

const getReactComponentsWithModel = (componentsArray) => {
  if (!isServerSide) {
    return null;
  }
  let result = [];
  for (let i = 0; i < componentsArray.length; i++) {
    result.push(getReactComponentWithModel(componentsArray[i]));
  }
  return result;
}

const loadReactComponentsWithModel = async (componentsArray) => {
  let result = [];
  for (let i = 0; i < componentsArray.length; i++) {
    result.push(await loadReactComponentWithModel(componentsArray[i]));
  }
  return result;
}
