define(function (require) {
    const logger = require("app/logger")

    logger.componentLoaded("form")

    return {
        /**
         * @param {Object} options
         * @param options.formGroupId {string}
         * @param options.credits {[]=[]}
         * @param options.uploadUri {string}
         * @param options.method {string=POST}
         * @param options.outputOptions {{inputName: string, formGroupId: string}}
         */
        imageUploadForm: function (
            {formGroupId, uploadUri, outputOptions, credits = [], method = 'POST'},
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
         * @param options {Object}
         * @param options.triggerElemId {string}
         * @param options.confirmMessage {string}
         * @param options.deleteUri {string}
         * @param options.token {{name: string, value: string}}
         * @param options.onDoneRedirectTo {string}
         * @param options.method {string}
         */
        deleteRequest: function ({
                                     triggerElemId,
                                     confirmMessage,
                                     deleteUri,
                                     token,
                                     onDoneRedirectTo,
                                     method = "POST"
                                 }) {
            $('#' + triggerElemId).on('click', function () {
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
        },

        deleteSubImages: function () {
            $('.bo-delete-file').each(function () {
                $(this).on('click', function () {
                    $('<input type="hidden" name="delete-files[]" value="' + $(this).data('image') + '" >').appendTo('#delete-sub-files')
                    $(this).parents('.bo-sub-file-container').remove()
                })
            })
        },

        toggleSubImageIcons: function () {
            $('.bo-sub-file-container').each(function () {
                $(this).hover(
                    function () {
                        $(this).find('.bo-image-control').removeClass('bo-disabled').addClass('bo-active')
                    },
                    function () {
                        $(this).find('.bo-image-control').removeClass('bo-active').addClass('bo-disabled')
                    },
                )
            })
        },

        /**
         * @param postUri {string}
         * @param data {Object}
         */
        postRequest: function (postUri, data) {
            $.ajax({
                url: postUri,
                method: 'POST',
                data: data,
            }).done(function (response) {
                console.warn("response", response)
            })
        },
    };
})