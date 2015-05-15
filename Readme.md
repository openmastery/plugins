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