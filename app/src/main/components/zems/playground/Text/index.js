import htm from 'https://unpkg.com/htm?module';

// noinspection JSUnusedGlobalSymbols
export default function Text(props) {
  const html = htm.bind(React.createElement);
  const { text } = props;
  return html`
    <div className="text">${text}</div>`;
}