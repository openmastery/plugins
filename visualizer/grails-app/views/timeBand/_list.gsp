<common:nav controller="${params.controller}" action="${params.action}"/>
<style>
.eventrow:hover {
    background-color: #d3e0ff;
    cursor:pointer;
}
</style>
<div id="timeBands">
    <table class="tabular">
        <tbody>
        <g:each in="${timeBands}" var="${band}" status="${index}">
            <tr id="event_${index}" class="eventrow" onmouseover="highlightColorBand(${index})" onmouseout="resetColorBands()">
                <td style="background-color: ${band.color}">
                    &nbsp;&nbsp;&nbsp;
                </td>
                <td>
                    ${band.startTime} - ${band.endTime}
                </td>
                <td>
                    ${band.bandType} ${band.comment ? '-' : ''} ${band.comment}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    showTimelineWindow(false);
    resetColorBands();
    //turn off green toggle
    //onHover, highlight band on timeline
    //onHover, highlight row and make X appear
    //onClick, open new highlight form
</script>