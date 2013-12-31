<%=packageName%>
<!doctype html>
<html>
	<head>

        <meta name="layout" content="main">

        <g:set var="entityName" value="\${g.message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
		<title>\${g.message(code: 'default.create.label', args: [entityName])}</title>
        <r:require module="scaffold/create"/>

	</head>
	<body>

        <content tag="nav">
            <li class="separator">&nbsp;</li>
            <li>
                <a class="home" href="\${createLink(uri: '/')}">\${g.message(code :'default.home.label')}</a>
            </li>
            <li class="separator">&nbsp;</li>
            <li>
                <g:link class="list" action="list">\${g.message(code: 'default.list.label', args: [entityName])}</g:link>
            </li>
		</content>

		<div id="create-${domainClass.propertyName}" class="scaffold-create">

            <h1>\${g.message(code: 'default.create.label', args: [entityName])}</h1>

			<g:if test="\${flash.message}">
			    <div class="message">\${flash.message}</div>
			</g:if>

			<g:hasErrors bean="\${${propertyName}}">
                <ul class="errors">
                    <g:eachError bean="\${${propertyName}}" var="error">
                        <li>\${g.message(error: error)}</li>
                    </g:eachError>
                </ul>
			</g:hasErrors>

            <g:form action="save" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>

                <div class="form box-2">
                    <fieldset class="fields">
                        <g:render template="form"/>
                    </fieldset>
                </div>

                <div class="buttons clearfix">
                    <input type="submit" class="save button-1" value="\${g.message(code: 'default.button.create.label', default: 'Create')}">
                </div>

            </g:form>

		</div>

        <g:javascript>
            \$(function() {
                com.newiron.scaffold.create.init({
                    errors: ["\${${propertyName}.errors.getFieldErrors().collect{it.field}.join('","')}"]
                });
            });
        </g:javascript>

	</body>
</html>
