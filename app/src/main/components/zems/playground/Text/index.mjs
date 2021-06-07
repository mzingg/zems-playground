import {jsxLight, React} from '../../../../modules/zems/core/React/index.mjs';
import {useProperty} from "../../../../modules/zems/core/ZemsReact/index.mjs";

export default function Text(props) {
    const [text, setText] = useProperty('text', props)

    return jsxLight`
    <div className="text">${text}</div>`;
}