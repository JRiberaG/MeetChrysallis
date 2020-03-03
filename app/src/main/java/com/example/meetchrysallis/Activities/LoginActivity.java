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

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.SocioService;
import com.example.meetchrysallis.Models.Administrador;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO:
//  Programar la conexión a la base de datos
//  https://dribbble.com/shots/6787415-Meet-Up-Login-App-UI-Design
public class LoginActivity extends AppCompatActivity {

    private boolean encontrado;
    private boolean conexionBuena;
    private boolean hayCredenciales;

    private ArrayList<Socio> listaSocios = new ArrayList<>();
    private Socio socio;
    private Administrador admin;

    private File fileCreds;

    private SocioService socioService = Api.getApi().create(SocioService.class);
    private Call<List<Socio>> sociosCall = socioService.getSocios();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO:
        //  - Buscar en la bd también para los admins (ellos tambien pueden loguearse...)

        // --------- PASOS PARA EL LOGIN ---------
        //OPCIÓN A: Hay credenciales guardadas
        //  1- leer json (si existe json es que ya se logueó y se guardaron sus credenciales)
        //  2- conectarse con la base de datos para comprobar que siga dado de alta el user y/o que
        //      no se hayan cambiado esas credenciales (email o pw)
        //  3- si lo encuentra y está activo, se da el login por bueno y se volverá a la MainActivity
        //
        //OPCIÓN B: No hay credenciales guardadas
        //  1- si no se encuentra/lee el json, el socio tendrá que escribir el email y pw
        //  2- conectarse con la base de datos para comprobar que haya un usuario con ese email y pw
        //  3- si lo encuentra y está activo, las credenciales usadas para el login se guardaran
        //      en el JSON, se dará el login por bueno y se volverá a la MainActivity
        //
        //(Si no se entiende en el drive hay un FlowChart explicándolo)
        //----------------------------------------

        //este path es '/storage/emulated/0/Android/data/com.example.meetchrysallis/files/cred.json'
        fileCreds = new File(getExternalFilesDir(null).getPath() + File.separator + "cred.json");

        //final Socio[] socio = {leerJsonCredenciales(fileCreds.getPath())};
        socio = leerJsonCredenciales(fileCreds.getPath());

