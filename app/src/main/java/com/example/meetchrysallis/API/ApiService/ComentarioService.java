package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Comentario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComentarioService {
    // SELECT
    @GET("api/Comentarios")
    Call<ArrayList<Comentario>> getComentarios();

    // SELECT
    @GET("api/Comentarios/ComentariosByEvento/{idEvento}")
    Call<ArrayList<Comentario>> getComentariosByEvento(@Path("idEvento") short idEvento);

    // SELECT
    @GET("api/Comentarios/ComentariosBySocio/{idSocio}")
    Call<ArrayList<Comentario>> getComentariosBySocio(@Path("idSocio")int idSocio);

    // INSERT
    @POST("api/Comentarios")
    Call<Comentario> insertComentario(@Body Comentario comentario);
}
