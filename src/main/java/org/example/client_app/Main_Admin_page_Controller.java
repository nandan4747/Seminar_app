package org.example.client_app;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Main_Admin_page_Controller implements Initializable {
    @FXML
    ListView list_view;

    @FXML Button refresh_btn;
    @FXML Button pfp_btn;
    @FXML VBox slide_bar;
    @FXML HBox user_info_hbox;
    @FXML Button close_btn;
    @FXML Button admin_log_out_btn;
    @FXML
    AnchorPane main_container;
    @FXML Button chnage_password_btn;
    @FXML VBox action_vbox,loading_overlay;
    @FXML Button new_user_btn,menue_btn,close_action_vbox;

    Conncetion_helper helper = new Conncetion_helper();
    Connection_Manager manager = helper.get_instance();
    Data_base_manager_helper db_helper = new Data_base_manager_helper();
    Data_base_manager dataBaseManager = db_helper.get_data_base_manager();
    String response,username,branch;

    public void See_all_request(String response) {
        list_view.getItems().clear();
       // System.out.println("exe function");

        //System.out.println(response);
        if (!response.equals("[]")) {
            response = response.replace("[", "");
            response = response.replace("]", "");
            response = response.replace(",", "");

            ArrayList<String> wrapper = new ArrayList<>(Arrays.asList(response.split("\\.")));
            for (String items : wrapper) {

                ArrayList<String> sliced_item = new ArrayList<>(Arrays.asList(items.split("\\^")));
                HBox hbox = new HBox();
                hbox.setSpacing(10);
                Text t = new Text("Request id / Date : ");
                hbox.getChildren().add(t);

                for (String indivisual_item : sliced_item) {
                    Text text = new Text(indivisual_item);
                    hbox.getChildren().add(text);
                }

                Button approve_btn = new Button("Approve");
                approve_btn.setStyle("-fx-background-color:green;"+
                        "-fx-text-fill:white;");
                Button reject_btn = new Button("Reject");
                reject_btn.setStyle("-fx-background-color:red;"
                        +"-fx-text-fill:white;");

                approve_btn.setOnAction(e -> {
                    Text id = (Text) hbox.getChildren().get(1);

                    Text slot = (Text) hbox.getChildren().get(5);

                    Accept_or_reject_request("approve",id.getText(),slot.getText());
                    approve_btn.setDisable(true);
                    reject_btn.setDisable(true);
                    Text status = new Text("Approved");
                    hbox.getChildren().add(status);

                });

                reject_btn.setOnAction(e -> {
                    Text id = (Text) hbox.getChildren().get(1);
                    Text slot = (Text) hbox.getChildren().get(5);
                    Accept_or_reject_request("reject", id.getText(),slot.getText());
                    approve_btn.setDisable(true);
                    reject_btn.setDisable(true);
                    Text status = new Text("Rejected");
                    hbox.getChildren().add(status);
                });
                hbox.getChildren().addAll(approve_btn, reject_btn);
                list_view.getItems().add(hbox);

            }
        }
    }
    private void Accept_or_reject_request(String header,String id,String slot){
        manager.push_request(header+","+id+","+slot);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loading_overlay.setVisible(false);
        action_vbox.setVisible(false);
        slide_bar.setVisible(false);

        //basic info
        username = Main_Booking_Application.username;
        branch = Main_Booking_Application.branch;

        Text user_name = new Text();
        Text Branch = new Text();
        user_name.setStyle("-fx-fill:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));");
        Branch.setStyle("-fx-fill:linear-gradient(from 0% 0% to 100% 0% ,rgb(255, 27, 27),rgb(109, 0, 252));");

        // pfp button action
        pfp_btn.setOnAction(event -> {
            pfp_btn.setDisable(true);
            slide_bar.setVisible(true);

            user_name.setText(username);
            Branch.setText(branch);
            VBox inside_items_of_user_pfp = new VBox();
            inside_items_of_user_pfp.getChildren().addAll(user_name,Branch);
            user_info_hbox.getChildren().add(inside_items_of_user_pfp);
        });
        //close button inside pfp slide bar
        close_btn.setOnAction(event -> {
            slide_bar.setVisible(false);
            pfp_btn.setDisable(false);
        });
        admin_log_out_btn.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Admin_login_page.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Boolean fullscreen = stage.isFullScreen();
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setFullScreen(fullscreen);
                stage.show();

                dataBaseManager.set_remember_me_false();

            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }

        });
        refresh_btn.setOnAction(event -> {
            loading_overlay.setVisible(true);

           /* manager.push_request("Show_all_request,"+branch);
            do{
                try {
                    Thread.sleep(1500);
                    response = manager.get_Response();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }while (response == null);
           // System.out.println(response);
            See_all_request(response);
            loading_overlay.setVisible(false);*/

            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    manager.push_request("Show_all_request,"+branch);
                    String res = null;
                    do{
                        Thread.sleep(1500);
                        res =  manager.get_Response();
                    }while(res == null);
                    return res;
                }
            };
            task.setOnSucceeded(e->{
                response = task.getValue();
                See_all_request(response);
                loading_overlay.setVisible(false);
            });
            task.setOnFailed(e->{
                loading_overlay.setVisible(false);
            });
            new Thread(task).start();
        });

        //change password button
        chnage_password_btn.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Admin_password_change.fxml"));
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

        // action slide bar actions
        menue_btn.setOnAction(event -> {
            action_vbox.setVisible(true);
            menue_btn.setDisable(true);
        });
        close_action_vbox.setOnAction(event -> {
            action_vbox.setVisible(false);
            menue_btn.setDisable(false);
        });
        new_user_btn.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Credential_Distribution.fxml"));
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

        Effects_ effects = new Effects_();
        double rfrsh_btnX = refresh_btn.getScaleX();
        double rfrsh_btnY = refresh_btn.getScaleY();

        refresh_btn.setOnMouseEntered(mouseEvent -> {
            effects.Hover_effect(refresh_btn, false, null, null, null, null, 1.2, 250);
        });
        refresh_btn.setOnMouseExited(mouseEvent -> {
            effects.Cancel_Effect(refresh_btn, false, rfrsh_btnX,rfrsh_btnY, null, null, null, null, 250);
        });

        double menue_btnX = menue_btn.getScaleX();
        double menue_btnY = menue_btn.getScaleY();

        menue_btn.setOnMouseEntered(mouseEvent -> {
            effects.Hover_effect(menue_btn, false, null, null, null, null, 1.8, 250);
        });
        menue_btn.setOnMouseExited(mouseEvent -> {
            effects.Cancel_Effect(menue_btn, false,menue_btnX,menue_btnY, null, null, null, null, 250);
        });
    }
}
