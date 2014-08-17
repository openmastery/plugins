<%@ page import="com.ideaflow.timeline.TimeBand; com.ideaflow.timeline.Event" %>
<common:nav controller="${params.controller}" action="${params.action}"/>
<style>
.eventrow:hover {
    background-color: #d3e0ff;
    cursor: pointer;
}
</style>

<div id="timeBands" class="tabcontent">
    <table class="tabular">
        <tbody>
        <g:each in="${timeEntries}" var="${timeEntry}" status="${index}">
            <g:if test="${timeEntry instanceof Event}">
                <tr id="time_${index}" class="eventrow" onmouseover="highlightEventById(${timeEntry.id})"
                    onmouseout="resetEventLines()">
                    <td>
                        <hr/>
                    </td>
                    <td>
                        ${timeEntry.time.shortTime}
                    </td>
                    <td>
                        Event - ${timeEntry.comment}
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;
                    </td>
                </tr>
            </g:if>
            <g:if test="${timeEntry instanceof TimeBand}">
                <tr id="event_${index}" class="eventrow" onmouseover="highlightColorBandById(${timeEntry.id})"
                    onmouseout="resetColorBands()">
                    <td class="${timeEntry.bandType}type">
                        &nbsp;&nbsp;&nbsp;
                    </td>
                    <td>
                        ${timeEntry.startPosition.shortTime} - ${timeEntry.endPosition.shortTime}
                    </td>
                    <td>
                        ${timeEntry.bandType.toString().capitalize()} - ${timeEntry.comment}
                    </td>
                    <td>
                        <span class="${timeEntry.bandType}pie">${timeEntry.percent}/100</span>&nbsp;
                    ${timeEntry.duration.hourMinSec}
                    </td>
                </tr>
            </g:if>
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    showTimelineWindow(false);
    resetColorBands();
    drawHighlightPies();
    //turn off green toggle
    //onHover, highlight band on timeline
    //onHover, highlight row and make X appear
    //onClick, open new highlight form
</script>