import { useKeyGenerator } from '../../../../modules/zems/core/ZemsReact'; /*$ZEMS_RESOURCE$*/
import { RenderMode } from '../../../../modules/zems/core/Defs'; /*$ZEMS_RESOURCE$*/
import { useConfig } from '../../../../modules/zems/playground/Config'; /*$ZEMS_RESOURCE$*/

// noinspection JSUnresolvedVariable
const { useState } = window.React;

// noinspection JSUnusedGlobalSymbols
export default function Editor(props) {
  const [editMode, setEditMode] = useState(false);
  const createKey = useKeyGenerator();
  const { renderMode } = useConfig();

  const toggleEditMode = (event) => {
    setEditMode(!editMode);
    event.stopPropagation();
  }

  const { component: WrappedComponent } = props;
  if (renderMode === RenderMode.AUTHOR) {
    const EditBar = jsxLight`
      <div className="zems-editor__edit-bar">
        <button>Properties</button>
        <button>Delete</button>
        <button onClick="${toggleEditMode}">Close</button>
      </div>`
    return jsxLight`
      <div className="zems-editor__wrapper" key="${createKey()}" onDoubleClick="${toggleEditMode}">${editMode ? EditBar : ''}${WrappedComponent}</div>`;
  } else {
    return WrappedComponent;
  }
}