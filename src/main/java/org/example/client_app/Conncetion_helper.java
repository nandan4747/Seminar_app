package org.example.client_app;

public class Conncetion_helper {
    private static final Connection_Manager instance= new Connection_Manager();
    public Connection_Manager get_instance(){
        return instance;
    }

}
