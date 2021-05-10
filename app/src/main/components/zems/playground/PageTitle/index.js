import htm from 'https://unpkg.com/htm?module';

// noinspection JSUnusedGlobalSymbols
export default function PageTitle(props) {
  const html = htm.bind(React.createElement);
  const { pageTitle } = props;
  return html`<h1>${pageTitle}</h1>`;
}