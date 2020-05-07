package com.example.meetchrysallis.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.SocioService;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.JavaMailAPI;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//https://dribbble.com/shots/6787415-Meet-Up-Login-App-UI-Design
public class LoginActivity extends AppCompatActivity {

    private Context ctx = LoginActivity.this;

    private Socio newSocio;

    private SocioService socioService = Api.getApi().create(SocioService.class);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pedirPermisos();

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


        // En caso de que accedamos desde el logout...
        boolean sesionCerrada = false;
        try {
            sesionCerrada = getIntent().getExtras().getBoolean("bool");
        } catch(Exception e) {
            // Error al recoger el Extra
        }

        //leerFicheroCreds();
        File fileCreds = Archivador.recuperarFicheroCredenciales(ctx);
        // Lee el fichero de las creds y, en caso de haberlas, las guarda en un objeto Socio
        Socio socioCredenciales = Archivador.leerFicheroCreds(fileCreds);

        //Si hay algunas credenciales guardadas se intenta el login con ellas
        if(socioCredenciales != null){
            loguearseConCredenciales(socioCredenciales, fileCreds);
        }

        //Si no hay credenciales guardadas (o la que había es errónea), el socio procederá
        // a intentar loguearse manualmente
        configurarBotonAcceder(fileCreds, sesionCerrada);

        configurarRecuperacionContrasenya();

