package com.example.meetchrysallis.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    //Constante necesaria para poder parsear la fecha que se recibe de la BD
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String BASE_URL = "http://10.0.2.2:49999/";
    private static Retrofit retrofit = null;

    public static Retrofit getApi(){

        Gson gson = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
        }

        return retrofit;
    }
}
