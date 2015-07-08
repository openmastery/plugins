package com.ideaflow.model


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