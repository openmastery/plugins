To release the visualizer:

* Increment the version number in application.properties

* Manually delete the core jar from the grails cache:  'rm -rf ~/.grails/ivy-cache/ideaflow'
* From visualizer folder, run: './gradlew upload'

Bounce vagrant to download the new visualizer:

* From vagrant folder in here: (git clone https://github.com/ideaflow/visualizer.git)
* vagrant destroy
* vagrant up