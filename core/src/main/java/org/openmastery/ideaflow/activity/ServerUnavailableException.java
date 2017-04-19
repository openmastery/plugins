package org.openmastery.ideaflow.activity;


/**
 * NOTE: Exception classes must be java, not groovy due to the need to compile on jdk6 and potentially run on jdk7+
 * http://blog.proxerd.pl/article/how-to-fix-incompatibleclasschangeerror-for-your-groovy-projects-running-on-jdk7
 */
public class ServerUnavailableException extends RuntimeException {

	ServerUnavailableException(String message) {
		super(message);
	}

}