        //Si hay algunas credenciales guardadas (en el JSON)...
        if(socio != null){
            //Hacemos una llamada a la API
            sociosCall.clone().enqueue(new Callback<List<Socio>>() {
                @Override
                public void onResponse(Call<List<Socio>> call, Response<List<Socio>> response) {
                    switch(response.code()){
                        case 200:
                            switch(comprobarSocio(socio, response)){
                                case 1: //login ok
                                    devolverSocioLogueado(socio);
                                    break;
                                case 0: //login mal
                                    fileCreds.delete(); //borramos las credenciales (son erroneas)
                                    break;
                                default: //no hay socios en la bd
                                    CustomToast.mostrarWarning(LoginActivity.this,getLayoutInflater(), "No hay ningún socio registrado en la base de datos");
                                    break;
                            }
                            break;
                        default:
                            CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), response.code() + " - " + response.message());
                            break;
                    }
                }

                @Override
                public void onFailure(Call<List<Socio>> call, Throwable t) {
                    CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), "No se ha podido conectar con la base de datos");
                }
            });
        }

        //Si no hay ninguna credencial guardada en el JSON (o la que había es errónea),
        //el socio procederá a intentar loguearse manualmente
        Button btnAcceder = findViewById(R.id.BtnAcceder);
        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edCorreo = findViewById(R.id.EditTextCorreo);
                final EditText edPassword = findViewById(R.id.EditTextPassword);

                final String email = edCorreo.getText().toString();
                final String password = edPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()){
                    final Socio newSocio = new Socio(email, password);

                    //Se clonan las calls para poder realizar una tras otra
                    // (por ejemplo, el usuario se equivoca al loguearse +2 veces), de lo contrario
                    // petan lanzando la siguiente exception:
                    //      IllegalStateException: Already executed
                    sociosCall.clone().enqueue(new Callback<List<Socio>>(){
                        @Override
                        public void onResponse(Call<List<Socio>> call, Response<List<Socio>> response) {
                            switch(response.code()){
                                case 200:
                                    switch(comprobarSocio(newSocio, response)){
                                        case 1: //login ok
                                            guardarJsonCredenciales(fileCreds, newSocio);
                                            devolverSocioLogueado(newSocio);
                                            break;
                                        case 0: //login mal
                                            edCorreo.setText("");
                                            edPassword.setText("");
                                            edCorreo.requestFocus();
                                            CustomToast.mostrarError(LoginActivity.this,getLayoutInflater(), "Email o contrasenña incorrectos");
                                            break;
                                        default: //no hay socios en la bd
                                            CustomToast.mostrarWarning(LoginActivity.this,getLayoutInflater(), "No hay ningún socio registrado en la base de datos");
                                            break;
                                    }
                                    break;
                                default:
                                    CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), response.code() + " - " + response.message());
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Socio>> call, Throwable t) {
                            CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), "No se ha podido conectar con la base de datos");
                        }
                    });
                }
            }
        });

        TextView tvOlvide = findViewById(R.id.TextViewOlvide);
        tvOlvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
                                //      - Modificar en BD la contraseña (encriptada) (updatear socio)
                                EditText etEmail = view.findViewById(R.id.dialog_olvide_etEmail);
                                String email = etEmail.getText().toString();
                                String contrasenya = generarContrasenya();
                                String asunto = "MeetChrysallis - Recuperación de contraseña";
                                String mensaje = "Hola,\n\n" +
                                            "Esta es su nueva contraseña: \n" +
                                            contrasenya + "\n\n" +
                                            "Si lo desea, puede modificarla accediendo a la pestaña" +
                                            "de \"Perfil\" > \"Modificar Datos personales\". \n" +
                                            "\nMuchas gracias.\n\n" +
                                            "** Mensaje generado automáticamente." +
                                            "Por favor, no responda a este email.**";

                                //MODIFICAR: comprobar que el email existe en la bd
                                //algo así como if (emailExisteEnBD())
                                if (email.isEmpty()){
                                    //TODO: sustituir el javaMail que está activo ahora por el comentado
                                    //JavaMailAPI javaMailAPI = new JavaMailAPI(getApplicationContext(), email, asunto, mensaje);
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
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * Comprueba que el socio pasado por parámetro se encuentre dentro de la base de datos (y que
     * sea un socio en activo) a través del Response que también se pasa por parámetro.<br/>
     * <b>Resultados</b><br/>
     * 1 = Socio existente y activo, LOGIN CORRECTO<br/>
     * 0 = Socio inexistente o inactivo, LOGIN INCORRECTO<br/>
     * -1 = Datos recuperados null o insuficientes (lista vacía)
     * @param socio         El Socio a comprobar
     * @param response      La Response que se recupera de la API
     * @return              El resultado de la comprobación
     */
    private int comprobarSocio(Socio socio, Response response) {
        int resultado;
        listaSocios = (ArrayList<Socio>)response.body();

        if(listaSocios != null || listaSocios.size() < 1){
            Iterator ite = listaSocios.iterator();
            Socio socioIterado;

            while(ite.hasNext() && !encontrado){
                socioIterado = (Socio) ite.next();

                if(socioIterado.getEmail().equals(socio.getEmail())
                        && socioIterado.getContrasenya().equals(socio.getContrasenya())
                        && socioIterado.isActivo()){
                    encontrado = true;
                    socio = socioIterado;
                }
            }

            if(encontrado)
                resultado = 1;
            else
                resultado = 0;
        }
        else
            resultado = -1;

        return resultado;
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

    /**
     * Se conecta a la base de datos y busca el socio que se le pasa por parámetro. Si existe un
     * socio con ese email y contraseña y hemos accedido a través del login
     * (el usuario ha escrito su email y contraseñas y ha dado al botón acceder = nuevaCredencial true),
     * se recupera todos los datos de ese socio y esa información se le asigna al objeto Socio pasado
     * por parámetro. Si existe ese socio y hemos accedido a través de las credenciales
     * (flag nuevaCredencial es false), no se asigna nada.<br/><br/>
     * Posibles resultados de la conexión: <br/>
     * - Conexión buena y el socio existe       -> resultado = 1<br/>
     * - Conexión buena pero el socio no existe -> resultado = 0<br/>
     * - Conexión mala                          -> resultado = -1
     * @param socio             El socio (email y contraseña) a buscar en la base de datos
     * @param nuevaCredencial   Flag que indica si se accede a través del login o del JSON
     * @return                  El resultado de la conexión en forma de entero
     */


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
