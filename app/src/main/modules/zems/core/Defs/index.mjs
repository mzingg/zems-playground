const ZemsRenderModeStorageId = 'ZemsRenderMode';
const ZemsRenderModeAuthorString = 'author';
const ZemsRenderModePublishString = 'publish';

export const RenderMode = Object.freeze({
  AUTHOR: Symbol(ZemsRenderModeAuthorString),
  PUBLISH: Symbol(ZemsRenderModePublishString),
  fromValue(value) {
    const { AUTHOR, PUBLISH } = this;
    if (value === ZemsRenderModePublishString) {
      return PUBLISH;
    }
    return AUTHOR;
  }
});

const renderModeValue = typeof window === 'object' && window.sessionStorage ? window.sessionStorage.getItem(ZemsRenderModeStorageId) : ZemsRenderModePublishString;

export const ZemsConfiguration = {
  renderMode: RenderMode.fromValue(renderModeValue),
};
