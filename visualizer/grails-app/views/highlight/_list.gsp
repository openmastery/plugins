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
                <td class="${band.bandType}type">
                    &nbsp;&nbsp;&nbsp;
                </td>
                <td>
                    ${band.startPosition.shortTime} - ${band.endPosition.shortTime}
                </td>
                <td>
                    ${band.bandType}
                    <g:if test="${band.bandType == "conflict"}">
                     - ${band.question}
                    </g:if>
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