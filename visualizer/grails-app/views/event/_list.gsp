<common:nav controller="${params.controller}" action="${params.action}"/>
<style>
.eventrow:hover {
      background-color: #d3e0ff;
      cursor:pointer;
}
</style>
<div id="events">
    <table class="tabular">
        <tbody>
        <g:each in="${events}" var="${event}" status="${index}">
            <tr id="event_${index}" class="eventrow" onmouseover="highlightEvent(${index})" onmouseout="resetEventLines()">
                <td>
                    ${event.time.shortTime}
                </td>
                <td>
                    ${event.comment}
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
    //onHover, highlight event on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>