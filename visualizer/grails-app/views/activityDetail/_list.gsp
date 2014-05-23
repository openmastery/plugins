<common:nav controller="${params.controller}" action="${params.action}"/>

<div id="intervals">
    <table class="tabular">
        <thead>
        <tr>
            <td>
                Time (h:m:s)
            </td>
            <td>
                File
            </td>
            <td>
                Duration (s)
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${activities}" var="${activity}" status="${index}">
            <tr id="interval_${index}">
                <td>
                    ${activity.time.relativeOffset}
                </td>
                <td>
                    ${activity.activityName}
                </td>
                <td>
                    ${activity.duration}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    showTimelineWindow(true);
    resetColorBands();
    //turn ON green toggle
    //onHover, highlight position on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>