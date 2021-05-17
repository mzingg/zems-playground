export const RenderMode = Object.freeze({
  AUTHOR: Symbol('author'),
  PUBLISH: Symbol('publish'),
  fromValue(value) {
    const { AUTHOR, PUBLISH } = this;
    if (value === 'publish') {
      return PUBLISH;
    }
    return AUTHOR;
  }
});
