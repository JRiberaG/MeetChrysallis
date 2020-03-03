package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Evento;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EventoService {
    @GET("api/Eventos")
    Call<ArrayList<Evento>> getEventos();
}
