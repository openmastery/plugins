var com = com || {};
com.newiron = com.newiron || {};
com.newiron.scaffold = com.newiron.scaffold || {};
com.newiron.scaffold.edit = {

    vars : {},

    init : function(opts) {

        $.extend(this.vars, opts);

        this.initComponents();
        this.assignActions();
        this.modernize();

    },

    initComponents : function() {

        var vars = this.vars;

        $('input, textarea').xbinputs();

        $.each(vars.errors, function(index, value) {
            $('#' + value).xbinputs('error', 'true');
        });

        $('#confirmDeleteDialog').dialog({
            autoOpen: false,
            modal: true,
            resizable: false,
            width: 400,
            height: 175
        });

    },

    assignActions: function() {

        $('#delete').click(function(event) {

            event.preventDefault();

            $('#confirmDeleteDialog').dialog('open');

        });

        $('#deleteConfirmed').click(function(event) {

            event.preventDefault();

            $('#deleteForm').submit();

        });

    },

    modernize : function() {

        var vars = this.vars;

        $('#confirmDeleteDialog').on('dialogopen', function() {

            if(!vars.modernized && Modernizr && (!Modernizr.boxsizing)) {

                $('#confirmDeleteDialog .box-1').each(function() {

                    var fullH = $(this).outerHeight(),
                        actualH = $(this).height(),
                        hDiff = fullH - actualH,
                        newH = actualH - hDiff;

                    $(this).css('height',newH);

                });

                vars.modernized = true;

            }

        });

    }

}