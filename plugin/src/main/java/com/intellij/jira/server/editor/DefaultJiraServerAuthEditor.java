package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.ui.editors.Editor;
import consulo.project.Project;
import consulo.ui.ex.awt.*;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;
import static java.lang.String.valueOf;

public class DefaultJiraServerAuthEditor extends JiraServerAuthEditor {

    private static final int VERTICAL_GAP = 10;

    private JRadioButton myUserAndPassRadio;
    private JRadioButton myApiTokenRadio;
    private JRadioButton myAccessTokenRadio;

    private UserAndPassCredentialsEditor myUserAndPassCredentialsEditor;
    private ApiTokenCredentialsEditor myApiTokenCredentialsEditor;
    private AccessTokenCredentialsEditor myAccessTokenCredentialsEditor;

    public DefaultJiraServerAuthEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JComponent createPanel() {
        myUserAndPassRadio = new JBRadioButton("User and pass", true);
        myApiTokenRadio = new JBRadioButton("Api token", true);
        myAccessTokenRadio = new JBRadioButton("Access token", true);

        myUserAndPassCredentialsEditor = new UserAndPassCredentialsEditor();
        JComponent myUserAndPassCredentialsPanel = myUserAndPassCredentialsEditor.createPanel();

        myApiTokenCredentialsEditor = new ApiTokenCredentialsEditor();
        JComponent myApiTokenCredentialsPanel = myApiTokenCredentialsEditor.createPanel();

        myAccessTokenCredentialsEditor = new AccessTokenCredentialsEditor();
        JComponent myAccessTokenCredentialsEditorPanel = myAccessTokenCredentialsEditor.createPanel();

        installListeners();


        return FormBuilder.createFormBuilder()
                .addVerticalGap(VERTICAL_GAP)
                .addLabeledComponent(myUrlLabel, myUrlField)
                .addVerticalGap(VERTICAL_GAP)
                .addComponent(myUserAndPassRadio)
                .addComponent(myUserAndPassCredentialsPanel)
                .addVerticalGap(VERTICAL_GAP)
                .addComponent(myApiTokenRadio)
                .addComponent(myApiTokenCredentialsPanel)
                .addVerticalGap(VERTICAL_GAP)
                .addComponent(myAccessTokenRadio)
                .addComponent(myAccessTokenCredentialsEditorPanel)
                .addVerticalGap(VERTICAL_GAP)
                .addComponent(myDefaultServerCheckbox)
                .addComponent(mySharedCheckbox)
                .addComponentToRightColumn(myTestPanel)
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        ValidationInfo validationInfo = super.validate();
        if (validationInfo == null) {
            if (myUserAndPassRadio.isSelected()) {
                return myUserAndPassCredentialsEditor.validate();
            } else if (myApiTokenRadio.isSelected()) {
                return myApiTokenCredentialsEditor.validate();
            }
            return myAccessTokenCredentialsEditor.validate();
        }

        return validationInfo;
    }


    @Override
    protected void apply() {
        String url = trim(myUrlField.getText());

        if (myUserAndPassRadio.isSelected()) {
            String username = myUserAndPassCredentialsEditor.getText();
            String password = myUserAndPassCredentialsEditor.getPassword();

            myServer.withUserAndPass(url, username, password, isSharedServer());
        } else if(myApiTokenRadio.isSelected()) {
            String email = myApiTokenCredentialsEditor.getText();
            String token = myApiTokenCredentialsEditor.getPassword();

            myServer.withApiToken(url, email, token, isSharedServer());
        } else {
            String token = myAccessTokenCredentialsEditor.getPassword();

            myServer.withAccessToken(url, token, isSharedServer());
        }

        super.apply();
    }

    public void installListeners() {
        super.installListeners();

        myUserAndPassCredentialsEditor.installListeners();
        myApiTokenCredentialsEditor.installListeners();
        myAccessTokenCredentialsEditor.installListeners();

        myUserAndPassRadio.addActionListener(event -> {
            if (myUserAndPassRadio.isSelected()) {
                myUserAndPassCredentialsEditor.enableComponents();
                myApiTokenCredentialsEditor.disableComponents();
                myAccessTokenCredentialsEditor.disableComponents();
            }
        });

        myApiTokenRadio.addActionListener(event -> {
            if (myApiTokenRadio.isSelected()) {
                myApiTokenCredentialsEditor.enableComponents();
                myAccessTokenCredentialsEditor.disableComponents();
                myUserAndPassCredentialsEditor.disableComponents();
            }
        });

        myAccessTokenRadio.addActionListener(event -> {
            if (myAccessTokenRadio.isSelected()) {
                myAccessTokenCredentialsEditor.enableComponents();
                myUserAndPassCredentialsEditor.disableComponents();
                myApiTokenCredentialsEditor.disableComponents();
            }
        });

    }

    interface CredentialsEditor extends Editor {

        String getText();

        String getPassword();

        void enableComponents();

        void disableComponents();

        void installListeners();

    }

    public class UserAndPassCredentialsEditor implements CredentialsEditor {

        private JLabel myUsernameLabel;
        private JTextField myUsernameField;

        private JLabel myPasswordLabel;
        private JPasswordField myPasswordField;

