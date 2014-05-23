<common:nav controller="${params.controller}" action="${params.action}"/>

<div id="conflicts">
    <g:each in="${conflicts}" var="${conflictBand}" status="${index}">
        <div class="conflict" onmouseover="highlightConflict(${index})" onmouseout="resetColorBands()">
            <div class="header">
                <span class="label">Conflict:</span>
                <span class="description">${band.conflict.question}</span>
            </div>
            <div class="content">
                <div>
                    <span class="label">Resolution:</span>
                    <span class="description">${band.resolution.answer}</span>
                </div>
                <g:if test="${band.conflict.mistakeType}">
                    <div>
                        <span class="label">Mistake Type:</span>
                        <span class="description">${band.conflict.mistakeType}</span>
                    </div>
                </g:if>
                <g:if test="${band.conflict.cause}">
                    <div>
                        <span class="label">Cause:</span>
                        <span class="description">${band.conflict.cause}</span>
                    </div>
                </g:if>
                <div>
                    <span class="label">Duration:</span>
                    <span class="description">${band.duration}</span>
                </div>
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