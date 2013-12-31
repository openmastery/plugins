var com = com || {};
com.newiron = com.newiron || {};
com.newiron.scaffold = com.newiron.scaffold || {};
com.newiron.scaffold['create'] = {

    vars : {},

    init : function(opts) {

        $.extend(this.vars, opts);

        this.initComponents();
//        this.assignActions();
//        this.modernize();

    },

    initComponents : function() {

        var vars = this.vars;

        $('input, textarea').xbinputs();

        $.each(vars.errors, function(index, value) {
            $('#' + value).xbinputs('error', 'true');
        });

    }

}