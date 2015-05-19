package com.ideaflow.intellij.settings;

import com.ideaflow.model.Task;
import com.intellij.ui.AncestorListenerAdapter;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IdeaSettingsDialog {
    public JPanel panel;
    public JTextField taskId;
    public JTextField user;
    public JTextField project;
    public JLabel baseUrlLabel;
    public JTextField baseUrl;
    public JLabel calculatedUrl;

    private final OnChangeListener listener = new OnChangeListener(this);


    public IdeaSettingsDialog() {

        taskId.addKeyListener(listener);
        user.addKeyListener(listener);
        project.addKeyListener(listener);
        baseUrl.addKeyListener(listener);

        calculatedUrl.addAncestorListener(new AncestorListenerAdapter() {

            @Override
            public void ancestorAdded(AncestorEvent event) {
                listener.calculateUrl();
            }
        });
    }

    public IdeaSettingsDialog(Task data) {

        this();

        load(data);
    }

    void load(Task data) {

        taskId.setText(data.getTaskId());
        user.setText(data.getUser());
        project.setText(data.getProject());
        baseUrl.setText(data.getBaseUrl());
        calculatedUrl.setText(data.getCalculatedUrl());
    }

    Task toTask() {

        Task data = new Task();

        data.setTaskId(taskId.getText());
        data.setUser(user.getText());
        data.setProject(project.getText());
        data.setBaseUrl(baseUrl.getText());
        data.setCalculatedUrl(calculatedUrl.getText());

        return data;
    }


    public boolean isValid() {

        return !isEmpty(taskId.getText()) && !isEmpty(user.getText()) && !isEmpty(project.getText());
    }

    private boolean isEmpty(String foo) {
        return foo == null || foo.isEmpty();
    }

    private boolean isEqual(String a, String b) {
        return a == null && a == b ? true : //both null
                a == null || b == null ? false : //only one null
                a.trim().equals(b.trim()); //neither null - use string comparison
    }

    boolean isDifferent(Task data) {

        return taskId == null || ! isEqual(data.getTaskId(), taskId.getText()) ||
                user == null || ! isEqual(data.getUser(), user.getText()) ||
                project == null || ! isEqual(data.getProject(), project.getText()) ||
                baseUrl == null || ! isEqual(data.getBaseUrl(), baseUrl.getText());
    }


    static class OnChangeListener extends KeyAdapter {

        private IdeaSettingsDialog dialog;

        public OnChangeListener(IdeaSettingsDialog dialog) {
            this.dialog = dialog;
        }

        private String label(JTextField field, String defaultValue) {
            return field == null || field.getText() == null || field.getText().trim().isEmpty() ?
                    defaultValue : field.getText();
        }

        public void calculateUrl() {
            IdeaSettingsDialog that = dialog;

            //http://localhost:8989/<project>/<user>/<taskId>
            String base = label(that.baseUrl, "http://localhost:8989/");
            String project = label(that.project, "<project>");
            String user = label(that.user, "<user>");
            String taskId = label(that.taskId, "<taskId>");

            that.calculatedUrl.setText(base + "/" + project + "/" + user + "/" + taskId);
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            calculateUrl();
        }
    }
}
