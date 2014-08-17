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
    <r:require module="layouts/visualizer"/>

    <r:layoutResources/>
    <link rel="shortcut icon" href="${createLink(uri: '/images/favicon.ico?v=2')}" type="image/x-icon" />
</head>

<body>
<div class="header">
    <div class="nav option1">
        <ul>
            <li class="active"><a href="#">${createdDate}</a></li>
            <li><a href="#">${ifmName}</a></li>
        </ul>
        <img src="${resource(dir: 'images', file: 'flow.svg')}" alt="Idea Flow"/></a>
    </div>
</div>
<div id="timelineHolder" class="timeline-holder"></div>

<script type="text/javascript">

    function hello() {

    }
    registerStopWindowDragCallback(scrollToTimePosition);
    registerClickBandCallback(hello);
    refreshTimeline();
</script>
<div id="contentPanel">
    <g:include controller="highlight" action="list" params="[controller:'highlight']"/>
</div>

</body>
</html>