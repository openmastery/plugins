package com.ideaflow.intellij.settings;

import com.intellij.ui.AncestorListenerAdapter;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.event.*;

public class IdeaSettingsDialog {
    public JPanel panel;
    public JTextField taskId;
    public JTextField user;
    public JTextField project;
    public JLabel urlLabel;
    public JLabel urlValue;
    public JTextField urlOverrideValue;


    public IdeaSettingsDialog() {

        //OnChangeListener listener = new OnChangeListener(this);

        project.addKeyListener(new OnChangeListener(this));
        user.addKeyListener(new OnChangeListener(this));
        taskId.addKeyListener(new OnChangeListener(this));
        urlOverrideValue.addKeyListener(new OnChangeListener(this));
        urlOverrideValue.addKeyListener(new OnChangeListener(this));

        urlLabel.addAncestorListener(new AncestorListenerAdapter() {

            OnChangeListener listener = new OnChangeListener(IdeaSettingsDialog.this);

            @Override
            public void ancestorAdded(AncestorEvent event) {
                listener.calculateUrl();
            }
        });
    }

    static class OnChangeListener extends KeyAdapter {

        private IdeaSettingsDialog dialog;

        public OnChangeListener(IdeaSettingsDialog dialog) {
            this.dialog = dialog;
        }

        private String label(JTextField field, String defaultValue) {
            return field.getText() == null || field.getText().trim().isEmpty() ?
                    defaultValue : field.getText();
        }

        public void calculateUrl() {
            IdeaSettingsDialog that = dialog;

            //http://localhost:8989/<project>/<user>/<taskId>
            String base = "http://localhost:8989/";
            String project = label(that.project, "<project>");
            String user = label(that.user, "<user>");
            String taskId = label(that.taskId, "<taskId>");

            if (that.urlOverrideValue.getText() == null || that.urlOverrideValue.getText().trim().isEmpty()) {

                that.urlValue.setText(base + project + "/" + user + "/" + taskId);
            }
            else {

                that.urlValue.setText(that.urlOverrideValue.getText());
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            calculateUrl();
        }
    }
}
