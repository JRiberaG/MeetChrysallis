package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Asistir;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AsistirService {
    @POST("api/Asistirs")
    Call<Asistir> insertAsistir(@Body Asistir asistir);

    //modificar asistir
}
