<g:if test="${id}">
    <div id="${id}">
</g:if>
<g:if test="${flash.message}">
    <div class="message">
        <span>${message(code: flash.message, args: flash.messageArgs)}</span>
    </div>
</g:if>
<g:if test="${flash.warn}">
    <div class="errors">
        <ul>
            <li>
                <g:message code="${flash.warn}" args="${flash.warnArgs}"/>
            </li>
        </ul>
    </div>
</g:if>
<g:hasErrors>
    <div class="errors">
        <g:renderErrors/>
    </div>
</g:hasErrors>
<g:if test="${flash?.error}">
    <div class="errors">
        <ul>
            <li>
                <g:message code="${flash.error}" args="${flash.errorArgs}"/>
            </li>
        </ul>
    </div>
</g:if>
<g:if test="${flash?.errors}">
    <div class="errors">
        <ul>
            <g:each in="${flash.errors}" var="${error}">
                <li>
                    <g:message code="${error.code}" args="${error.args}"/>
                </li>
            </g:each>
        </ul>
    </div>
</g:if>
<g:if test="${id}">
    </div>
</g:if>