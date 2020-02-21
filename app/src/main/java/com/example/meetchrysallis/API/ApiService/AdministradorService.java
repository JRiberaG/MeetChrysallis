package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Administrador;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AdministradorService {
    @GET("api/Administradores")
    Call<List<Administrador>> getAdministradores();
    //min 25
    //Para 'llamar' al GET (hacer el SELECT), basta con dónde queramos del proyecto (al clickar botón,
    //abrir activity, etc. escribir lo siguiente:
    //  AdministradorService adminService = Api.getApi().create(AdministradorService.class);
    //  Call<List<Administrador>> listCall = adminService.getAdministradores();
    //  listCall.enqueue(new Callback<List<Administradores>>){...

}
