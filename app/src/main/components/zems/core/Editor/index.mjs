import { jsxLight, React } from '../../../../modules/zems/core/React/index.mjs'; /*$ZEMS_SSR_RESOURCE$*/
import { useComponent, useKeyGenerator } from '../../../../modules/zems/core/ZemsReact/index.mjs'; /*$ZEMS_RESOURCE$*/
import { RenderMode } from '../../../../modules/zems/core/Defs/index.mjs'; /*$ZEMS_RESOURCE$*/
import { Configuration } from '../../../../modules/zems/playground/Config/index.mjs'; /*$ZEMS_RESOURCE$*/
import { sendUpdate } from "../../../../modules/zems/core/ContentBus/index.mjs"; /*$ZEMS_RESOURCE$*/

const { useState } = React;

export default function Editor(props) {
  const [editMode, setEditMode] = useState(false);
  const createKey = useKeyGenerator();
  const { renderMode } = Configuration;
  const { component: WrappedComponent, editPath } = props;

  const EditDialogComponent = useComponent({
    resourceType: 'zems/core/EditDialog',
    props: { dialogTitle: 'Edit Dialog', target: WrappedComponent.key }
  });

  const poke = (event) => {
    sendUpdate({ changedPath: editPath, payload: { text: 'changed Text' } })
  };

  const toggleEditMode = (event) => {
    setEditMode(!editMode);
    event.stopPropagation();
  };

  if (renderMode === RenderMode.AUTHOR) {
    const EditBar = jsxLight`
      <div key="${createKey()}" className="zems-editor__edit-bar btn-group" role="group" aria-label="Edit Bar">
      <div className="btn-group" role="group">
        <button id="btnGroupDrop1" type="button" className="btn btn-sm btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
          Add Component
        </button>
        <ul className="dropdown-menu" aria-labelledby="btnGroupDrop1">
          <li><a className="dropdown-item" href="#">Text</a></li>
          <li><a className="dropdown-item" href="#">Image</a></li>
          <li><a className="dropdown-item" href="#">Text And Image</a></li>
        </ul>
        </div>
        <button type="button" className="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#EditDialogModal">Properties</button>
        <button type="button" className="btn btn-sm btn-primary" onClick="${poke}">Delete</button>
        <button type="button" className="btn btn-sm btn-primary" onClick="${toggleEditMode}">Stop Editing</button>
      </div>
      ${EditDialogComponent}`
    return jsxLight`
      <div className="zems-editor__wrapper" key="${createKey()}" onDoubleClick="${toggleEditMode}">${editMode ? EditBar : ''}${WrappedComponent}</div>`;
  } else {
    return WrappedComponent;
  }
}