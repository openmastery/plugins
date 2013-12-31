<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

        <meta name="description" content="">
        <meta name="viewport" content="width=device-width,initial-scale=1.0">

        <title><g:layoutTitle/> - New Iron</title>

        <g:layoutHead/>

        <r:require module="layouts/main"/>
        <r:layoutResources />

        <link rel="shortcut icon" href="${createLink(uri: '/images/favicon.ico')}" type="image/x-icon" />

    </head>
    <body>
        <div class="layout-wrapper">
            <div class="layout-header">
                <div class="header-wrapper clearfix">
                    <a href="http://www.newiron.com" class="logo">
                        <r:img uri="/images/newiron_logo.png"/>
                    </a>
                    <div class="user-info-wrapper">
                        <div class="underline clearfix">
                            <div class="user-info">
                                <sec:ifLoggedIn>
                                    <span>Logged in as ${sec.loggedInUserInfo(field: 'username')}.</span>
                                </sec:ifLoggedIn>
                                <sec:ifNotLoggedIn>
                                    <span>Hello, stranger.</span>
                                </sec:ifNotLoggedIn>
                                <span class="ui-icon ui-icon-triangle-1-s">&nbsp;</span>
                            </div>
                        </div>
                        <div class="actions">
                            <sec:ifLoggedIn>
                                <a href="${createLink(controller: 'logout')}">Logout</a>
                            </sec:ifLoggedIn>
                            <sec:ifNotLoggedIn>
                                <a href="${createLink(controller: 'login')}">Login</a>
                            </sec:ifNotLoggedIn>
                        </div>
                    </div>
                </div>
                <div class="nav">
                    <ul>
                        <g:if test="${pageProperty(name: 'page.nav')}">
                            <g:pageProperty name="page.nav"/>
                        </g:if>
                        <g:else>
                            <li class="separator">&nbsp;</li>
                            <a href="${createLink(uri: '/')}">Home</a>
                        </g:else>
                    </ul>
                </div>
            </div>
            <div class="layout-content">
                <g:layoutBody/>
            </div>
            <div class="layout-footer">
                Copyright &copy; ${java.util.Calendar.getInstance().get(Calendar.YEAR)} New Iron Group. All rights reserved.
                <br/>
                ${meta(name: 'app.name')} v${meta(name: 'app.version')}:${render(template: '/version')}
                <br/>
                Built on: ${render(template: '/buildDate')}
            </div>
        </div>

        <script type="text/javascript">
            $(function() {
                com.newiron.global.init();
                com.newiron.layouts.main.init();
            });
        </script>

        <r:layoutResources />

    </body>
</html>