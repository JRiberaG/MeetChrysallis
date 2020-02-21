package com.example.meetchrysallis.Activities;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.meetchrysallis.Models.Administrador;
import com.example.meetchrysallis.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Administrador> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET},
                1);

        /* PRUEBA DE LA API

        AdministradorService adminService = Api.getApi().create(AdministradorService.class);

        Call<List<Administrador>> listCall = adminService.getAdministradores();

        listCall.enqueue(new Callback<List<Administrador>>() {
            @Override
            public void onResponse(Call<List<Administrador>> call, Response<List<Administrador>> response) {
                switch(response.code()){
                    case 200:
                        adminList = response.body();
                        //txt.setText(adminList.get(0).toString());
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Administrador>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                            t.getCause().toString() + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        */


    }
}
