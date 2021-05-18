import { useKeyGenerator, useComponent } from '../../../../modules/zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/
import { RenderMode } from '../../../../modules/zems/core/Defs'; /*$ZEMS_RESOURCE$*/
import { useConfig } from '../../../../modules/zems/playground/Config'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnresolvedVariable
const { useState } = window.React;

// noinspection JSUnusedGlobalSymbols
export default function Editor(props) {
  const [editMode, setEditMode] = useState(false);
  const createKey = useKeyGenerator();
  const { renderMode } = useConfig();

  const EditDialogComponent = useComponent({
    resourceType: 'zems/core/EditDialog',
    modelLoader: () => { return { dialogTitle: 'Edit Dialog' } }
  });

  const toggleEditMode = (event) => {
    setEditMode(!editMode);
    event.stopPropagation();
  }

  const { component: WrappedComponent } = props;
  if (renderMode === RenderMode.AUTHOR) {
    const EditBar = jsxLight`
      <div className="zems-editor__edit-bar btn-group" role="group" aria-label="Edit Bar">
      <div class="btn-group" role="group">
        <button id="btnGroupDrop1" type="button" class="btn btn-sm btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
          Add Component
        </button>
        <ul class="dropdown-menu" aria-labelledby="btnGroupDrop1">
          <li><a class="dropdown-item" href="#">Text</a></li>
          <li><a class="dropdown-item" href="#">Image</a></li>
          <li><a class="dropdown-item" href="#">Text And Image</a></li>
        </ul>
        </div>
        <button type="button" className="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#EditDialogModal">Properties</button>
        <button type="button" className="btn btn-sm btn-primary">Delete</button>
        <button type="button" className="btn btn-sm btn-primary" onClick="${toggleEditMode}">Stop Editing</button>
      </div>
      ${EditDialogComponent}`
    return jsxLight`
      <div className="zems-editor__wrapper" key="${createKey()}" onDoubleClick="${toggleEditMode}">${editMode ? EditBar : ''}${WrappedComponent}</div>`;
  } else {
    return WrappedComponent;
  }
}