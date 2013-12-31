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
        <meta name="viewport" content="width=device-width">

        <title><g:layoutTitle/> - New Iron</title>

        <g:layoutHead/>

        <r:require module="layouts/login"/>
        <r:layoutResources />

        <link rel="shortcut icon" href="${createLink(uri: '/images/favicon.ico')}" type="image/x-icon" />

	</head>
	<body>

        <!-- Don't hate me for this Table... I needed it because display:table is not available in IE7. -->
        <table>
            <tr>
                <td>
                    <div class="login-content">
                        <a href="http://www.newiron.com" class="logo">
                            <r:img uri="/images/newiron_logo.png"/>
                        </a>
                        <g:layoutBody/>
                    </div>
                </td>
            </tr>
        </table>

        <div class="layout-footer">
            Copyright &copy; ${java.util.Calendar.getInstance().get(Calendar.YEAR)} New Iron Group. All rights reserved.
            <br/>
            ${meta(name: 'app.name')} v${meta(name: 'app.version')}:${render(template: '/version')}
            <br/>
            Built on: ${render(template: '/buildDate')}
        </div>

        <script type="text/javascript">
            $(function() {
                com.newiron.global.init();
                com.newiron.layouts.login.init();
            });
        </script>

		<r:layoutResources />

	</body>
</html>