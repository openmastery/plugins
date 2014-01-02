modules = {

    application {
        dependsOn 'monolith'
    }

    monolith {

        dependsOn 'jquery', 'reset', 'normalize', 'html5boilerplate', 'modernizr', 'pie', 'kineticjs', 'jqxbinputs', 'global'

        overrides {
            jquery {
                defaultBundle 'monolith'
            }

            'jquery-ui' {

                defaultBundle 'monolith'

                dependsOn 'reset', 'normalize', 'html5boilerplate'
            }

            'jquery-theme' {

                dependsOn 'jquery-ui'

                resource id: 'theme', bundle: 'monolith'

            }
        }

    }

    global {

        defaultBundle 'monolith'

        dependsOn 'jquery-ui', 'jquery-theme', 'jqueryForms'

        resource url: '/css/global/boxes.css'
        resource url: '/css/global/buttons.css'
        resource url: '/css/global/errors.css'
        resource url: '/css/global/utils.css'
        resource url: '/css/global/jqueryui-override.css'
        resource url: '/css/global/pagination.css'
        resource url: '/css/global/tables.css'
        resource url: '/js/global/global.js'

    }

    reset {

        defaultBundle 'monolith'

        resource url: '/css/lib/reset/reset.css'

    }

    normalize {

        dependsOn 'reset'

        defaultBundle 'monolith'

        resource url: '/css/lib/normalize/normalize.css'

    }

    html5boilerplate {

        dependsOn 'jquery', 'reset', 'normalize'

        defaultBundle 'monolith'

        resource url: '/css/lib/html5boilerplate/html5boilerplate.css'
        resource url: '/js/lib/html5boilerplate/html5boilerplate.js', disposition: 'head'

    }

    pie {

        dependsOn 'jquery'

        resource url: '/js/lib/pie/PIE.js', disposition: 'head', wrapper: { s -> "<!--[if IE]>$s<![endif]-->" }

    }

    kineticjs {

        dependsOn 'jquery'

        resource url: '/js/lib/kineticjs/kinetic-v4.7.4.min.js'

    }

    modernizr {

        defaultBundle 'monolith'

        resource url: '/js/lib/modernizr/modernizr-2.6.2.js'
        resource url: '/js/lib/modernizr/modernizr-boxsizing.js'

    }

    jqueryForms {

        dependsOn 'jquery'

        defaultBundle 'monolith'

        resource url: '/js/lib/jqueryForms/jquery.form.js'

    }

    jqpluginFactory {

        dependsOn 'jquery'

        defaultBundle 'monolith'

        resource url: '/js/lib/jq-plugin-factory/jq.plugin-factory.js', disposition: 'head'

    }

    jqxbinputs {

        dependsOn 'jquery', 'jqpluginFactory', 'modernizr', 'pie'

        defaultBundle 'monolith'

        resource url: '/css/lib/jq-xbinputs/jq.xbinputs.min.css'
        resource url: '/js/lib/jq-xbinputs/jq.xbinputs.js'

    }

    error {

        dependsOn 'monolith'

        resource url: '/css/error.css'
        resource url: '/js/error.js'

    }

    'scaffold/list' {

        dependsOn 'monolith'

        resource url: '/js/scaffold/list.js'

    }

    'scaffold/create' {

        dependsOn 'monolith'

        resource url: '/css/scaffold/create.css'
        resource url: '/js/scaffold/create.js'

    }

    'scaffold/show' {

        dependsOn 'monolith'

        resource url: '/css/scaffold/show.css'
        resource url: '/js/scaffold/show.js'

    }

    'scaffold/edit' {

        dependsOn 'monolith'

        resource url: '/css/scaffold/edit.css'
        resource url: '/js/scaffold/edit.js'

    }

    'layouts/main' {

        dependsOn 'monolith'

        resource url: '/css/layouts/main.css'
        resource url: '/js/layouts/main.js'

    }

    'layouts/login' {

        dependsOn 'monolith'

        resource url: '/css/layouts/login.css'
        resource url: '/js/layouts/login.js'

    }

    'login/auth' {

        dependsOn 'monolith'

        resource url: '/css/login/auth.css'
        resource url: '/js/login/auth.js'

    }

}