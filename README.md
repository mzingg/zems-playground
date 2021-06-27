##### Examples

###### Changing Property in component in all browsers
- Define function:
  `let pageCounter = (count)=>window.ContentBusClient.sendUpdate({changedPath: '/content/playground/de/de', payload: {pageTitle: `Page Title ${count}`} }); let globalPageCounter = 0;`
- Execute it regularly:
  `interv = setInterval(()=> {pageCounter(globalPageCounter++)}, 500)`
- Stop it:
  `clearInterval(interv)`  