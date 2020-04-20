package com.example.meetchrysallis.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiWorldTime {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String BASE_URL = "http://worldtimeapi.org/";
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
