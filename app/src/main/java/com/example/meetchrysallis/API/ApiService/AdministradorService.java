package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Administrador;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AdministradorService {
    @GET("api/Administradores")
    Call<List<Administrador>> getAdministradores();
}
