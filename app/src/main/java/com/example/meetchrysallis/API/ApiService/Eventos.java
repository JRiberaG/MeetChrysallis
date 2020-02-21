package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Evento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Eventos {
    @GET("api/Eventos")
    Call<List<Evento>> getEventos();
}
