package org.example.client_app;

import java.sql.*;
import java.util.ArrayList;

public class Data_base_manager {
    private Connection connection;
    private final String url = "jdbc:sqlite:Credential.db";
    private String create_table_quarry = "CREATE table IF NOT EXISTS user_login_info(login_number INTEGER PRIMARY KEY AUTOINCREMENT ,username varchar(25),password varchar(20) NOT NULL,branch varchar(10),remember_login INTEGER,user_type varchar(10));";
    public void initial_setup(){
        try {
            Statement statement = connection.createStatement();
            statement.execute(create_table_quarry);
            String quarry = "CREATE table IF NOT EXISTS server_ip_info(serial_no INTEGER PRIMARY KEY AUTOINCREMENT ,ipaddress varchar(20));";
            statement.execute(quarry);
        } catch (SQLException e) {
            System.out.println("error at initial setup");
            throw new RuntimeException(e);
        }
    }
    public void primary_setup(){
        try {
            this.connection = DriverManager.getConnection(url);
           /* PreparedStatement ps = connection.prepareStatement("drop table user_login_info;");
            ps.executeUpdate();
            System.out.println("done");*/
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void perform_client_login(String username, String password,String branch,int login_remember,String user_type){
        try {
            String quarry = "INSERT into user_login_info (username,password,branch,remember_login,user_type)values(?,?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(quarry);
            statement.setString(1,username);
            statement.setString(2,password);
            statement.setString(3,branch);
            statement.setInt(4,login_remember);
            statement.setString(5,user_type);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public int get_remember_me(){

        try {
            String quarry = "SELECT remember_login from user_login_info ORDER BY login_number DESC LIMIT 1;";
            PreparedStatement ps = connection.prepareStatement(quarry);
            ResultSet rs =  ps.executeQuery();
            if(rs.next()){
                int response = rs.getInt("remember_login");
                return response;
            }
            else{
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void set_remember_me_false(){
        String quarry = "UPDATE user_login_info set remember_login = 0;";
        try {
            PreparedStatement statement = connection.prepareStatement(quarry);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String get_user_type(){
        try {
            String quarry = "SELECT user_type from user_login_info ORDER BY login_number DESC LIMIT 1;";
            PreparedStatement ps = connection.prepareStatement(quarry);
            ResultSet rs =  ps.executeQuery();
            if(rs.next()){
                String response = rs.getString("user_type");
                return response;
            }
            else{
                return "staff";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void Get_user_info_from_client_db()throws Exception{
        String quearry = "SELECT * from user_login_info ORDER BY login_number DESC LIMIT 1;";
        ArrayList<String> list = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(quearry);
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            //System.out.println("exe function");
            Main_Booking_Application.username = rs.getString("username");
            Main_Booking_Application.branch = rs.getString("branch");

        }

    }
    public void add_ip_address(String ip){
        try {
            String quarry = "insert into server_ip_info()values(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(quarry);
            preparedStatement.setString(1,ip);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("error at inserting ip address");
            throw new RuntimeException(e);
        }
    }
    public String get_server_ip(){
        String ip = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT ipaddress from server_ip_info ORDER BY serial_no DESC LIMIT 1;");
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ip = rs.getString("ipaddress");
            }
        } catch (SQLException e) {
            System.out.println("error at getting server ip");
            throw new RuntimeException(e);
        }
        return ip;
    }

}
