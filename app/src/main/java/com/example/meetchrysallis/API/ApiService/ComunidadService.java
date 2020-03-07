package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Comunidad;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ComunidadService {
    @GET("api/Comunidades")
    Call<ArrayList<Comunidad>> getComunidades();

    @GET("api/Comunidades/{id}")
    Call<Comunidad> getComunidad(byte id);
}
