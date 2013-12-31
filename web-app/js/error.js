var com = com || {};
com.newiron = com.newiron || {};
com.newiron.error = {

    vars: {},

    init : function() {

        $('#errorDialog').dialog({
            modal: true,
            draggable: false,
            resizable: false,
            width: 700,
            height: 700
        });

    }

}
