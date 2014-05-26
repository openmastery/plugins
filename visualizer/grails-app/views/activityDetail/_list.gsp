<%@ page import="com.ideaflow.timeline.Event; com.ideaflow.timeline.ActivityDetail" %>
<common:nav controller="${params.controller}" action="${params.action}"/>

<div id="timeline_scrollwindow" class="timedetail">
    <table class="tabular">
        <thead>
        <tr>
            <td>Raw Time</td>
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
                    <td>
                        ${detail.activityName}
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
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    showTimelineWindow(true);
    resetColorBands();
    $("#timeline_scrollwindow").on("scrollstop", {latency: 650}, updateTimelineWindowPosition);

    //$("#timeline_scrollwindow").on("scrollstop",
    //        function (event) {
    //            alert('hello');
    //            updateTimelineWindowPosition();
    //        });
    //onHover, highlight position on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>