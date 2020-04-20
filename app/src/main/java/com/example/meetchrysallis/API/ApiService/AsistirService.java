package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Asistir;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AsistirService {
    @GET("api/asistirs/{idSocio}")
    Call<ArrayList<Asistir>> getAsistirBySocio(@Path("idSocio")int idSocio);

    // INSERT
    @POST("api/Asistirs")
    Call<Asistir> insertAsistir(@Body Asistir asistir);

    // UPDATE
    @POST("api/Asistirs/update/{idSocio}/{idEvento}")
    Call<Asistir> updateAsistir(@Path("idSocio")int idSocio, @Path("idEvento")short idEvento, @Body Asistir asistir);

    // DELETE
    @POST("api/Asistirs/delete/{idSocio}/{idEvento}")
    Call<Asistir> deleteAsistir(@Path("idSocio")int idSocio, @Path("idEvento")short idEvento);
}