        configurarBotonesIdiomas();
    }

    private void loguearseConCredenciales(final Socio socioCredenciales, final File fileCreds) {
        final DialogProgress dp = new DialogProgress(ctx);
        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.iniciando_sesion));
        //Hacemos una llamada a la API (socio completo) para comprobar que esas credenciales sean válidas
        Call<Socio> socioCall = socioService.getSocioByID(socioCredenciales.getId(), true);
        socioCall.enqueue(new Callback<Socio>() {
            @Override
            public void onResponse(Call<Socio> call, Response<Socio> response) {
                switch (response.code()) {
                    case 200:
                        switch (comprobarSocio(socioCredenciales, response, true)) {
                            case 1: //login ok
                                //devolverSocioCompleto(fileCreds);
                                devolverSocioLogueado();
                                break;
                            case 0:
                            default: // socio inactivo o error --> borramos creds, son erroneas
                                fileCreds.delete();
                                CustomToast.mostrarWarning(ctx, getLayoutInflater(), "Credenciales erroneas, inicie sesión", true);
                                break;
                        }
                        break;
                    case 404: // not found --> borramos creds, son erroneas
                        fileCreds.delete();
                        break;
                    default:
                        CustomToast.mostrarInfo(ctx, getLayoutInflater(), response.code() + " - " + response.message(), true);
                        break;
                }

                ad.dismiss();
            }

            @Override
            public void onFailure(Call<Socio> call, Throwable t) {
                String fallo = t.toString();
                if (fallo.contains("failed to connect"))
                    CustomToast.mostrarInfo(ctx, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                else if (fallo.contains("timeout"))
                    CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.timeout), true);

                fileCreds.delete();

                ad.dismiss();
            }
        });
    }

    private void configurarBotonAcceder(final File fileCreds, final boolean sesionCerrada) {
        Button btnAcceder = findViewById(R.id.BtnAcceder);
        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pedirPermisos();

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
                    String contrasenyaEncriptada = Utils.encriptarString(password);
//                    newSocio = new Socio(email, contrasenyaEncriptada);
                    final Socio socioTemp = new Socio(email, contrasenyaEncriptada);


                    final DialogProgress dp = new DialogProgress(ctx);
                    final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.iniciando_sesion));
                    //Se clonan las calls para poder realizar una tras otra
                    // (por ejemplo, el usuario se equivoca al loguearse +2 veces), de lo contrario
                    // petaría lanzando la siguiente exception:
                    //      IllegalStateException: Already executed
                    Call<List<Socio>> sociosCall = socioService.getSocios();
                    sociosCall.clone().enqueue(new Callback<List<Socio>>(){
                        @Override
                        public void onResponse(Call<List<Socio>> call, Response<List<Socio>> response) {
                            switch(response.code()){
                                case 200:
                                    switch(comprobarSocio(socioTemp, response, false)){
                                        case 1: //login ok
                                            //Buscamos el socio al completo
                                            Call<Socio> socioIndividualCall = socioService.getSocioByID(newSocio.getId(), true);
                                            socioIndividualCall.clone().enqueue(new Callback<Socio>() {
                                                @Override
                                                public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                    switch(response.code()){
                                                        case 200:
                                                        case 202:
                                                        case 204:
                                                            newSocio = response.body();

                                                            // Encriptamos el email para poder guardarlo en el fichero de Creds
                                                            String emailEncriptado = Utils.encriptarString(newSocio.getEmail());

                                                            // Creamos un objeto Map, en el que almacenaremos los datos a guardar en el fichero Creds
                                                            Map<String, String> datosMap = new HashMap<>();
                                                            // Primera fila: id del socio
                                                            datosMap.put("0", String.valueOf(newSocio.getId()));
                                                            // Segunda fila: contraseña del socio (encriptada)
                                                            datosMap.put("1", newSocio.getContrasenya());
                                                            // Tercera fila: email del socio (encriptado)
                                                            datosMap.put("2", emailEncriptado);

                                                            Archivador.guardarCredenciales(fileCreds, datosMap);

                                                            if (sesionCerrada) {
                                                                Intent intent = new Intent(ctx, MainActivity.class);
                                                                intent.putExtra("socio", newSocio);
                                                                // Elimina esta activity del stack, así si desde el Main se presiona 'back',
                                                                // no se volverá a esta activity
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                            } else {
                                                                devolverSocioLogueado();
                                                            }
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Socio> call, Throwable t) {
                                                    CustomToast.mostrarInfo(ctx, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                                                }
                                            });
                                            break;
                                        case 0: //login mal
                                            edCorreo.setText("");
                                            edPassword.setText("");
                                            edCorreo.requestFocus();
                                            CustomToast.mostrarError(ctx, getLayoutInflater(), getString(R.string.loginincorrecto), false);
                                            break;
                                        default: //no hay socios en la bd
                                            CustomToast.mostrarWarning(ctx, getLayoutInflater(), getResources().getString(R.string.no_hay_socio_registrado), true);
                                            break;
                                    }
                                    break;
                                default:
                                    CustomToast.mostrarInfo(ctx, getLayoutInflater(), response.code() + " - " + response.message(), true);
                                    break;
                            }
                            ad.dismiss();
                        }
                        @Override
                        public void onFailure(Call<List<Socio>> call, Throwable t) {
                            System.err.println(t.getMessage());
                            CustomToast.mostrarInfo(ctx, getLayoutInflater(), getString(R.string.error_conexion_db), true);
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
    }

    private void pedirPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
        }
    }

    private void configurarRecuperacionContrasenya() {
        final TextView tvOlvide = findViewById(R.id.TextViewOlvide);

        final View view                             = getLayoutInflater().inflate(R.layout.dialog_olvide, null);
        final TextInputLayout inputLayoutEmail      = view.findViewById(R.id.dialog_olvide_layoutEmail);
        final TextInputEditText inputEditTextEmail  = view.findViewById(R.id.dialog_olvide_etEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(view)
                .setTitle(getResources().getString(R.string.escriba_su_email))
                .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputEditTextEmail.setText("");
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.enviar), null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btn = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Busca en la base de datos el email facilitado por el usuario,
                        //si lo está, envía una nueva contraseña al email
                        final String email = inputEditTextEmail.getText().toString();

                        if (!email.isEmpty() ) {// FIXME &&*/ Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            inputLayoutEmail.setErrorEnabled(false);
                            inputLayoutEmail.setErrorIconDrawable(null);

                            //Genera una nueva contraseña aleatorio de 6 carácteres alfabéticos
                            final String contrasenyaRandom = generarContrasenya();
                            final String asunto = getResources().getString(R.string.recuperacion_asunto);
                            final String mensaje;
                            switch (MainActivity.idioma) {
                                case "en":
                                    mensaje = "Hi!\n\n" +
                                            "This is your new password: \n" +
                                            contrasenyaRandom + "\n\n" +
                                            "If you wish, you may change it through the app, and " +
                                            "once you are logged in, " +
                                            "\"Options\" > \"Modify personal data\". \n" +
                                            "\nThanks.\n\n" +
                                            "** Message generated automatically. " +
                                            "Please, do not reply to this email. **";
                                    break;
                                case "ca":
                                    mensaje = "Hola!\n\n" +
                                            "Aquesta és la seva nova contrasenya: \n" +
                                            contrasenyaRandom + "\n\n" +
                                            "Si ho desitja, pot modificar-la accedint a l'App i, " +
                                            "un cop dins, en la pestanya d'\"Opcions\" > \"Modificar dades personals\". \n" +
                                            "\nMoltes gràcies.\n\n" +
                                            "** Missatge generat automàticament. " +
                                            "Si us plau, no respongui a aquest email. **";
                                    break;
                                default:
                                    mensaje = "¡Hola!\n\n" +
                                            "Esta es su nueva contraseña: \n" +
                                            contrasenyaRandom + "\n\n" +
                                            "Si lo desea, puede modificarla accediendo a la app y, " +
                                            "una vez dentro, en la pestaña" +
                                            "de \"Opciones\" > \"Modificar Datos personales\". \n" +
                                            "\nMuchas gracias.\n\n" +
                                            "** Mensaje generado automáticamente. " +
                                            "Por favor, no responda a este email. **";
                                    break;
                            }
                            Call<List<Socio>> sociosCall = socioService.getSocios();
                            sociosCall.clone().enqueue(new Callback<List<Socio>>() {
                                @Override
                                public void onResponse(Call<List<Socio>> call, Response<List<Socio>> response) {
                                    ArrayList<Socio> listaSocios = (ArrayList<Socio>) response.body();
                                    boolean emailEncontrado = false;

                                    if (listaSocios != null || listaSocios.size() > 0) {
                                        Iterator ite = listaSocios.iterator();
                                        Socio socioIterado = null;

                                        while (ite.hasNext() && !emailEncontrado) {
                                            socioIterado = (Socio) ite.next();

                                            if (socioIterado.getEmail().equals(email) &&
                                                    socioIterado.isActivo()) {
                                                emailEncontrado = true;
                                            }
                                        }

                                        if (emailEncontrado) {
                                            // Encripta la contraseña que hemos generado automáticamente
                                            
                                            String contrasenyaEncriptada = Utils.encriptarString(contrasenyaRandom);
                                            // String contrasenyaEncriptada = Utils.encriptarString("prueba");
                                            Socio socioUpdated = socioIterado;
                                            socioUpdated.setContrasenya(contrasenyaEncriptada);

                                            //Hace una llamada a la API modificando la contraseña del socio
                                            Call<Socio> callUpdateSocio = socioService.updateSocio(socioUpdated.getId(), socioUpdated);
                                            callUpdateSocio.enqueue(new Callback<Socio>() {
                                                @Override
                                                public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                    switch (response.code()) {
                                                        case 204:
                                                            //se actualizó el usuario
                                                            System.out.println("Socio actualizado");
                                                            break;
                                                        default:
                                                            //error al intentar actualizar el socio
                                                            System.out.println(response.code() + " - " + response.message());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Socio> call, Throwable t) {
                                                    CustomToast.mostrarInfo(ctx, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                                                }
                                            });

                                            //Enviamos el mail al usuario con la nueva contraseña
                                            //JavaMailAPI javaMailAPI = new JavaMailAPI(ctx, Utils.EMAIL, asunto, mensaje);
                                            JavaMailAPI javaMailAPI = new JavaMailAPI(ctx, email, asunto, mensaje);
                                            javaMailAPI.execute();

                                            CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.se_ha_enviado_correo), true);
                                        } else
                                            CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.no_hay_socio_registrado), true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Socio>> call, Throwable t) {
                                    CustomToast.mostrarInfo(ctx, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                                }
                            });

                            inputEditTextEmail.setText("");
                            dialog.dismiss();
                        } else {
                            inputLayoutEmail.setErrorEnabled(true);
                            inputLayoutEmail.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                            inputLayoutEmail.setError(getResources().getString(R.string.email_invalido));
                        }
                    }
                });
            }
        });

        tvOlvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.show();
            }
        });
    }

    private void configurarBotonesIdiomas() {
        final File fileConfig = new File(getExternalFilesDir(null).getPath() + File.separator + "config.cfg");
        ImageButton btnEsp = findViewById(R.id.BtnEsp);
        ImageButton btnCat = findViewById(R.id.BtnCat);
        ImageButton btnEng = findViewById(R.id.BtnEng);

        btnEsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.idioma = "es";
                actualizarIdiomaApp(fileConfig);
            }
        });

        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.idioma = "ca";
                actualizarIdiomaApp(fileConfig);
            }
        });

        btnEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.idioma = "en";
                actualizarIdiomaApp(fileConfig);
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
     * @param esSocioSimple True si solo se espera un unico resultado del response, false si se espera un ArrayList
     * @return              El resultado de la comprobación
     */
    private int comprobarSocio(Socio socio, Response response, boolean esSocioSimple) {
        int resultado = -1;

        if (esSocioSimple)  {
            Socio socioRecuperado = (Socio)response.body();

            // Si se recupera un socio válido...
            if (socioRecuperado != null) {
                // Encriptamos su email para comprobarlo con el de las credenciales
                String emailRecuperadoEncriptado = Utils.encriptarString(socioRecuperado.getEmail());

                // Si tanto el email, como la contraseña coinciden y además, el socio está marcado como activo,
                // guardamos el socioRecuperado
                if (socio.getEmail().equals(emailRecuperadoEncriptado) &&
                        socio.getContrasenya().equals(socioRecuperado.getContrasenya()) &&
                        socioRecuperado.isActivo()) {
                    resultado = 1;
                    newSocio = socioRecuperado;
                } else {
                    resultado = 0;
                }
            }
        } else {
            ArrayList<Socio> listaSocios = (ArrayList<Socio>) response.body();

            if (listaSocios != null || listaSocios.size() > 0) {
                Iterator ite = listaSocios.iterator();
                Socio socioIterado;
                boolean encontrado = false;

                while (ite.hasNext() && !encontrado) {
                    socioIterado = (Socio) ite.next();

                    if (socioIterado.getEmail().equals(socio.getEmail())
                            && socioIterado.getContrasenya().equals(socio.getContrasenya())
                            && socioIterado.isActivo()) {
                        encontrado = true;
                        newSocio = socioIterado;
                    }
                }

                if (encontrado)
                    resultado = 1;
                else
                    resultado = 0;
            }
        }

//        listaSocios = (ArrayList<Socio>)response.body();
//
//        if(listaSocios != null || listaSocios.size() < 1){
//            Iterator ite = listaSocios.iterator();
//            Socio socioIterado;
//            boolean encontrado = false;
//
//            while(ite.hasNext() && !encontrado){
//                socioIterado = (Socio) ite.next();
//
//                if(socioIterado.getEmail().equals(s.getEmail())
//                        && socioIterado.getContrasenya().equals(s.getContrasenya())
//                        && socioIterado.isActivo()) {
//                    encontrado = true;
//                    newSocio = socioIterado;
//                }
//            }
//
//            if(encontrado)
//                resultado = 1;
//            else
//                resultado = 0;
//        }
//        else
//            resultado = -1;

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
     * Vuelve a la MainActivity pasándole el socio logueado como 'Extra'.
     */
    private void devolverSocioLogueado() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("socio", newSocio);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void actualizarIdiomaApp(File fileConfig) {
        if (!MainActivity.idioma.equalsIgnoreCase("")) {
            Archivador.guardarConfig(fileConfig, MainActivity.idioma);
            Utils.configurarIdioma(ctx, MainActivity.idioma);
            recreate();
        }
    }

    @Override
    public void onBackPressed(){
        //Deshabilitamos el botón hacía atrás
    }
}
