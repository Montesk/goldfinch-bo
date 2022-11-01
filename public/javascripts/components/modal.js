define(function (require) {
    const logger = require("app/logger")

    logger.componentLoaded("modal")

    return {
        /**
         *
         * @param modalId {string} id with empty #
         */
        showOnLoad: function (modalId) {
            setTimeout(function () {
                $('#' + modalId + '').modal({show: true})
            }, 150)
        },

        /**
         *
         * @param modalId {string} id with empty #
         * @param redirectUrl {string}
         */
        redirectOnModalClose: function (modalId, redirectUrl) {
            $('#' + modalId + '').on('hide.bs.modal', function () {
                history.pushState({}, '', redirectUrl)
            })
        },
    }
})