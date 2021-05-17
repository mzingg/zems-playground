// noinspection JSUnusedGlobalSymbols
export default function Image(props) {
  const { imageSrc } = props;

  return jsxLight`<img src="${imageSrc}"/>`;
}