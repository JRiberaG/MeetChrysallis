package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Socio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SocioService {
    @GET("api/Socios")
    Call<List<Socio>> getSocios();
}
