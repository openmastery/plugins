<div id="conflicts">
    <table class="tabular">
        <tbody>
        <g:each in="${conflicts}" var="${conflict}" status="${index}">
            <tr>
                <td>Conflict:</td>
                <td>${conflict.conflict}</td>
            </tr>
            <tr>
                <td>Resolution:</td>
                <td>${conflict.resolution}</td>
            </tr>
            <g:if test="${conflict.mistakeType}">
            <tr>
                <td>Mistake Type:</td>
                <td>${conflict.mistakeType}</td>
            </tr>
            </g:if>
            <g:if test="${conflict.cause}">
                <tr>
                    <td>Cause:</td>
                    <td>${conflict.cause}</td>
                </tr>
            </g:if>
            <tr>
                <td>Duration:</td>
                <td>${conflict.durationFormattedTime}</td>
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