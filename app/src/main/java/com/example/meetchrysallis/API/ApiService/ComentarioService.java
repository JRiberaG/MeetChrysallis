package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Comentario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ComentarioService {
    @GET("api/Comentarios")
    Call<ArrayList<Comentario>> getComentarios();

    @POST("api/Comentarios/5")
    Call<Comentario> insertComentario(@Body Comentario comentario);
}
