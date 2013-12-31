<!doctype html>
<html>

    <head>
		<title>Server Error</title>
		<meta name="layout" content="login">
        <r:require module="error"/>
	</head>

    <body>

        <div class="errors">
            HTTP 500: Server Error.
        </div>

        <g:if env="development">
            <div id="errorDialog">
                <g:renderException exception="${exception}"/>
            </div>
        </g:if>

        <g:javascript>
            $(function() {
                com.newiron.error.init();
            });
        </g:javascript>

	</body>

</html>