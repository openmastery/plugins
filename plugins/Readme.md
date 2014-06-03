To release IDEA plugin:
 * Build plugin in IDEA.
 * execute './gradlew update' (Note: gradle daemon must not be used, otherwise console will not work)

The build file is expecting the plugin to be named ideaflow-plugin.zip, so if your project isn't named that, the task will fail.  
