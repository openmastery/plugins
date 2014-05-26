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
</head>

<body>

<div id="timelineHolder" class="timeline-holder"></div>

<script type="text/javascript">
    function hello() {
        //alert('hello');
    }
    registerStopWindowDragCallback(hello);
    registerClickBandCallback(hello);
    refreshTimeline();
</script>
<div id="contentPanel">
    <g:include controller="event" action="list" params="[controller:'event']"/>
</div>

</body>
</html>