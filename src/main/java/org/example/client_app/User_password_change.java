package org.example.client_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class User_password_change implements Initializable {
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
        // change password button
        action_btn.setOnAction(event -> {
            String old_password = old_password_text.getText();
            String new_password = new_password_text.getText();

            if(!old_password.isEmpty() && !new_password.isEmpty()){
                System.out.println(Main_Booking_Application.username);
                connectionManager.push_request("password_update,set_new_pass_user,"+Main_Booking_Application.username + "," + old_password +"," + new_password);
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
                notification.setText("incorrect old password!!");
            }
            else if(response.equals("<Done>")){
                notification.setText("password updated!!!");
            } else if (response.equals("<NotDone>")) {
                notification.setText("some error occurred");
            }
        });

        // back button
        back_btn.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("hall_booking_page.fxml"));
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
