package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Evento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EventoService {
    @GET("api/Eventos")
    Call<List<Evento>> getEventos();

    @GET("api/Eventos/{id}")
    Call<Evento> getEventos(@Path("id")short id);
}
