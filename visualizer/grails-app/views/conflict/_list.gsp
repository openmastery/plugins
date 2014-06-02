<common:nav controller="${params.controller}" action="${params.action}"/>

<div id="conflicts" class="tabcontent">
    <g:each in="${conflicts}" var="${conflict}" status="${index}">
        <div class="conflict" onmouseover="highlightConflict(${conflict.id})" onmouseout="resetColorBands()">
            <div class="header">
                <span class="pie">${conflict.percent}/100</span>
                <span class="label">Conflict:</span>
                <span class="description">${conflict.question}</span>
            </div>
            <div class="content">
                <div>
                    <span class="label">Resolution:</span>
                    <span class="description">${conflict.answer}</span>
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
                    <span class="description">${conflict.duration.hourMinSec}</span>
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
    drawPies();
    //turn off green toggle
    //onHover, highlight event on timeline
    //onHover, highlight row and make X appear
    //onClick, toggle to form
</script>