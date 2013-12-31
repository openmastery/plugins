var com = com || {};
com.newiron = com.newiron || {};
com.newiron.login = com.newiron.login || {};
com.newiron.login.auth = {

    vars : {

    },

    init : function (opts) {

        $.extend(this.vars, opts);

        var vars = this.vars;

        if(vars.hasCookie) {
            $('#remember_me').attr('checked','checked');
        }

        $('input').xbinputs();

        $('#username').focus();

    }

}