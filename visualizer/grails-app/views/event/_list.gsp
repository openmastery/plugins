
<div id="events">
    <table class="tabular">
        <tbody>
        <g:each in="${events}" var="${event}" status="${index}">
            <tr id="event_${index}" class="${(index % 2) == 0 ? 'even' : 'odd'}">
                <td>
                    ${event.shortTime}
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
    //turn off green toggle
    //onHover, highlight event on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>