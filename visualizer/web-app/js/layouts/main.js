var com = com || {};
com.newiron = com.newiron || {};
com.newiron.layouts = com.newiron.layouts || {};
com.newiron.layouts.main = {

    vars : {

    },

    init : function () {

        $('.user-info-wrapper').mouseenter(function(event) {

            $(this).addClass('hover');

            $(this).animate({
                borderColor: '#989898'
            },'fast', function() {
                if($(this).hasClass('hover')) {
                    $(this).children('.actions').slideDown();
                }
            });

        }).mouseleave(function(event) {

            var $this = $(this);

            $this.removeClass('hover');

            $this.children('.actions').slideUp({
                complete: function() {
                    $this.animate({
                        borderColor: '#333333'
                    },'fast', function() {

                    });
                }
            });

        });

        $('.separator:first').css({
            backgroundImage: 'none',
            margin: '0px',
            marginRight: '20px'
        })

        /**
         * Modernize elements using PIE.js.
         */
        if(Modernizr && (!Modernizr.borderradius || !Modernizr.cssgradients)) {
            if (window.PIE) {
                $('.nav, .user-info-wrapper').each(function() {
                    PIE.attach(this);
                });

                $('.separator:gt(0)').each(function() {
                    PIE.attach(this);
                });
            }
        }

    }

}