package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Socio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SocioService {
    // SELECT
    @GET("api/socios")
    Call<List<Socio>> getSocios();

    /**
     * Busca en la base de datos un socio dada una ID pasada por parámetro.
     * Dependiendo de si el booleano es 'true' o 'false', devolverá (en caso de encontrarlo) el socio
     * al completo (incluyendo otras tablas) o no.<br/><br/>
     * <pre>
     * Ejemplo:
     *          xxx/api/socios/3/true
     * </pre>
     * @param id        El id único para encontrar al socio
     * @param completo  Flag para indicar si se quiere recuoperar toda la información del socio o no
     * @return          El resultado de la búsqueda
     */
    @GET("api/socios/{id}/{completo}")
    Call<Socio> getSocioByID(@Path("id") int id, @Path("completo") boolean completo);

    // SELECT
    @GET("api/socios/{email}")
    Call<Socio> getSocioByEmail(@Path("email") String email);

//    @PUT("api/socios/{id}")
//    Call<Socio> updateSocio(@Path("id")int id, @Body Socio socio);

    // UPDATE
    @POST("api/socios/update/{id}")
    Call<Socio> updateSocio(@Path("id")int id, @Body Socio socio);
}
