<% import grails.persistence.Event %>
<%=packageName%>
<!doctype html>
<html>
	<head>

        <meta name="layout" content="main">

        <g:set var="entityName" value="\${g.message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
        <r:require module="scaffold/show"/>

	</head>
	<body>

        <content tag="nav">
            <li class="separator">&nbsp;</li>
            <li>
                <a class="home" href="\${createLink(uri: '/')}">\${g.message(code :'default.home.label')}</a>
            </li>
            <li class="separator">&nbsp;</li>
            <li>
                <g:link class="list" action="list">\${g.message(code :'default.list.label', args: [entityName])}</g:link>
            </li>
            <li class="separator">&nbsp;</li>
            <li>
                <g:link class="create" action="create">\${g.message(code :'default.new.label', args: [entityName])}</g:link>
            </li>
		</content>

		<div id="show-${domainClass.propertyName}" class="content scaffold-show">

            <h1>\${g.message(code: 'default.show.label', args: [entityName])}</h1>

			<g:if test="\${flash.message}">
			    <div class="message">\${flash.message}</div>
			</g:if>

			<div class="property-list box-2 ${domainClass.propertyName}">

                <%  excludedProps = Event.allEvents.toList() << 'id' << 'version'
                    allowedNames = domainClass.persistentProperties*.name << 'dateCreated' << 'lastUpdated'
                    props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) }
                    Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                    props.each { p -> %>

                    <g:if test="\${${propertyName}?.${p.name}}">
                        <div class="fieldcontain">
                            <span id="${p.name}-label" class="property-label">\${g.message(code: '${domainClass.propertyName}.${p.name}.label', default: '${p.naturalName}')}</span>
                            <%  if (p.isEnum()) { %>
                                <span class="property-value"><g:fieldValue bean="\${${propertyName}}" field="${p.name}"/></span>
                            <%  } else if (p.oneToMany || p.manyToMany) { %>
                                <g:each in="\${${propertyName}.${p.name}}" var="${p.name[0]}">
                                    <span class="property-value"><g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${p.name[0]}.id}">\${${p.name[0]}?.encodeAsHTML()}</g:link></span>
                                </g:each>
                            <%  } else if (p.manyToOne || p.oneToOne) { %>
                                <span class="property-value"><g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${propertyName}?.${p.name}?.id}">\${${propertyName}?.${p.name}?.encodeAsHTML()}</g:link></span>
                            <%  } else if (p.type == Boolean || p.type == boolean) { %>
                                <span class="property-value"><g:formatBoolean boolean="\${${propertyName}?.${p.name}}" /></span>
                            <%  } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
                                <span class="property-value"><g:formatDate date="\${${propertyName}?.${p.name}}" /></span>
                            <%  } else if(!p.type.isArray()) { %>
                                <span class="property-value"><g:fieldValue bean="\${${propertyName}}" field="${p.name}"/></span>
                            <%  } %>
                        </div>
                    </g:if>

                <%  } %>

			</div>

            <g:form action="delete">
				<fieldset class="buttons">
					<g:hiddenField name="id" value="\${${propertyName}?.id}" />
                    <a id="edit" href="\${createLink(action: 'edit', params: [id: ${propertyName}?.id])}" class="button-1">\${g.message(code: 'default.button.edit.label', default: 'Edit')}</a>
                    <a id="delete" href="#">\${g.message(code: 'default.button.delete.label', default: 'Delete')}</a>
                </fieldset>
			</g:form>

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
                com.newiron.scaffold.show.init();
            });
        </g:javascript>

	</body>
</html>
