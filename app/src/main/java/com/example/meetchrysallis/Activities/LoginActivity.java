package com.example.meetchrysallis.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//TODO:
//  Programar la conexión a la base de datos
//  https://dribbble.com/shots/6787415-Meet-Up-Login-App-UI-Design
public class LoginActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //PASOS PARA EL LOGIN
        //OPCIÓN A: Hay credenciales guardadas
        //  1- leer json (si existe json es que ya se logueó y se guardaron sus credenciales)
        //  2- conectar con la base de datos para comprobar que siga dado de alta el user y/o que
        //      no se hayan cambiado esas credenciales
        //  3- si lo encuentra y está activo, se volverá a la MainActivity
        //
        //OPCIÓN B: No hay credenciales guardadas
        //  1- si no se encuentra/lee el json, el socio escribirá email y pw
        //  2- conectar con la base de datos para comprobar que haya un usuario con ese email y pw
        //  3- si lo encuentra y está activo, ese socio se guardará en el JSON credenciales y se
        //      volverá a la MainActivity

        //este path es '/storage/emulated/0/Android/data/com.example.meetchrysallis/files/cred.json'
        final File fileCreds = new File(getExternalFilesDir(null).getPath() + File.separator + "cred.json");

        Socio socio = leerJsonCredenciales(fileCreds.getPath());

        //Si hay algún socio registrado en el JSON de credenciales...
        if(socio != null){
            //Se intenta conectar a la base de datos. Si se consigue y ese socio es válido...
            if (conectarBaseDatos(socio, false)){
                //Se vuelve a la MainActivity devolviendo el socio
                devolverSocioLogueado(socio);
            }
            else{
                //Si no se consige conectar a la BD, se muestra un Toast
                Toast.makeText(getApplicationContext(), "Imposible conectar con la base de datos",Toast.LENGTH_LONG).show();

            }
        }
        //Si no hay ninguna credencial guardada en el JSON, el socio procederá a intentar loguearse
        else{
            Button btnAcceder = findViewById(R.id.BtnAcceder);

            btnAcceder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText edCorreo = findViewById(R.id.EditTextCorreo);
                    EditText edPassword = findViewById(R.id.EditTextPassword);
                    TextView tvError = findViewById(R.id.TxtLoginIncorrecto);

                    String email = edCorreo.getText().toString();
                    String password = edPassword.getText().toString();

                    if (email != null && password != null){
                        //Pendiente: borrar sólo este if / else (el condicional), lo de dentro se queda
                        if (email.equals("prueba") && password.equals("prueba")){
                            tvError.setVisibility(View.GONE);
                            Socio newSocio = new Socio(email, password);
                            //Se intenta conectar a la base de datos. Si se consigue y ese socio es válido...
                            if(conectarBaseDatos(newSocio, true)){
                                guardarJsonCredenciales(fileCreds, newSocio);
                                devolverSocioLogueado(newSocio);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Imposible conectar con la base de datos",Toast.LENGTH_LONG).show();
                                tvError.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }

        TextView tvOlvide = findViewById(R.id.TextViewOlvide);
        tvOlvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"ME OLVIDÉ!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean conectarBaseDatos(Socio socio, boolean nuevaCredencial) {
        //pendiente:
        //si nos conectamos a comprobando datos de un socioNuevo y es válido ese socio, recoger el
        //socio al completo de la BD y asignarlo a pasado por parámetro
        //si es válido pero lo comprobamos a través del json, no asignar nada
        return true;
    }

    /**
     * Guarda (escribe) en un JSON las credenciales del usuario (email y contraseña)
     * @param fileCreds
     * @param socio
     */
    private void guardarJsonCredenciales(File fileCreds, Socio socio) {
        Gson gson = new GsonBuilder()
                                    .serializeNulls() //podrá escribir nulos (si los hay)
                                    .setPrettyPrinting() //lo escribirá con buen formato
                                    .create(); //crea el builder
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileCreds.getPath());
            gson.toJson(socio, fw);
        } catch (IOException e) {
            //error al abrir el archivo
        }
        finally{
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    //error al cerrar el arhcivo
                }
            }
        }
    }

    private Socio leerJsonCredenciales(String path) {
        Socio socio = null;

        Gson gson = new Gson();
        try {
            socio = gson.fromJson(new FileReader(path), Socio.class);
        } catch (FileNotFoundException e) {
            //no se encontró el archivo y por tanto, no hay credenciales registradas
        }
        return socio;
    }

    private void devolverSocioLogueado(Socio socio) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("socio", socio);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
