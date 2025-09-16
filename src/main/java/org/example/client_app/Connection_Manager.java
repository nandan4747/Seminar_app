package org.example.client_app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection_Manager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile static String Respons;
    private volatile Boolean NewResponse = false;
    Data_base_manager_helper db_helper = new Data_base_manager_helper();
    Data_base_manager db_manager = db_helper.get_data_base_manager();

    public void get_Connection(String host ,int port){
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(),true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("connected");
            Main_Booking_Application.move_forword = true;
        } catch (IOException e) {
            String ip = db_manager.get_server_ip();
            try {
                this.socket = new Socket(ip, 56000);
                this.out = new PrintWriter(socket.getOutputStream(),true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("connected");
                System.out.println("2nd");
                System.out.println(ip);
                Main_Booking_Application.move_forword = true;

            } catch (IOException ex) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("server_ip_input_page.fxml"));
                    Scene scene = new Scene(root,684,526);
                    Stage ip_stage = new Stage();
                    ip_stage.setTitle("Server IP Address");
                    ip_stage.setScene(scene);
                    ip_stage.show();
                } catch (Exception exception) {
                    System.out.println("error at connceting to server");
                    throw new RuntimeException(exception);
                }
            }
        }

    }
    public void Start_hear_response(){
        new Thread(()->{
            while(true){
                try {
                    Respons = in.readLine();
                    NewResponse = true;
                } catch (IOException e) {
                    try {
                        in.close();
                        out.close();
                        socket.close();

                    } catch (IOException ex) {
                        System.out.println("error at closing connection");
                        throw new RuntimeException(ex);
                    }
                    System.out.println("error at reading response");
                    break;
                    //throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public String get_Response(){
        if(NewResponse) {
            NewResponse = false;
            return Respons;
        }
        else{
            return null;
        }
    }
    public void push_request(String request){
        out.println(request);
    }
    public void close_connection(){
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("error at closing socket");
            throw new RuntimeException(e);
        }
    }

}
