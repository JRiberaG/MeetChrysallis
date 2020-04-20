package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Evento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventoService {
    // SELECT
    @GET("api/Eventos")
    Call<List<Evento>> getEventos();

    // SELECT
    @GET("api/Eventos/{id}")
    Call<Evento> getEventos(@Path("id")short id);

    // UPDATE
    @POST("api/Eventos/update/{id}")
    Call<Evento> updateEvento(@Path("id")short id, @Body Evento evento);
}
