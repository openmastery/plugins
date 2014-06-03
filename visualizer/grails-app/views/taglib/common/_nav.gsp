<div id="nav" class="nav">
    <span class="menuButton default ${'activityDetail'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="activityDetail" action='list' update='contentPanel'>Timeline</g:remoteLink>
    </span>
    <span class="menuButton default ${'event'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="event" action='list' update='contentPanel'>Events</g:remoteLink>
    </span>
    <span class="menuButton default ${'highlight'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="highlight" action='list' update='contentPanel'>Highlights</g:remoteLink>
    </span>
    <span class="menuButton ${'conflict'.equals(controller) ? 'active' : ''}">
        <g:remoteLink controller="conflict" action='list' update='contentPanel'>Conflicts</g:remoteLink>
    </span>
</div>