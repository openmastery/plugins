To release IDEA plugin to ideaflow.org:
 * increment the plugin version in intellij/META-INF/plugin.xml
 * Resolve any IDEA dependency changes (since building with IDEA) - in core run './gradlew idea', in intellij-plugin run './gradlew idea'
 * build plugin in IDEA.
 * execute './gradlew upload' (Note: gradle daemon must not be used, otherwise console will not work)