// noinspection JSUnresolvedVariable,JSUnresolvedFunction

export const withGraalVMClient = () => {
  if (!ContentBusService) {
    throw Error('ContentBusService not set - must be executed inside Zems SSR rendering context')
  }

  return {
    loader({ path }) {
      return () => ContentBusService.getProperties(path);
    },
    registerUpdateHandler({ componentId, handlerFunction }) {
      // not implemented on serverside
    },
    unRegisterUpdateHandler({ componentId }) {
      // not implemented on serverside
    },
    sendUpdate({ componentId, payload }) {
      // TODO: theoretically this would be possible to implement - is there a use case that we send messages to the bus on SSR rendering?
      // not implemented on serverside
    }
  }
};