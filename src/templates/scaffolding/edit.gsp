<%=packageName%>
<!doctype html>
<html>
	<head>

        <meta name="layout" content="main">

        <g:set var="entityName" value="\${g.message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
		<title>\${g.message(code: 'default.edit.label', args: [entityName])}</title>
        <r:require module="scaffold/edit"/>

	</head>
	<body>

        <content tag="nav">
            <li class="separator">&nbsp;</li>
            <li>
                <a class="home" href="\${createLink(uri: '/')}">\${g.message(code: 'default.home.label')}</a>
            </li>
            <li class="separator">&nbsp;</li>
            <li>
                <g:link class="list" action="list">\${g.message(code: 'default.list.label', args: [entityName])}</g:link>
            </li>
            <li class="separator">&nbsp;</li>
            <li>
                <g:link class="create" action="create">\${g.message(code: 'default.new.label', args: [entityName])}</g:link>
            </li>
		</content>

		<div id="edit-${domainClass.propertyName}" class="scaffold-edit">

            <h1>\${g.message(code: 'default.edit.label', args: [entityName])}</h1>

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

            <g:form action="update" method="post" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>

                <g:hiddenField name="id" value="\${${propertyName}?.id}" />
				<g:hiddenField name="version" value="\${${propertyName}?.version}" />

                <div class="box-2">
                    <fieldset class="fields">
                        <g:render template="form"/>
                    </fieldset>
                </div>

                <fieldset class="buttons clearfix">
					<input type="submit" class="button-1" value="\${g.message(code: 'default.button.update.label', default: 'Update')}" />
					<a href="#" id="delete">\${g.message(code: 'default.button.delete.label', default: 'Delete')}</a>
				</fieldset>

			</g:form>

            <form id="deleteForm" action="\${createLink(action: 'delete')}" method="POST">
                <g:hiddenField name="id" value="\${${propertyName}?.id}" />
            </form>

		</div>

        <div id="confirmDeleteDialog" class="hide">
            <div class="box-1">
                <div class="title">
                    \${g.message(code: 'default.button.delete.confirm.message.title', default: 'Are you sure?')}
                </div>

                <div class="content">
                    \${g.message(code: 'default.button.delete.confirm.message', default: 'This action cannot be undone.')}
                </div>

                <div class="buttons clearfix">
                    <button id="deleteConfirmed" class="button-1">\${g.message(code: 'default.button.delete.confirm.okay')}</button>
                    <a href="#" class="js-cancel">\${g.message(code: 'default.button.delete.confirm.cancel')}</a>
                </div>
            </div>
        </div>

        <g:javascript>
            \$(function() {
                com.newiron.scaffold.edit.init({
                    errors: ["\${${propertyName}.errors.getFieldErrors().collect{it.field}.join('","')}"]
                });
            });
        </g:javascript>

	</body>
</html>
