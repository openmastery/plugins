<div id="nav" class="nav">
    <span class="menuButton default ${'activityDetail'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="activityDetail" action='list' update='contentPanel'>File Activity</g:remoteLink>
    </span>
    <span class="menuButton default ${'highlight'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="highlight" action='list' update='contentPanel'>Timeline</g:remoteLink>
    </span>
    <span class="menuButton ${'conflict'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="conflict" action='list' update='contentPanel'>Conflicts</g:remoteLink>
    </span>
</div>