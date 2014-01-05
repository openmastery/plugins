<common:nav controller="${params.controller}" action="${params.action}"/>
<style>
.conflict:hover {
    background-color: #d3e0ff;
    cursor: pointer;
}

.conflict {
    display: inline-block;
    width: 700px;
}
.label {
    vertical-align: top;
    text-align: right;
    display: inline-block;
    width: 100px;
}

.description {
    display: inline-block;
    margin-left: 20px;
}

</style>

<div id="conflicts">
    <g:each in="${conflicts}" var="${conflict}" status="${index}">
        <div class="conflict" onmouseover="highlightConflict(${index})" onmouseout="resetColorBands()">
            <div>
                <span class="label">Conflict:</span>
                <span class="description">${conflict.conflict}</span>
            </div>

            <div>
                <span class="label">Resolution:</span>
                <span class="description">${conflict.resolution}</span>
            </div>
            <g:if test="${conflict.mistakeType}">
                <div>
                    <span class="label">Mistake Type:</span>
                    <span class="description">${conflict.mistakeType}</span>
                </div>
            </g:if>
            <g:if test="${conflict.cause}">
                <div>
                    <span class="label">Cause:</span>
                    <span class="description">${conflict.cause}</span>
                </div>
            </g:if>
            <div>
                <span class="label">Duration:</span>
                <span class="description">${conflict.durationFormattedTime}</span>
            </div>
        </div>

        <div>
            <span>&nbsp;</span>
            <span>&nbsp;</span>
        </div>
    </g:each>
</div>

<script type="text/javascript">
    showTimelineWindow(false);
    resetColorBands();
    //turn off green toggle
    //onHover, highlight event on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>