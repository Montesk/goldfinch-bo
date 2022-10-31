define(function (require) {
    const logger = require("app/logger")

    logger.componentLoaded("edit form logic ready")

    return {
        /**
         * @param formGroupId {string}
         * @param uploadUri {string}
         * @param outputOptions {{ inputName: string, formGroupId: string }}
         * @param method {string}
         * @param credits {any[]}
         */
        initFileUpload: function (
            formGroupId,
            uploadUri,
            outputOptions,
            method = 'POST',
            credits = [],
        ) {
            // https://pqina.nl/filepond/docs/api/plugins/image-preview/
            // https://pqina.nl/filepond/docs/api/instance/events/
            // https://pqina.nl/filepond/docs/api/server/

            const pond = FilePond.create(
                document.getElementById(formGroupId),
                {
                    credits: credits,
                    server: {
                        url: uploadUri,
                        method: method,
                    },
                }
            )

            pond.on('processfiles', () => {
                $('input[name="filepond"]').each(function () {
                    if (!$(this).val()) {
                        return
                    }
                    console.log('Image added', $(this).val())
                    $('<input type="hidden" name="' + outputOptions.inputName + '" value="' + $(this).val() + '" >').appendTo('#' + outputOptions.formGroupId)
                })
            });
        },

        /**
         *
         * @param triggerId {string}
         * @param confirmMessage {string}
         * @param deleteUri {string}
         * @param token {{name: string, value: string}}
         * @param onDoneRedirectTo {string}
         * @param method {string}
         */
        deleteProduct: function (triggerId, confirmMessage, deleteUri, token, onDoneRedirectTo, method = "POST") {
            $('#' + triggerId).on('click', function () {
                if (confirm(confirmMessage)) {
                    $.ajax({
                        url: deleteUri,
                        method: method,
                        data: {
                            [token.name]: token.value,
                        },
                    }).done(function (response) {
                        console.warn(response)
                        window.location.href = onDoneRedirectTo
                    })
                }
            })
        }
    };
})