var com = com || {};
com.newiron = com.newiron || {};
com.newiron.global = {

    vars : {

    },

    init : function () {

        /**
         * Modernize elements using PIE.js.
         */
        if(Modernizr && (!Modernizr.borderradius || !Modernizr.cssgradients)) {
            if (window.PIE) {
                $('.box-1, .box-1 *').each(function() {
                    PIE.attach(this);
                });

                $('.box-2').each(function() {
                    PIE.attach(this);
                });

                $('.button-1').each(function() {
                    PIE.attach(this);
                });

                if($('.pagination').html() != "") {
                    $('.pagination').each(function() {
                        PIE.attach(this);
                    });
                }

                $('.table-wrapper, .table-wrapper th').each(function() {
                    PIE.attach(this);
                });
            }
        }

        $('.table-wrapper tr:last').children('td').css({
            borderBottom: '1px solid transparent'
        });

        /**
         * Customizes Jquery-ui dialogs making them easier to style.
         */
        $(document).on('dialogcreate', 'div', function(event, ui) {

            var $currentTarget = $(event.currentTarget),
                $dialog = $currentTarget.parents('.ui-dialog');

            // remove title bar
            $dialog.find('.ui-dialog-titlebar').remove();

        });

        $(document).on('dialogopen', function(event, ui) {

            // prevent buttons from auto-focusing
            $('*').blur();

        });

        $(document).on('click', '.ui-dialog-content .buttons .js-cancel', function(event) {

            event.preventDefault();

            $(this).parents('.ui-dialog-content').dialog('close');

        });

        /**
         * Clicking on a modal overlay causes dialogs to close.
         */
        $(document).on('click', '.ui-widget-overlay', function(event) {
            $('.ui-dialog-content').dialog('close');
        });

    }

}