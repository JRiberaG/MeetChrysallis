package com.example.meetchrysallis.API.ApiService;

import com.example.meetchrysallis.Models.Time;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TimeService {
    @GET("api/timezone/europe/madrid")
    Call<Time> getTime();
}

//  CÓMO USARLO
//       TimeService timeService = ApiWorldTime.getApi().create(TimeService.class);
//       Call<Time> timeCall = timeService.getTime();
//       timeCall.enqueue(new Callback<Time>() {
//           @Override
//           public void onResponse(Call<Time> call, Response<Time> response) {
//               if (response.code() == 200) {
//                   Time tiempo = response.body();
//                   Timestamp ts = tiempo.getDateTime();
//                   // Hacer lo que se quiera con el ts recogido
//               } else {
//                   //salió mal
//               }
//          }
//           @Override
//           public void onFailure(Call<Time> call, Throwable t) {
//               // salió mal
//           }
//       });