package org.example.client_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Admin_login_Controller implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private RadioButton bcaRadio;
    @FXML private RadioButton bbaRadio;
    @FXML private RadioButton bcomRadio;
    @FXML private Button loginButton;
    @FXML private AnchorPane main_large_container;
    @FXML private VBox inner_main_container;
    @FXML private Text warning_text;
    @FXML private StackPane stack_pane;
    @FXML private Button staff_login_switch;
    private ToggleGroup departmentGroup;

    private Data_base_manager_helper helper = new Data_base_manager_helper();
    private Data_base_manager db_manager = helper.get_data_base_manager();
    private Conncetion_helper conncetion_helper = new Conncetion_helper();
    private Connection_Manager connectionManager = conncetion_helper.get_instance();
    String response;
    //effects
    Effects_ effect = new Effects_();
    @FXML private void handleLogin(ActionEvent event) {

        warning_text.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean remember = rememberMeCheckBox.isSelected();
        String department = ((RadioButton) departmentGroup.getSelectedToggle()).getText();
        if (!username.isEmpty() && !password.isEmpty()) {

            connectionManager.push_request("_credential_," + "admin_login," + username + "," + password + ","+ department);
            do {

                try {
                    Thread.sleep(1500);
                    response = connectionManager.get_Response();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            } while (response == null);
            // switching scene
            if (response.equals("<valid>")) {
                Main_Booking_Application.username = username;
                Main_Booking_Application.branch = department;
                Main_Booking_Application.user_type = "admin";
                try {
                    if (remember) {
                        db_manager.perform_client_login(username, password, department, 1,"admin");
                    } else {
                        db_manager.perform_client_login(username, password, department, 0,"admin");
                    }
                    Parent root = FXMLLoader.load(getClass().getResource("Main_Admin_page.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Boolean fullscreen = stage.isFullScreen();
                    Scene scene = new Scene(root,stage.getWidth(),stage.getHeight());
                    stage.setScene(scene);
                    stage.setFullScreenExitHint("");
                    stage.setFullScreen(fullscreen);
                    stage.show();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // alert
                warning_text.setText("                      incorrect username or password or branch!!!!!!!!!");

            }

        }
        else{
            warning_text.setText("                      please enter username and password!!!!!!!!!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        departmentGroup = new ToggleGroup();
        bcaRadio.setToggleGroup(departmentGroup);
        bbaRadio.setToggleGroup(departmentGroup);
        bcomRadio.setToggleGroup(departmentGroup);
        bcaRadio.setSelected(true); // Default selection
        inner_main_container.setStyle("-fx-background-color:rgb(237, 237, 237);"+
                "-fx-background-radius:16px;"+
                "-fx-border-color:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));"+
                "-fx-border-radius:16px;");
        staff_login_switch.setStyle("-fx-border-color:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));"+
                "-fx-text-fill:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));"+
                "-fx-border-radius:12px;"+
                "-fx-background-radius:12px;"+
                "-fx-background-color:white");
        usernameField.setStyle("-fx-border-color:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));"+"-fx-border-radius:8px;"+"-fx-background-radius:8px;");
        passwordField.setStyle("-fx-border-color:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));"+"-fx-border-radius:8px;"+"-fx-background-radius:8px;");
        main_large_container.setStyle("-fx-background-color:linear-gradient(to bottom left, #ffe4e6, #ccfbf1);");
        stack_pane.setStyle("-fx-background-color:transparent;");
        // staff button logic
        staff_login_switch.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Boolean fullscreen = stage.isFullScreen();
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setFullScreen(fullscreen);
                stage.show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // effects calling
        double usernameFieldX =  usernameField.getScaleX();
        double usernameFieldY = usernameField.getScaleY();
        usernameField.setOnMouseEntered(mouseEvent -> {
            effect.Hover_effect(usernameField,false,"non","non","non","non",1.2,250);
        });
        usernameField.setOnMouseExited(mouseEvent -> {
            effect.Cancel_Effect(usernameField,false,usernameFieldX,usernameFieldY,null,null,null,null,250);
        });

        double passwordX = passwordField.getScaleX();
        double passwordY = passwordField.getScaleY();

        passwordField.setOnMouseEntered(mouseEvent -> {
            effect.Hover_effect(passwordField,false,null,"non","non","non",1.2,250);
        });
        passwordField.setOnMouseExited(mouseEvent -> {
            effect.Cancel_Effect(passwordField,false,passwordX,passwordY,null,null,null,null,250);
        });

        double login_btnX = loginButton.getScaleX();
        double login_btnY = loginButton.getScaleY();

        loginButton.setOnMouseEntered(mouseEvent -> {
            effect.Hover_effect(loginButton,false,null,null,null,null,1.2,250);
        });
        loginButton.setOnMouseExited(mouseEvent -> {
            effect.Cancel_Effect(loginButton,false,login_btnX,login_btnY,null,null,null,null,250);
        });

        double staff_switchX = staff_login_switch.getScaleX();
        double staff_switchY = staff_login_switch.getScaleY();

        staff_login_switch.setOnMouseEntered(mouseEvent -> {
            effect.Hover_effect(staff_login_switch,false,null,null,null,null,1.2,250);
        });
        staff_login_switch.setOnMouseExited(mouseEvent -> {
            effect.Cancel_Effect(staff_login_switch,false,staff_switchX,staff_switchY,null,null,null,null,250);
        });
    }
}

