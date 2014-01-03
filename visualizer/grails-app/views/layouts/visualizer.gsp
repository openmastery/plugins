<%--
  Created by IntelliJ IDEA.
  User: katrea
  Date: 1/2/14
  Time: 9:15 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:layoutTitle default="Visualizer"/></title>
    <style>
    #header {
        background-color: #ffe0e0;
        text-align: center;
    }

    #footer {
        background-color: #e0e0ff;
        text-align: center;
    }
    </style>
    <g:javascript library="monolith"/>
    <g:layoutHead/>
    <r:layoutResources/>
</head>

<body>
<common:header/>
<div id="timelineHolder"></div>
<common:nav controller="${params.controller}" action="${params.action}"/>
<g:layoutBody/>
<div id="contentPanel">
    <g:include controller="event" action="list" />
</div>
<common:footer/>
</body>
</html>