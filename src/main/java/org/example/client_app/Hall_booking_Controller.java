package org.example.client_app;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Hall_booking_Controller implements Initializable {
    @FXML
    DatePicker datePicker;

    @FXML
    ComboBox <String> slotComboBox;

    @FXML
    Button validateButton,Myrequest,change_password,logOut,user_info_toogle;
    @FXML
    TextArea purpose;
    @FXML Button confirmButton;

    @FXML
    VBox info_box,loading_overlay,menu,loading_screen;

    Conncetion_helper conncetion_helper = new Conncetion_helper();
    Connection_Manager connectionManager = conncetion_helper.get_instance();
    Data_base_manager_helper db_helper = new Data_base_manager_helper();
    Data_base_manager db_manager = db_helper.get_data_base_manager();

    String request,response;

    String date;
    String slot;

    String Purpose;
    String username;
    String branch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //popup
        loading_screen.setVisible(false);

        //menue toggle
        menu.setVisible(false);
        user_info_toogle.setOnAction(event -> {
           if(menu.isVisible()){
               menu.setVisible(false);
           }
           else {
               menu.setVisible(true);
           }
        });
        //dis ableing booking button if any changes in date picking or slot picking
        datePicker.setOnAction(event -> {
            confirmButton.setDisable(true);
        });
        slotComboBox.setOnAction(event -> {
            confirmButton.setDisable(true);
        });
        loading_overlay.setVisible(false);
        //basic user info
        username = Main_Booking_Application.username;
        branch = Main_Booking_Application.branch;

        confirmButton.setDisable(true);
        slotComboBox.getItems().add("slot1");
        slotComboBox.getItems().add("slot2");
        slotComboBox.getItems().add("slot3");
        slotComboBox.getItems().add("slot4");
        slotComboBox.getItems().add("slot5");
        slotComboBox.getItems().add("slot6");

        Alert warning = new Alert(Alert.AlertType.ERROR);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //validation button
        validateButton.setOnAction(event -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if(datePicker.getValue() != null) {

                LocalDate localDate = datePicker.getValue();
                this.date = localDate.format(dateTimeFormatter);
            }

            this.slot = slotComboBox.getValue();
            Purpose = purpose.getText();

            if(date != null && slot != null && !Purpose.isEmpty()) {
                // sending request
                connectionManager.push_request("get_date_info," + date + "," + slot);
                loading_screen.setVisible(true);
                Task <String> task = new Task<String>() {

                    String Response;
                    @Override
                    protected String call() throws Exception {
                        do {
                            try {

                                Thread.sleep(1500);
                                response = connectionManager.get_Response();

                            } catch (InterruptedException e) {
                                System.out.println("error while reading response");
                                throw new RuntimeException(e);
                            }
                        } while (response == null);
                        return Response;
                    }

                };
                task.setOnSucceeded(workerStateEvent -> {
                    if (response.equals("Available")) {
                        loading_screen.setVisible(false);
                        confirmButton.setDisable(false);
                        alert.setTitle("Available");
                        alert.setContentText("slot is free Please click on Confirm Booking to proceed.");
                        alert.show();
                    } else {
                        loading_screen.setVisible(false);
                        warning.setTitle("UNAVAILABLE");
                        warning.setHeaderText("Please try Another slot");
                        warning.setContentText("this slot is already Booked or may Registered by someone else before you ");
                        warning.show();
                    }
                });
                task.setOnFailed(workerStateEvent -> {
                    loading_screen.setVisible(false);
                    warning.setTitle("error at reading response");
                    warning.show();

                });
                new Thread(task).start();

                //System.out.println(response);


            }
            else{
                warning.setTitle("Some field(s) is empty");
                warning.setContentText("please select and fill all the requirement!!");
                warning.show();
            }

        });



        // register request

        confirmButton.setOnAction(event -> {
            confirmButton.setDisable(true);
            datePicker.setValue(null);
            slotComboBox.setValue(null);
            purpose.setText("");
            System.out.println("username"+username);
            System.out.println("put,"+date +","+username+","+branch+","+Purpose+","+slot);
            connectionManager.push_request("put,"+date +","+username+","+branch+","+Purpose+","+slot);
            date = null;
            Purpose = null;
            slot = null;
            Task<String> task = new Task<String>() {

                @Override
                protected String call() throws Exception {
                    loading_screen.setVisible(true);
                    do{
                        try {
                            Thread.sleep(1500);
                            response = connectionManager.get_Response();
                        } catch (InterruptedException e) {
                            System.out.println("error while reading response");
                            throw new RuntimeException(e);
                        }
                    }while (response.equals(null));
                    return response;
                }
            };

            task.setOnFailed(workerStateEvent -> {
                loading_screen.setVisible(false);
                warning.setTitle("error at booking");
                warning.show();
            });
            task.setOnSucceeded(workerStateEvent -> {
                loading_screen.setVisible(false);
                if(response.equals("NotAvailable")){
                    warning.setTitle("UNAVAILABLE");

                    warning.setContentText("Something went wrong!!");
                    warning.show();
                }

                else if(response.equals("<confirmed>")){
                    alert.setTitle("Booking Registered!!");
                    alert.setContentText("your request is sent for Confirmation to Your Respective Branch HOD.");
                    alert.show();
                }
            });
            new Thread(task).start();

        });

        // request history
        Myrequest.setOnAction(event -> {
            loading_overlay.setVisible(true);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("my_bookings.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Boolean fullscreen = stage.isFullScreen();
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setFullScreen(fullscreen);
                loading_overlay.setVisible(false);
                stage.show();

            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        // logout button
        logOut.setOnAction(event -> {

            try {
                db_manager.set_remember_me_false();
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
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

        // change_password button

        change_password.setOnAction(event -> {
            try {
                db_manager.set_remember_me_false();
                Parent root = FXMLLoader.load(getClass().getResource("user_password_change.fxml"));
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

        // showing username
        Text username = new Text(Main_Booking_Application.username);
        Text branch = new Text(Main_Booking_Application.branch);

        username.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 0%,rgb(144, 0, 250),rgb(245, 0, 250));");
       // branch.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 0%,rgb(250, 0, 225),rgb(250, 0, 0));");
        branch.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 0%,rgb(250, 0, 0),rgb(166, 0, 250));");
        info_box.getChildren().addAll(username,branch);
    }
}
