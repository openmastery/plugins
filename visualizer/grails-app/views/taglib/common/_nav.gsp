<div id="nav" class="nav">
    <span class="menuButton">
        <g:remoteLink controller="interval" action='list' update='contentPanel'
                      class="${'interval'.equals(controller) ? 'active' : ''}">Timeline</g:remoteLink>
    </span>
    <span class="menuButton">
        <g:remoteLink controller="event" action='list' update='contentPanel'
                      class="${'event'.equals(controller) ? 'active' : ''}">Events</g:remoteLink>
    </span>
    <span class="menuButton">
        <g:remoteLink controller="timeBand" action='list' update='contentPanel'
                      class="${'timeBand'.equals(controller) ? 'active' : ''}">Highlights</g:remoteLink>
    </span>
    <span class="menuButton">
        <g:remoteLink controller="conflict" action='list' update='contentPanel'
                      class="${'conflict'.equals(controller) ? 'active' : ''}">Conflicts</g:remoteLink>
    </span>
</div>