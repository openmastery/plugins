To release IDEA plugin to ideaflow.org:
 * increment the plugin version in intellij/META-INF/plugin.xml
 * Resolve any IDEA dependency changes (since building with IDEA) - in core run './gradlew idea', in ideaflow-plugin run './gradlew idea'
 * build plugin in IDEA.
 * execute './gradlew upload' (Note: gradle daemon must not be used, otherwise console will not work)

The build file is expecting the plugin to be named ideaflow-plugin.zip, so if your project isn't named that, the task will fail.  
