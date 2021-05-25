const PageModel = {
  pageTitle: 'Page Title From Contentbus',
  contentParsys: {
    components: [
      {
        resourceType: 'zems/playground/TextImage',
        modelLoader: () => ({
          text: 'A lorem ipsum text 1',
          imageSrc: 'data:image/gif;base64,R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7'
        })
      },
      {
        resourceType: 'zems/playground/TextImage',
        modelLoader: () => ({
          text: 'A lorem ipsum text 2',
          imageSrc: ''
        })
      },
      {
        resourceType: 'zems/playground/TextImage',
        modelLoader: () => ({
          text: 'A lorem ipsum text 3',
          imageSrc: ''
        })
      },
      {
        resourceType: 'zems/playground/TextImage',
        modelLoader: () => ({
          text: 'A lorem ipsum text 4',
          imageSrc: ''
        })
      },
      {
        resourceType: 'zems/playground/TextImage',
        modelLoader: () => ({
          text: 'A lorem ipsum text 5',
          imageSrc: ''
        })
      },
      {
        resourceType: 'zems/playground/Text',
        modelLoader: () => ({
          text: 'A lorem ipsum text 6',
        })
      },
      {
        resourceType: 'zems/core/Container',
        modelLoader: () => ({
          components: [
            {
              resourceType: 'zems/playground/Text',
              modelLoader: () => ({
                text: 'Container Component 1',
              })
            },
            {
              resourceType: 'zems/playground/Text',
              modelLoader: () => ({
                text: 'Container Component 2',
              })
            },
          ]
        })
      },
      {
        resourceType: 'zems/playground/Text',
        modelLoader: () => ({
          text: 'A lorem ipsum text 8',
        })
      },
      {
        resourceType: 'zems/playground/Text',
        modelLoader: () => ({
          text: 'A lorem ipsum text 9',
        })
      },
      {
        resourceType: 'zems/playground/Image',
        modelLoader: () => ({
          imageSrc: 'data:image/gif;base64,R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7'
        })
      },
    ]
  }
}

export const withMockClient = () => ({
  loader({ path }) {
    if (path === '/content/playground/de/de') {
      return () => PageModel;
    } else if ('/content/playground/de/de>contentParsys') {
      return () => PageModel.contentParsys;
    }
  }
});
