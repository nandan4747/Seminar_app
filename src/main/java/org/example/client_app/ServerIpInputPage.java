package org.example.client_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerIpInputPage implements Initializable {
    @FXML TextField ip_text;
    @FXML Button close_btn,add_ip_btn;
    Data_base_manager_helper db_helper = new Data_base_manager_helper();
    Data_base_manager db_manager = db_helper.get_data_base_manager();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        close_btn.setDisable(true);
        add_ip_btn.setOnAction(event -> {
            String ip = ip_text.getText();
            ip.replace(" ","");
            if(!ip.isEmpty()) {
                db_manager.add_ip_address(ip);
                close_btn.setDisable(false);
            }
        });

        close_btn.setOnAction(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        });
    }


}
