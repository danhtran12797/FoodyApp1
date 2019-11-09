package com.danhtran12797.thd.foodyapp.service;

public class APIService {
    private static String base_url="https://avapp.000webhostapp.com/foody/server/";
    //private static String base_url="http://192.168.1.11:8012/foody/server/";
    public static DataService getService(){
        return APIRetrofitClient.getClient(base_url).create(DataService.class);
    }
}
