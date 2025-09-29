package org.example.client_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Admin_password_change implements Initializable {
    @FXML TextField old_password_text;
    @FXML TextField new_password_text;
    @FXML
    Button action_btn;
    @FXML
    Text notification;
    @FXML Button back_btn;
    @FXML VBox inner_vbox;

    @FXML
    AnchorPane main_bg;
    String response;

    Conncetion_helper helper = new Conncetion_helper();
    Connection_Manager connectionManager = helper.get_instance();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Alert warning = new Alert(Alert.AlertType.ERROR);
        warning.setTitle("Wrong Old Password!!!");
        warning.setContentText("please enter propper old password..");
        Alert alert= new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText("password successfully changed.....");
        // change password button
        action_btn.setOnAction(event -> {
            String old_password = old_password_text.getText();
            String new_password = new_password_text.getText();
            if(!old_password.isEmpty() && !new_password.isEmpty()){
                connectionManager.push_request("password_update,set_new_pass_admin,"+Main_Booking_Application.username + "," + old_password +"," + new_password);
            }
            do {
                try {
                    Thread.sleep(1500);
                    response = connectionManager.get_Response();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }while (response.isEmpty());

            old_password_text.clear();
            new_password_text.clear();

            if(response.equals("<invalid>")){
                warning.show();
                notification.setText("incorrect old password!!");
            }
            else if(response.equals("<Done>")){
                notification.setText("password updated!!!");
                alert.show();
            } else if (response.equals("<NotDone>")) {
                warning.setTitle("Error");
                warning.setContentText("Some error has occurred please try again later...");
                notification.setText("some error occurred");
                warning.show();
            }
        });

        // back button
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



    }
}
