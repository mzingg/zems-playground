import {jsxLight, React} from '../../../../modules/zems/core/React/index.mjs';
import {useProperty} from "../../../../modules/zems/core/ZemsReact/index.mjs";

export default function Text(props) {
    const text = useProperty('text', props)

    //language=HTML
    return jsxLight`
        <div className="text">${text}</div>`;
}