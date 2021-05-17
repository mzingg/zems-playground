// noinspection JSUnusedGlobalSymbols
export default function Text(props) {
  const { text } = props;
  return jsxLight`
    <div className="text">${text}</div>`;
}