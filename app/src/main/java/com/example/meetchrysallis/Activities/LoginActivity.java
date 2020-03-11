package com.example.meetchrysallis.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.SocioService;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.JavaMailAPI;
import com.example.meetchrysallis.Others.JsonHelper;
import com.example.meetchrysallis.R;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//https://dribbble.com/shots/6787415-Meet-Up-Login-App-UI-Design
public class LoginActivity extends AppCompatActivity {

    private ArrayList<Socio> listaSocios = new ArrayList<>();
    private Socio socio;
    private Socio newSocio;

    private File fileCreds;

    private SocioService socioService = Api.getApi().create(SocioService.class);
    private Call<List<Socio>> sociosCall = socioService.getSocios();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        //Intentamos leer el archivo de credenciales y pasamos los datos del archivo a un objeto Socio
        socio = JsonHelper.leerJsonCredenciales(fileCreds.getPath());

        //Si hay algunas credenciales guardadas...
        if(socio != null){



            final DialogProgress dp = new DialogProgress(LoginActivity.this);
            final AlertDialog ad = dp.setProgressDialog("Iniciando sesión...");




            //Hacemos una llamada a la API para comprobar que esas credenciales sean válidas
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

                    ad.dismiss();
                }
                @Override
                public void onFailure(Call<List<Socio>> call, Throwable t) {
                    String fallo = t.toString();
                    if (fallo.contains("failed to connect"))
                        CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), getString(R.string.error_conexion_db));
                    else
                        CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), t.toString());

                    ad.dismiss();
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

                LinearLayout linearEmail = findViewById(R.id.login_linearlayout_email);
                LinearLayout linearPW = findViewById(R.id.login_linearlayout_password);

                if (!email.isEmpty() && !password.isEmpty()){
                    linearEmail.setBackgroundResource(R.drawable.background_edittext_login);
                    linearPW.setBackgroundResource(R.drawable.background_edittext_login);
                    //Encriptamos la contraseña para poder comparar con las que hay en la BD
                    String contrasenyaEncriptada = encriptarContrasenya(password).toUpperCase();
                    newSocio = new Socio(email, contrasenyaEncriptada);


                    final DialogProgress dp = new DialogProgress(LoginActivity.this);
                    final AlertDialog ad = dp.setProgressDialog("Iniciando sesión...");
                    //Se clonan las calls para poder realizar una tras otra
                    // (por ejemplo, el usuario se equivoca al loguearse +2 veces), de lo contrario
                    // petaría lanzando la siguiente exception:
                    //      IllegalStateException: Already executed
                    sociosCall.clone().enqueue(new Callback<List<Socio>>(){
                        @Override
                        public void onResponse(Call<List<Socio>> call, Response<List<Socio>> response) {
                            switch(response.code()){
                                case 200:
                                    switch(comprobarSocio(newSocio, response)){
                                        case 1: //login ok
                                            //Buscamos el socio al completo
                                            Call<Socio> socioIndividualCall = socioService.getSocioByID(newSocio.getId());
                                            socioIndividualCall.clone().enqueue(new Callback<Socio>() {
                                                @Override
                                                public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                    switch(response.code()){
                                                        case 200:
                                                        case 202:
                                                        case 204:
                                                            newSocio = response.body();
                                                            JsonHelper.guardarJsonCredenciales(fileCreds, newSocio);
                                                            devolverSocioLogueado(newSocio);
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Socio> call, Throwable t) {
                                                    CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), getString(R.string.error_conexion_db));
                                                }
                                            });
                                            break;
                                        case 0: //login mal
                                            edCorreo.setText("");
                                            edPassword.setText("");
                                            edCorreo.requestFocus();
                                            CustomToast.mostrarError(LoginActivity.this,getLayoutInflater(), getString(R.string.loginincorrecto));
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
                            ad.dismiss();
                        }
                        @Override
                        public void onFailure(Call<List<Socio>> call, Throwable t) {
                            CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), getString(R.string.error_conexion_db));
                            ad.dismiss();
                        }
                    });
                }
                else{
                    if (email.isEmpty())
                        linearEmail.setBackgroundResource(R.drawable.background_edittext_login_empty);
                    if (password.isEmpty())
                        linearPW.setBackgroundResource(R.drawable.background_edittext_login_empty);
                    if (!email.isEmpty())
                        linearEmail.setBackgroundResource(R.drawable.background_edittext_login);
                    if (!password.isEmpty())
                        linearPW.setBackgroundResource(R.drawable.background_edittext_login);
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
                                //Busca en la base de datos el email facilitado por el usuario,
                                //si lo está, envía una nueva contraseña al email, si no lo está,
                                //informa al usuario
                                EditText etEmail = view.findViewById(R.id.dialog_olvide_etEmail);
                                final String email = etEmail.getText().toString();

                                if (!email.isEmpty()){
                                    //Genera una nueva contraseña aleatorio de 6 carácteres alfabéticos
                                    final String contrasenyaRandom = generarContrasenya();
                                    final String asunto = "MeetChrysallis - Recuperación de contraseña";
                                    final String mensaje = "¡Hola!\n\n" +
                                            "Esta es su nueva contraseña: \n" +
                                            contrasenyaRandom + "\n\n" +
                                            "Si lo desea, puede modificarla accediendo a la pestaña" +
                                            "de \"Perfil\" > \"Modificar Datos personales\". \n" +
                                            "\nMuchas gracias.\n\n" +
                                            "** Mensaje generado automáticamente. " +
                                            "Por favor, no responda a este email. **";

                                    sociosCall.clone().enqueue(new Callback<List<Socio>>() {
                                        @Override
                                        public void onResponse(Call<List<Socio>> call, Response<List<Socio>> response) {
                                            listaSocios = (ArrayList<Socio>)response.body();
                                            boolean emailEncontrado = false;

                                            if(listaSocios != null || listaSocios.size() < 1) {
                                                Iterator ite = listaSocios.iterator();
                                                Socio socioIterado = null;

                                                while (ite.hasNext() && !emailEncontrado) {
                                                    socioIterado = (Socio) ite.next();

                                                    if (socioIterado.getEmail().equals(email) &&
                                                            socioIterado.isActivo()) {
                                                        emailEncontrado = true;
                                                    }
                                                }
                                                //FIXME: modificar el mail para cuando se acaben de hacer las pruebas
                                                if (emailEncontrado){
                                                    //Encripta la contraseña generada automáticamente
                                                    String contrasenyaEncriptada = encriptarContrasenya(contrasenyaRandom).toUpperCase();
                                                    Socio socioUpdated = socioIterado;
                                                    socioUpdated.setContrasenya(contrasenyaEncriptada);

                                                    //Hace una llamada a la API modificando la contraseña del socio
                                                    Call<Socio> callUpdateSocio = socioService.updateSocio(socioUpdated.getId(), socioUpdated);
                                                    callUpdateSocio.enqueue(new Callback<Socio>() {
                                                        @Override
                                                        public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                            switch(response.code()){
                                                                case 200:
                                                                case 201:
                                                                case 202:
                                                                case 203:
                                                                case 204:
                                                                    System.out.println("Se actualizó el usuario");
                                                                    break;
                                                                default:
                                                                    System.out.println(response.code() + " - " + response.message());
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Socio> call, Throwable t) {
                                                            CustomToast.mostrarInfo(getApplicationContext(), getLayoutInflater(), getString(R.string.error_conexion_db));
                                                        }
                                                    });

                                                    //Enviamos el mail al usuario con la nueva contraseña (sin encriptar)
                                                    JavaMailAPI javaMailAPI = new JavaMailAPI(getApplicationContext(), LoginActivity.this, "jribgomez@gmail.com", asunto, mensaje);
                                                    javaMailAPI.execute();

                                                    CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(),"Se le ha enviado un correo con su nueva contraseña. En caso de no haberlo recibido, compruebe su carpeta de 'spam' o su conexión a internet");
                                                }
                                                else
                                                    CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(),"No hay ningún socio registrado con ese email");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<Socio>> call, Throwable t) {
                                            CustomToast.mostrarInfo(LoginActivity.this,getLayoutInflater(), getString(R.string.error_conexion_db));
                                        }
                                    });
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
     * @param s         El Socio a comprobar
     * @param response      La Response que se recupera de la API
     * @return              El resultado de la comprobación
     */
    private int comprobarSocio(Socio s, Response response) {
        int resultado;
        listaSocios = (ArrayList<Socio>)response.body();

        if(listaSocios != null || listaSocios.size() < 1){
            Iterator ite = listaSocios.iterator();
            Socio socioIterado;
            boolean encontrado = false;

            while(ite.hasNext() && !encontrado){
                socioIterado = (Socio) ite.next();

                if(socioIterado.getEmail().equals(s.getEmail())
                        && socioIterado.getContrasenya().equals(s.getContrasenya())
                        && socioIterado.isActivo()) {
                    encontrado = true;
                    newSocio = socioIterado;
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
     * Encripta con el sistema SHA5-512 un string que se le pasará por parámetro.
     * @param contrasenya   El string a encriptar
     * @return              El string encriptado
     */
    private String encriptarContrasenya(String contrasenya){
        String result;
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(contrasenya.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            result = hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return result;
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

    @Override
    public void onBackPressed(){
        //Deshabilitamos el botón hacía atrás
    }
}
