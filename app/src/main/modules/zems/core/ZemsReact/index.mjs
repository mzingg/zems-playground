// noinspection ES6PreferShortImport,JSUnusedGlobalSymbols

import { uuidv4 } from '../../../../modules/zems/core/Lib/index.mjs'; /*$ZEMS_RESOURCE$*/
import { isServerSide, React, ReactDOM, ReactDOMServer } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { contentBusLoader, registerUpdateHandler } from '../../../../modules/zems/core/ContentBus/index.mjs'; /*$ZEMS_RESOURCE$*/

const { useState, useEffect, createElement } = React;
const { render } = ReactDOM;
const { renderToString } = ReactDOMServer;

const SupportedComponents = {};

/*
See:
- https://reactjs.org/docs/hooks-effect.html#effects-with-cleanup
- https://stackoverflow.com/questions/53949393/cant-perform-a-react-state-update-on-an-unmounted-component
 */
export const useAsyncEffect = ({ promiseOrAsyncFunction, dependencyArray, onSuccessFunction = null, cleanupOp = null }) => {
  useEffect(() => {
    let isMounted = true;
    promiseOrAsyncFunction.then(result => {
      if (isMounted && typeof onSuccessFunction === 'function' && result) onSuccessFunction(result)
    });
    return () => {
      isMounted = false;
      if (typeof cleanupOp === 'function') cleanupOp();
    };
  }, dependencyArray);
}

export const useProperty = (propertyName, props) => {
  if (!props[propertyName]) throw Error(`props[${propertyName}] must be defined for useProperty`);

  const [value, setterFunction] = useState(props[propertyName]);

  // add contentbus listener for property (if we have a path and an id)
  const { componentId, path: contentPath } = props;
  if (componentId && contentPath) {
    // TODO: add error propagation capabilities (when data.properties[propertyName] does not exest fex.)
    useAsyncEffect({
      promiseOrAsyncFunction: registerUpdateHandler({ contentPath, componentId, handlerFunction: (data) => setterFunction(data.properties[propertyName]) }),
      dependencyArray: []
    });
  }
  return [value, setterFunction];
}

export const useComponent = ({ resourceType, loadFrom = null, modelLoader = null, props = null }) => {
  const [component, setComponent] = useState(
      getReactComponentWithModel({ resourceType, loadFrom, modelLoader, props })
  );

  useAsyncEffect({
    promiseOrAsyncFunction: loadReactComponentWithModel({ resourceType, loadFrom, modelLoader, props }),
    dependencyArray: [],
    onSuccessFunction: setComponent
  })

  return component;
}

export const useComponents = (componentDefinitionArray) => {
  if (!componentDefinitionArray) {
    return [];
  }

  const [components, setComponents] = useState(
      getReactComponentsWithModel(componentDefinitionArray)
  );

  useAsyncEffect({
    promiseOrAsyncFunction: loadReactComponentsWithModel(componentDefinitionArray),
    dependencyArray: [],
    onSuccessFunction: setComponents
  })

  return components;
}

export const useKeyGenerator = () => {
  return () => uuidv4();
}

export const cmp = async ({ resourceType, renderId, modelLoader = null, props = null }) => {
  const ReactElement = await loadReactComponentWithModel({ resourceType, modelLoader, props });
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
        props: { pagePath: path }
      }
  );
  return renderToString(ReactElement);
}

const loadSupportedReactComponent = async ({ resourceType }) => {
  const { default: Component } = await import(`../../../../components/${resourceType}/index.mjs`); /*$ZEMS_RESOURCE$*/
  SupportedComponents[resourceType] = Component;

  return Component;
}

const getReactComponentWithModel = ({ resourceType, loadFrom = null, modelLoader = null, props = null }) => {
  if (!isServerSide) {
    return null;
  }
  const Component = SupportedComponents[resourceType];
  if (!Component) {
    throw Error(`Resource type ${resourceType} not supported for synchronous loading. Please call loadSupportedReactComponent first.`)
  }
  const modelLoaderToUse = getLoader(loadFrom, modelLoader, props);
  if (typeof modelLoaderToUse !== 'function') {
    throw Error('Only functions are supported as modelLoader')
  }

  const model = modelLoaderToUse();
  if (!model) {
    throw Error(`Error loading model for component ${resourceType} (loadFrom=${loadFrom}, modelLoader=${modelLoader}, props=${props})`)
  }
  if (model.key) {
    throw Error('Model must not have \'key\' property as this is used for uniquely identifying React components.')
  }

  const componentId = uuidv4();
  return createElement(Component, { ...model, key: componentId, componentId });
}

const loadReactComponentWithModel = async ({ resourceType, loadFrom = null, modelLoader = null, props = null }) => {
  const { default: Component } = await import(`../../../../components/${resourceType}/index.mjs`); /*$ZEMS_RESOURCE$*/

  const modelLoaderToUse = getLoader(loadFrom, modelLoader, props);
  const model = typeof modelLoaderToUse === 'function' ? modelLoaderToUse() : (modelLoaderToUse instanceof Promise ? await modelLoaderToUse : {});
  if (!model) {
    throw Error(`Error loading model for component ${resourceType} (loadFrom=${loadFrom}, modelLoader=${modelLoader}, props=${props})`)
  }
  if (model.key) {
    throw Error('Model must not have \'key\' property as this is used for uniquely identifying React components.')
  }

  const { default: Editor } = await import('../../../../components/zems/core/Editor/index.mjs'); /*$ZEMS_RESOURCE$*/

  const componentId = uuidv4();
  const wrappedComponent = createElement(Component, { ...model, key: componentId, componentId });
  if (props != null) {
    return wrappedComponent;
  } else {
    const editorComponentId = uuidv4();
    return createElement(Editor, { component: wrappedComponent, editPath: model.path, key: editorComponentId, componentId: editorComponentId });
  }
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

const getLoader = (loadFrom, modelLoader, props) => {
  let saveModelLoader;
  if (props != null) {
    saveModelLoader = () => props;
  } else if (loadFrom !== null && modelLoader === null) {
    saveModelLoader = contentBusLoader({ path: loadFrom });
  } else if (loadFrom == null && modelLoader === null) {
    saveModelLoader = () => {
    };
  } else {
    saveModelLoader = modelLoader;
  }

  return saveModelLoader;
};
