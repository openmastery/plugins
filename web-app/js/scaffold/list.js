var com = com || {};
com.newiron = com.newiron || {};
com.newiron.scaffold = com.newiron.scaffold || {};
com.newiron.scaffold.list = {

    vars : {},

    init : function(opts) {

        $.extend(this.vars, opts);

        if($('.pagination').children().length <= 0) {
            $('.pagination').hide();
        }

    }

}