        @Override
        public JComponent createPanel() {

            myUsernameLabel = new JBLabel("Username:", 4);
            myUsernameField = new JBTextField();
            myUsernameField.setText(myServer.hasUserAndPassAuth() ? myServer.getUsername() : "");
            myUsernameField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

            myPasswordLabel = new JBLabel("Password:", 4);
            myPasswordField = new JPasswordField();
            myPasswordField.setText(myServer.hasUserAndPassAuth() ? myServer.getPassword() : "");
            myPasswordField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

            if (!myServer.hasUserAndPassAuth()) {
                disableComponents();
            }

            return FormBuilder.createFormBuilder()
                    .setHorizontalGap(5)
                    .addLabeledComponent(myUsernameLabel, myUsernameField)
                    .addLabeledComponent(myPasswordLabel, myPasswordField)
                    .getPanel();
        }

        @Override
        public @Nullable ValidationInfo validate() {
            if (isEmpty(trim(myUsernameField.getText()))) {
                return new ValidationInfo("Username is required.");
            } else if (isEmpty(trim(valueOf(myPasswordField.getPassword())))) {
                return new ValidationInfo("Password is required.");
            }

            return null;
        }

        @Override
        public String getText() {
            return trim(myUsernameField.getText());
        }

        @Override
        public String getPassword() {
            return trim(valueOf(myPasswordField.getPassword()));
        }

        @Override
        public void enableComponents() {
            myUsernameLabel.setEnabled(true);
            myUsernameField.setEnabled(true);
            myPasswordLabel.setEnabled(true);
            myPasswordField.setEnabled(true);
        }

        @Override
        public void disableComponents() {
            myUserAndPassRadio.setSelected(false);
            myUsernameLabel.setEnabled(false);
            myUsernameField.setEnabled(false);
            myPasswordLabel.setEnabled(false);
            myPasswordField.setEnabled(false);
        }

        @Override
        public void installListeners() {
            installListener(myUsernameField);
            installListener(myPasswordField);
        }
    }

    public class ApiTokenCredentialsEditor implements CredentialsEditor {

        private JLabel myEmailLabel;
        private JTextField myEmailField;

        private JLabel myApiTokenLabel;
        private JPasswordField myApiTokenField;

        @Override
        public JComponent createPanel() {
            myEmailLabel = new JBLabel("Email:", 4);
            myEmailField = new JBTextField();
            myEmailField.setText(myServer.hasApiTokenAuth() ? myServer.getUsername() : "");
            myEmailField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

            myApiTokenLabel = new JBLabel("Token:", 4);
            myApiTokenField = new JPasswordField();
            myApiTokenField.setText(myServer.hasApiTokenAuth() ? myServer.getPassword() : "");
            myApiTokenField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

            if (!myServer.hasApiTokenAuth()) {
                disableComponents();
            }

            return FormBuilder.createFormBuilder()
                    .setHorizontalGap(5)
                    .addLabeledComponent(myEmailLabel, myEmailField)
                    .addLabeledComponent(myApiTokenLabel, myApiTokenField)
                    .getPanel();
        }

        @Override
        public @Nullable ValidationInfo validate() {
            if (isEmpty(trim(myEmailField.getText()))) {
                return new ValidationInfo("Email is required.");
            } else if (isEmpty(trim(valueOf(myApiTokenField.getPassword())))) {
                return new ValidationInfo("Api token is required.");
            }

            return null;
        }


        @Override
        public String getText() {
            return trim(myEmailField.getText());
        }

        @Override
        public String getPassword() {
            return trim(valueOf(myApiTokenField.getPassword()));
        }

        @Override
        public void enableComponents() {
            myEmailLabel.setEnabled(true);
            myEmailField.setEnabled(true);
            myApiTokenLabel.setEnabled(true);
            myApiTokenField.setEnabled(true);
        }

        @Override
        public void disableComponents() {
            myApiTokenRadio.setSelected(false);
            myEmailLabel.setEnabled(false);
            myEmailField.setEnabled(false);
            myApiTokenLabel.setEnabled(false);
            myApiTokenField.setEnabled(false);
        }

        @Override
        public void installListeners() {
            installListener(myEmailField);
            installListener(myApiTokenField);
        }
    }


    public class AccessTokenCredentialsEditor implements CredentialsEditor {

        private JLabel myAccessTokenLabel;
        private JPasswordField myAccessTokenField;

        @Override
        public String getText() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPassword() {
            return trim(valueOf(myAccessTokenField.getPassword()));
        }

        @Override
        public void enableComponents() {
            myAccessTokenLabel.setEnabled(true);
            myAccessTokenField.setEnabled(true);
        }

        @Override
        public void disableComponents() {
            myAccessTokenRadio.setSelected(false);
            myAccessTokenLabel.setEnabled(false);
            myAccessTokenField.setEnabled(false);
        }

        @Override
        public void installListeners() {
            installListener(myAccessTokenField);
        }

        @Override
        public JComponent createPanel() {
            myAccessTokenLabel = new JBLabel("Token:", 4);
            myAccessTokenField = new JPasswordField();
            myAccessTokenField.setText(myServer.hasAccessTokenAuth() ? myServer.getPassword() : "");
            myAccessTokenField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

            if (!myServer.hasAccessTokenAuth()) {
                disableComponents();
            }

            return FormBuilder.createFormBuilder()
                    .setHorizontalGap(5)
                    .addLabeledComponent(myAccessTokenLabel, myAccessTokenField)
                    .getPanel();
        }

        @Override
        public @Nullable ValidationInfo validate() {
            if (isEmpty(getPassword())) {
                return new ValidationInfo("Access token is required.");
            }

            return null;
        }
    }


}
