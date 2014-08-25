<%@ page import="com.ideaflow.timeline.IdleDetail; com.ideaflow.timeline.TimeBand; com.ideaflow.timeline.Event; com.ideaflow.timeline.ActivityDetail" %>
<common:nav controller="${params.controller}" action="${params.action}"/>

<div id="timeline_scrollwindow" class="tabcontent">
    <table class="tabular">
        <thead>
        <tr>
            <td>Time (h:m:s)</td>
            <td>File</td>
            <td class="right">Duration (s)</td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${detailEntries}" var="${detail}" status="${index}">
            <g:if test="${detail instanceof ActivityDetail}">
                <tr id="detail_${index}" class="${detail.activeBandType ? "${detail.activeBandType}type" : ""}">
                    <td class="hiddenOffset">
                        ${detail.time.relativeOffset}
                    </td>
                    <td>
                        ${detail.time.longTime}
                    </td>
                    <td class="${detail.isModified() ? "modified" : ""}">
                        ${detail.activityName}
                    </td>
                    <td class="right">
                        ${detail.duration.hourMinSec}
                    </td>
                </tr>
            </g:if>
            <g:if test="${detail instanceof IdleDetail}">
                <tr id="detail_${index}" class="idletype">}">
                    <td class="hiddenOffset">
                        ${detail.time.relativeOffset}
                    </td>
                    <td>
                        ${detail.time.longTime}
                    </td>
                    <td>
                        [Idle] ${detail.comment}
                    </td>
                    <td class="right">
                        ${detail.duration.hourMinSec}
                    </td>
                </tr>
            </g:if>
            <g:if test="${detail instanceof Event}">
                <tr id="detail_${index}" class="${detail.activeBandType ? "${detail.activeBandType}type" : ""}">
                    <td class="hiddenOffset">
                        ${detail.time.relativeOffset}
                    </td>
                    <td colspan=2>
                        <hr/>
                    </td>
                    <td class="right">
                        ${detail.comment}
                    </td>
                </tr>
            </g:if>
            <g:if test="${detail instanceof TimeBand}">
                <tr id="detail_${index}" class="${detail.bandType}type">
                    <td class="hiddenOffset">
                        ${detail.time.relativeOffset}
                    </td>
                    <td colspan=2>
                        <hr/>
                    </td>
                    <td class="right">
                        ${detail.comment}
                    </td>
                </tr>
            </g:if>
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    showTimelineWindow(true);
    resetColorBands();
    $("#timeline_scrollwindow").on("scrollstop", {latency: 100}, updateTimelineWindowPosition);

    //$("#timeline_scrollwindow").on("scrollstop",
    //        function (event) {
    //            alert('hello');
    //            updateTimelineWindowPosition();
    //        });
    //onHover, highlight position on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>