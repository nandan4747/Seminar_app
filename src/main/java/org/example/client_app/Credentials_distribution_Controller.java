package org.example.client_app;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Credentials_distribution_Controller implements Initializable {
    @FXML Button admin_switch,staff_switch,register_btn,back_btn;
    @FXML TextField new_username,new_password;
    @FXML VBox main_vbox,inner_vbox;
    @FXML Label sliding_label;
    @FXML
    Text warning_message;
    private String header ="_credential_",operation = "new_user";

    private Conncetion_helper helper = new Conncetion_helper();
    private Connection_Manager connectionManager = helper.get_instance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //actions
        staff_switch.setDisable(true);
        staff_switch.setOnAction(event -> {
            operation = "new_user";
            TranslateTransition animation = new TranslateTransition(Duration.millis(100),sliding_label);
            animation.setByX(79.2);
            animation.setCycleCount(1);
            animation.setAutoReverse(false);
            animation.play();
          //  sliding_label.setLayoutX(112.0);
            new_username.clear();
            new_password.clear();
            staff_switch.setDisable(true);
            admin_switch.setDisable(false);
            warning_message.setText("");
        });

        // register button

        admin_switch.setOnAction(event -> {
            operation = "new_admin";
            TranslateTransition animation = new TranslateTransition(Duration.millis(100),sliding_label);
            animation.setByX(-79.2);
            animation.setCycleCount(1);
            animation.setAutoReverse(false);
            animation.play();
           // sliding_label.setLayoutX(32.8);
            new_username.clear();
            new_password.clear();
            admin_switch.setDisable(true);
            staff_switch.setDisable(false);
            warning_message.setText("");
        });
        register_btn.setOnAction(event -> {
            String username = new_username.getText();
            String password = new_password.getText();
            String branch = Main_Booking_Application.branch;
            if(!username.isEmpty() && !password.isEmpty()){
                if(operation.equals("new_user")){
                    connectionManager.push_request(header+","+operation+","+username +","+password + "," + branch);
                } else if (operation.equals("new_admin")) {
                    connectionManager.push_request(header +","+ operation + ","+ username +","+ password +","+ branch);
                }
            }
            String response;
            do{
                try{
                    Thread.sleep(1500);
                    response = connectionManager.get_Response();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }while (response.isEmpty());

            if(response.equals("<Done>")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("new user Registered successfully..");
                alert.show();
                warning_message.setText("new user Registered!!!!");
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("error");
                alert.setContentText("Some error occurred please try again...");
                alert.show();
                warning_message.setText("Some error occurred please try again... ");

            }
            new_username.setText("");
            new_password.setText("");
        });

        back_btn.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Main_Admin_page.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Boolean fullscreen = stage.isFullScreen();
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setFullScreen(fullscreen);
                stage.show();

            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        // styling
        inner_vbox.setStyle("-fx-background-color:linear-gradient(to bottom right, #57534e, #a8a29e, #e7e5e4);" +
                "-fx-border-color:white;" +
                "-fx-border-width:3px;" +
                "-fx-background-radius:12px;" +
                "-fx-border-radius:12px;");
        main_vbox.setStyle("-fx-background-color:linear-gradient(to bottom right, #57534e, #a8a29e, #e7e5e4);" +
                "-fx-border-color:white;" +
                "-fx-border-width:3px;" +
                "-fx-background-radius:12px;" +
                "-fx-border-radius:12px;");
        new_username.setStyle("-fx-background-radius:12px;" +
                "-fx-border-radius:12px;" +
                "-fx-border-width:2.3px;" +
                "-fx-border-color: linear-gradient(to left, #525252, #a3a3a3, #e5e5e5);");
        new_password.setStyle("-fx-background-radius:12px;" +
                "-fx-border-radius:12px;" +
                "-fx-border-width:2.3px;" +
                "-fx-border-color: linear-gradient(to right, #525252, #a3a3a3, #e5e5e5);");
        register_btn.setStyle("-fx-background-radius:12px;" +
                "-fx-border-radius:12px;" +
                "-fx-border-width:2.3px;" +
                "-fx-border-color:white;" +
                "-fx-background-color:rgb(135,135,135);" +
                "-fx-text-fill:white;");
    }

}
