package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Comentario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComentarioService {
    @GET("api/Comentarios")
    Call<ArrayList<Comentario>> getComentarios();

    @GET("api/Comentarios/ComentariosByEvento/{idEvento}")
    Call<ArrayList<Comentario>> getComentariosByEvento(@Path("idEvento") short idEvento);

    @GET("api/Comentarios/ComentariosBySocio/{idSocio}")
    Call<ArrayList<Comentario>> getComentariosBySocio(@Path("idSocio")int idSocio);

    @POST("api/Comentarios")
    Call<Comentario> insertComentario(@Body Comentario comentario);
}
