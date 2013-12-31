<html>

    <head>
        <meta name="layout" content="login"/>
        <title>${message(code: 'springSecurity.login.title')}</title>

        <r:require module="login/auth"/>
    </head>

    <body>

        <form action="${postUrl}" method="POST" id="loginForm" class="cssform" autocomplete="off">

            <g:if test="${flash.error}">
                <div class="errors">
                    ${flash.error}
                </div>
            </g:if>

            <div class="field">
                <input id="username" type="text" name="j_username" title="${message(code: "springSecurity.login.username.label")}" tabindex="1"/>
            </div>
            <div class="field">
                <input id="password" type="password" name="j_password" title="${message(code: "springSecurity.login.password.label")}" tabindex="2"/>
            </div>
            <div class="field">
                <div class="checkbox-wrapper">
                    <input id="remember_me" type="checkbox" name="${rememberMeParameter}" tabindex="3"/>
                    <label for="remember_me">${message(code:'springSecurity.login.remember.me.label')}</label>
                </div>
                <input id="submit" type="submit" class="button-1" value="${message(code: "springSecurity.login.button")}" tabindex="4">
            </div>
            <div style="clear: both">
                &nbsp;
            </div>
        </form>

        <g:javascript>
            $(function() {
                com.newiron.login.auth.init({
                    hasCookie : ${hasCookie != null ? hasCookie : 'false'}
                });
            });

        </g:javascript>

    </body>

</html>
