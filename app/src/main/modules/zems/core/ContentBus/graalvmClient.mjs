// noinspection JSUnresolvedVariable,JSUnresolvedFunction

export const withGraalVMClient = () => {
  if (!ContentBusService) {
    throw Error('ContentBusService not set - must be executed inside Zems SSR rendering context')
  }

  return {
    loader({ path }) {
      return () => ContentBusService.getProperties(path);
    }
  }
};