package org.example.client_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main_Booking_Application extends Application {
    public static String username;
    public static String branch;
    public static String user_type;
    int remember_me;
    FXMLLoader fxmlLoader;
    public static Boolean move_forword = false;
    @Override
    public void start(Stage stage) throws IOException {

        // database connection
        Data_base_manager_helper db_helper = new Data_base_manager_helper();
        Data_base_manager db_manager = db_helper.get_data_base_manager();
        db_manager.primary_setup();
        // creates table if not exists
        db_manager.initial_setup();

        // creating a connection
        Conncetion_helper helper = new Conncetion_helper();
        Connection_Manager server_connection = helper.get_instance();
        server_connection.get_Connection("localhost",56000);
        if(move_forword) {
            server_connection.Start_hear_response();

            stage.setOnCloseRequest(windowEvent -> {
                server_connection.push_request("-X-X-");
                server_connection.close_connection();

            });


            remember_me = db_manager.get_remember_me();
            System.out.println("remember:" + remember_me);
            // UI
            if (remember_me == 0) {
                this.fxmlLoader = new FXMLLoader(Main_Booking_Application.class.getResource("Login.fxml"));

            } else {
                try {
                    db_manager.Get_user_info_from_client_db();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                user_type = db_manager.get_user_type();
                if (user_type.equals("staff")) {
                    this.fxmlLoader = new FXMLLoader(Main_Booking_Application.class.getResource("hall_booking_page.fxml"));
                } else {
                    this.fxmlLoader = new FXMLLoader(Main_Booking_Application.class.getResource("Main_Admin_page.fxml"));
                }
            }
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("my app");
            stage.setScene(scene);
            stage.show();
        }


    }

    public static void main(String[] args) {
        launch();
    }
}