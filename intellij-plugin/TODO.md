# GUI changes
## TODO add meta data (in pref panel)

Properties would be:

- taskId
- <strike>description</strike>
- user
- project
- URL

The URL would be an automatically generated REST call that defaulted to:

http://localhost:8989/<project>/<user>/<taskId>

a GET request on that URL should show the IFM for the task

Once we have a sharable team server functionality, the developer could change the URL to the team's Idea Flow server

This GET request would show a list of recent tasks for the project and user:

http://localhost:8989/<project>/<user>

### Notes

Adding information to IdeaFlowMaps...

taskId, author, project

We need additional information for IFMs, but want to minimize annoyance with sensible defaults.
Data tracked at the story/IFM level.  Use cases:
Find IFMs by user or project.
Associate IFMs with Rally story (using taskId)
Look through a list of IFMs and know what's what (table that includes: task, author, project).
Future: project/user/task for URLs

Create default preferences for project and author that will auto-populate when creating a new IFM.

# TODO Create settings wizard
I've got an idea I like better than both options actually...

What if when you created a new task, it prompted you for all the fields,
but some fields it defaults to whatever you entered in last time?

No preference pane, constant validation of settings, no errors the first time you use
it because you didn't validate the settings, and you can change them easily if you like.
I think it's much more mistake-proof.

# TODO Merge Tasks
# TODO REst layer (PUT IFM events/GET initial state)
# Notes
---------

Allow conflicts that switch to learning/rework to be "rolled up" as a grouped aggregate conflict.

Biggest problems would be sorted by their aggregates, with children ignored. (biggest problems up top)
Timeline would include the rolledup entries, rolling over the group would highlight the group on the timeline.
Aggregate data analysis would avoid double-counting problems for these cases... also have the initial learning case, that doesn't start w/conflict.

---------

Add configurable reminders that can be toggled on and off.  

When IFM is created, reminder for initial prediction: "What do you expect this IFM to look like?"
Future: Project-wide configured reminders to enforce whatever the rules are for the team.

----------

When a conflict is clicked/selected - highlight the details from that conflict.
- would this go for a group too?

---------

Allow blue bands to be ontop of yellow.

Put content message in tooltip when a band is active (the thing currently in the description)

When in the rework for a conflict, the message should essentially be "end rework for this conflict"

Make icons for nesting - when in conflict then flip to yellow, change the yellow icon to be rework for the conflict (put a red bulb in the corner).
When rework of conflict is ended, goes back to normal bulb.  

When in conflict, and press blue/yellow, the icons ought to be the yellow/red ones.

You are in learning and seem to be typing away... are you sure you didn't forget to turn off the band?







Clean up working set stuff

---- BUILD STUFF

Release process for plugin, current problems:

Since building in Intellij, need to rebuild the idea project if any dependencies change (this time we added spock) of both core and intellij plugin.  Wasn't in the process, needs to be a process step.

Uploading visualizer jar requires wiping out old version first or replacement doesn't work

---- REST API RELEASE

'context-switching' support - starting new IF recording will create an [active task context] drop down, and allow you to switch easily between multiple active tasks.  When the task is finished, it can be 'closed' and dropped from the active list.  The active list is essentially the WIP.

Use cases:

* Create new context - instead of the record on/off toggle button, have a non-toggle button that creates a new IF context.  Clicking the button should open a dialog that requests the project, user, taskId, and description.  Once the task is created, the taskId should display in a select box on the toolbar with the name of the task.

* No context available - When no contexts are available, recording is disabled, all buttons are disabled except for the one to create a new context.

* Close active context - Click the close button on the toolbar to close the active context.  If no other contexts are available, recording is disabled until one exists.  If another context is available, switch to the next context in the list. The button states (active lightbulbs) should change to that of the new active context.

* Switch context - Select a different context from the drop down list.  Button state should switch to the new context.

-----------

Use case: How do you reopen something that was closed in the past?  If we type in the ids for the old ticket, it shouldn't "reinitialize" if the IFM already exists, or should it?  Maybe multiple initialization blocks is ok?  Maybe we just change to open/close?  But task switching otherwise doesn't register open/closes?

Possible solution: Move the description and initialization stuff to the startRecording() element that occurs when the task is added (or re-added) to the active set.  Drop the "initialize" block.  These will show the "sessions" within the context.

Save the description field to the IFM when the task is opened (or resumed?)


Create a preference pane to be able to configure the base URL and basic metadata for default values: user, project.  These should be URL friendly since they will be used in the resource string

Create a button to open active task context in IF visualizer.

Visualizer (backend code change) - show last open conflict as running until the end of the IFM, even though it's still open.  The state of whether there is an active conflict or not should still report correctly.

Multi-project support limitation - switching between active windows of the same project does not trigger file activity events and will not record correctly when switching back and forth.


------- SUPPORT/MAINTENANCE STUFF

Refactor tests to use builders

add intellij plugin tests

------ FUTURE

Add a 'timeline scrubber' to be able to go back in time to a commit snapshot with any event thats recorded and open up files that were open around that time.  Can go back and try to remember what happened.  Do the same thing for conflicts.
