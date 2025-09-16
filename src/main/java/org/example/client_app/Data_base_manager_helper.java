package org.example.client_app;

public class Data_base_manager_helper {
    public static Data_base_manager instance = new Data_base_manager();

    public Data_base_manager get_data_base_manager(){
        return instance;
    }
}
