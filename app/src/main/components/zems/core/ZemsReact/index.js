export const useComponent = ({ resourceType, modelLoader }) => {
  const [component, setComponent] = React.useState();

  React.useEffect(() => {
    if (!component) {
      load({ resourceType, modelLoader })
          .then(c => setComponent(c));
    }
  });

  return component;
}

export const useComponents = (componentArray) => {
  const [components, setComponents] = React.useState();

  React.useEffect(() => {
    if (!components) {
      loadAll(componentArray)
          .then(c => setComponents(c));
    }
  });

  return components;
}

export const cmp = async ({ resourceType, renderId, modelLoader }) => {
  const ReactElement = await load({ resourceType, modelLoader });
  ReactDOM.render(ReactElement, document.getElementById(renderId));
}

const load = async ({ resourceType, modelLoader }) => {
  const { default: component } = await import(`../../../${resourceType}`); /*$ZEMS_RESOURCE$*/
  const model = await modelLoader();
  return React.createElement(component, { ...model, key: uuidv4() });
}

const loadAll = async (componentsArray) => {
  let result = [];
  for (let i = 0; i < componentsArray.length; i++) {
    result.push(await load(componentsArray[i]));
  }
  return result;
}

/* Thanks to https://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid */
function uuidv4() {
  return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  );
}