<%--
  Created by IntelliJ IDEA.
  User: katrea
  Date: 12/31/13
  Time: 7:08 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="visualizer"/>
    <g:javascript library="ideaflow"/>

    <r:layoutResources/>
</head>

<body>
<div id="timelineHolder"></div>
<g:render template="timeline"/> <!-- this goes to timeline holder, will need to pass ifm file into template -->

<div id="contentPanel">
    <g:include controller="event" action="list" params="[controller:'event']"/>
</div>
</body>
</html>