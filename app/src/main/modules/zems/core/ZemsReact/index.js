// noinspection ES6PreferShortImport

import 'https://unpkg.com/react@17/umd/react.production.min.js';
import 'https://unpkg.com/react-dom@17/umd/react-dom.production.min.js';
import htm from 'https://unpkg.com/htm?module';

import { uuidv4 } from '../../../../modules/zems/core/Lib'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnresolvedVariable
const { useState, useEffect, createElement } = window.React;
// noinspection JSUnresolvedVariable
const { render } = window.ReactDOM;

if (!window.jsxLight) {
  window.jsxLight = htm.bind(createElement);
}

export const useComponent = ({ resourceType, modelLoader }) => {
  const [component, setComponent] = useState();

  useEffect(() => {
    if (!component) {
      load({ resourceType, modelLoader })
          .then(c => setComponent(c));
    }
  }, [component]);

  return component;
}

export const useComponents = (componentArray) => {
  const [components, setComponents] = useState();

  useEffect(() => {
    if (componentArray && !components) {
      loadAll(componentArray)
          .then(c => setComponents(c));
    }
  });

  return components;
}

export const useKeyGenerator = () => {
  return () => uuidv4();
}

export const cmp = async ({ resourceType, renderId, modelLoader= null }) => {
  const ReactElement = await load({ resourceType, modelLoader });
  render(ReactElement, document.getElementById(renderId));
}

const load = async ({ resourceType, modelLoader = null }) => {
  const { default: Component } = await import(`../../../../components/${resourceType}`); /*$ZEMS_RESOURCE$*/
  const model = modelLoader ? (await modelLoader)() : {};
  if (model?.key) {
    throw Error('Model must not have \'key\' property as this is used for uniquely identifying React components.')
  }

  const { default: Editor } = await import('../../../../components/zems/core/Editor'); /*$ZEMS_RESOURCE$*/

  const wrappedComponent = createElement(Component, { ...model, key: uuidv4() });
  return createElement(Editor, { component: wrappedComponent, key: uuidv4() });
}

const loadAll = async (componentsArray) => {
  let result = [];
  for (let i = 0; i < componentsArray.length; i++) {
    result.push(await load(componentsArray[i]));
  }
  return result;
}
