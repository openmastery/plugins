<common:nav controller="${params.controller}" action="${params.action}"/>

<div id="intervals">
    <table class="tabular">
        <tbody>
        <g:each in="${intervals}" var="${interval}" status="${index}">
            <tr id="interval_${index}">
                <td>
                    ${interval.getStartTime(true)}
                </td>
                <td>
                    ${interval.comment}
                </td>
                <td>
                    ${interval.durationFormattedTime}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    showTimelineWindow(true);
    //turn ON green toggle
    //onHover, highlight position on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>