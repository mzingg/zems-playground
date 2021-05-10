import htm from 'https://unpkg.com/htm?module';

// noinspection JSUnusedGlobalSymbols
export default function Image(props) {
  const html = htm.bind(React.createElement);
  const { imageSrc } = props;

  return html`<img src="${imageSrc}"/>`;
}