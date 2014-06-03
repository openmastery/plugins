<common:nav controller="${params.controller}" action="${params.action}"/>
<style>
.eventrow:hover {
    background-color: #d3e0ff;
    cursor:pointer;
}
</style>
<div id="timeBands" class="tabcontent">
    <table class="tabular">
        <tbody>
        <g:each in="${timeBands}" var="${band}" status="${index}">
            <tr id="event_${index}" class="eventrow" onmouseover="highlightColorBand(${index})" onmouseout="resetColorBands()">
                <td class="${band.bandType}type">
                    &nbsp;&nbsp;&nbsp;
                </td>
                <td>
                    ${band.startPosition.shortTime} - ${band.endPosition.shortTime}
                </td>
                <td>
                    ${band.bandType.toString().capitalize()} - ${band.comment}
                </td>
                <td>
                    <span class="${band.bandType}pie">${band.percent}/100</span>&nbsp;
                    ${band.duration.hourMinSec}
                </td>
            </tr>
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