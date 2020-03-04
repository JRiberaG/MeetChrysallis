package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Socio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SocioService {
    @GET("api/socios")
    Call<List<Socio>> getSocios();

    @PUT("api/socios/{id}")
    Call<Socio> updateSocio(@Path("id")int id, @Body Socio socio);
}
