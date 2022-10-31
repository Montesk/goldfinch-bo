// Client-side magnific popup library logic

define(function (require) {
    const logger = require("app/logger")

    $(document).ready(function () {
        $('.bo-mfp-container').magnificPopup({
            delegate: 'a',
            type: 'image',
            gallery: {enabled: true},
        });

        $('.bo-mfp-show').magnificPopup({type: 'image'});
    })

    logger.componentLoaded("magnific popup image preview")
})


