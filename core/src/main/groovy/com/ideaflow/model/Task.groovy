package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Task {

    String taskId
    String user
    String project
    String baseUrl
    String calculatedUrl

    boolean hasTaskId() {
        return taskId != null && !taskId.isAllWhitespace()
    }
}