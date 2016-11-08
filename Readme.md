# Usage Instructions

The Idea Flow Mapping plugin measures the "friction" that occurs during developer experience to help developers run "Experience Reviews" with objective data.  To use the plugin:

After installing, you'll need to configure Preferences > Idea Flow and configure the API url, and API key to connect to an ifm-publisher server.  You can use the server at ifm-publisher.heroku.com or install a local server from https://github.com/openmastery/ifm-publisher

To get an API-Key for ifm-publisher.heroku.com, you'll need to get an API-key via email from the Open Mastery community.  You can join for free at openmastery.org, or request an API-key by emailing janelle@openmastery.org.

After the server is configured, create a new task from the drop down, click the unpause button, and start recording.  All data being sent to the server is first spooled to the <userHome>/.ideaflow directory so you have full visibility of the data collected.

## Project Setup Instructions

To setup the projects:

cd core
./gradlew idea
cd ../intellij-plugin
./gradlew idea


Within Intellij:

Create a new empty project.

Import module core using the core.iml (hit the + sign to add a module then select the core.iml file)
Set the project SDK to Java 1.6

Import module intellij-plugin using the intellij-plugin.iml
Set the Module SDK to IDEA 12 Common Edition

If you do not have the common edition, you'll need to download and install it on your machine.  Then from this dialog click "New > Intellij Platform SDK" and navigate to the ".app" file for the installation.



To setup the visualizer module:

Right click the project pane and choose New > Module
Create a new Grails Module (you'll need to have ultimate edition of Intellij for this to work)
Set the content root to the visualizer location.
Use Grails-2.2.4 SDK

If you do not have the Grails 2.2.4 SDK, you'll need to download and unzip somewhere on your machine.  Then from this dialog, click "Create..." and navigate to the grails home directory.  The gradle build will also expect the grails command to be available in your PATH.


Each project has a corresponding Readme.md file that contains instructions for how to do deployment.
