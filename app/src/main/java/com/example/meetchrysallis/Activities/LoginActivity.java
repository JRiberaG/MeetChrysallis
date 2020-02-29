package com.example.meetchrysallis.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.JavaMailAPI;
import com.example.meetchrysallis.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

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
                                //TODO: si no se puede conectar a la base de datos mostrar el toast,
                                // si se conecta pero no se reconoce al usuario mostrar el tvError
                                CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), "Imposible conectar con la base de datos");
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
            public void onClick(final View v) {
                //Toast.makeText(getApplicationContext(),"ME OLVIDÉ!", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                final View view = getLayoutInflater().inflate(R.layout.dialog_olvide, null);
                builder.setView(view)
                        .setTitle("Escriba su correo").setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Buscar en la base de datos el email facilitado por el usuario,
                                //si lo está, enviar una nueva contraseña el email, si no lo está,
                                //informar al usuario con un toast
                                //TODO:
                                // PENDIENTE de programar:
                                //      - Buscar email en BD
                                //      - Modificar en BD la contraseña
                                EditText etEmail = view.findViewById(R.id.dialog_olvide_etEmail);
                                String email = etEmail.getText().toString();
                                String contrasenya = generarContrasenya();
                                String asunto = "MeetChrysallis - Recuperación de contraseña";
                                String mensaje = "Hola,\n\n" +
                                            "Esta es su nueva contraseña: \n"
                                            + contrasenya + "\n\n"
                                            + "Si lo desea, puede modificarla accediendo a la pestaña" +
                                            "de \"Perfil\" > \"Modificar Datos personales\". \n" +
                                            "\nMuchas gracias.";

                                //MODIFICAR: comprobar que el email existe en la bd

                                if (email.isEmpty()){//(email.equals("prueba")){
                                    //TODO: modificar estos parámetros
                                    JavaMailAPI javaMailAPI = new JavaMailAPI(getApplicationContext(), "jribgomez@gmail.com", asunto, mensaje);
                                    javaMailAPI.execute();

                                    //Esperamos 2,5 segundos para que dé tiempo a poder enviar el mensaje
                                    //para luego poder enviar un mensaje al usuario informándole del resultado
                                    try {
                                        //Esperamos 2,5 segundos para que de tiempo a poder enviar el mensaje
                                        Thread.sleep(2500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if(javaMailAPI.isResult())
                                        CustomToast.mostrarSuccess(LoginActivity.this,getLayoutInflater(),"Email enviado con éxito");
                                    else
                                        CustomToast.mostrarError(LoginActivity.this,getLayoutInflater(),"No se pudo enviar el email");
                                }
                                else{
                                    CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(),"No hay ningún socio registrado con ese email");
                                }
                                //buscar en la base de datos el correo
                                //si existe, enviar un email
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * Genera una nueva contraseña aleatoria con un tamaño de 6 carácteres
     * @return  La contraseña generada.
     */
    private String generarContrasenya() {
        String password = "";

        do {
            Random r = new Random();
            char letra = (char)(r.nextInt(26)+ 'a');

            if(Character.isAlphabetic(letra))
                password += letra;

        }while(password.length() < 7);

        return password;
    }

    private boolean conectarBaseDatos(Socio socio, boolean nuevaCredencial) {
        //TODO:
        // Si nos conectamos a comprobando datos de un socioNuevo y es válido ese socio, recoger el
        // socio al completo de la BD y asignarlo al pasado por parámetro
        // si es válido pero lo comprobamos a través del json, no asignar nada
        return true;
    }

    /**
     * Guarda (escribe) en un JSON las credenciales del usuario (email y contraseña)
     * @param fileCreds El archivo donde se escribirá
     * @param socio     Los datos del socio
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

    /**
     * Lee un fichero JSON en el que recogerá (en caso de que las haya) las credenciales del usuario.
     * @param path  El archivo a leer
     * @return      Los datos del socio. De no existir las credenciales devolverá null
     */
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

    /**
     * Vuelve a la MainActivity pasándole el socio logueado como 'Extra'.
     * @param socio     El socio logueado
     */
    private void devolverSocioLogueado(Socio socio) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("socio", socio);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
