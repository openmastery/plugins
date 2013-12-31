var com = com || {};
com.newiron = com.newiron || {};
com.newiron.layouts = com.newiron.layouts || {};
com.newiron.layouts.login = {

    vars : {},

    init : function() {

        /**
         * IE has a weird bug where resizing the window makes some elements overlap.
         * It's fixed by redrawing the element in question.
         */
        if($('.ie7')) {
            $(window).resize(function() {
                // Redraw login content
                $('.login-content').hide().show();
            });
        }

    }

}