package com.example.meetchrysallis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.ComunidadService;
import com.example.meetchrysallis.API.ApiService.SocioService;
import com.example.meetchrysallis.Models.Comunidad;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class DatosPersonalesActivity extends AppCompatActivity {

    private View viewDialog;
    private TextInputLayout inputLayout_Actual;
    private TextInputLayout inputLayout_Nuevo;
    private TextInputLayout inputLayout_Confirmar;
    private TextInputEditText inputEditText_Actual;
    private TextInputEditText inputEditText_Nuevo;
    private TextInputEditText inputEditText_Confirmar;

    private Context context = DatosPersonalesActivity.this;

    private int posicionIdioma = 0; //por defecto será 0

    private Socio socio = MainActivity.socio;

    private boolean correctoActual;
    private boolean correctoNuevo;
    private boolean correctoConfirmar;

    private SocioService socioService = Api.getApi().create(SocioService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);

        final TextView tvCorreo = findViewById(R.id.datos_personales_tvCorreo);
        final TextView tvIdioma = findViewById(R.id.datos_personales_tvIdioma);
        final TextView tvComunidades = findViewById(R.id.datos_personales_tvComunidades);

        inicializacionDatos(tvCorreo, tvIdioma, tvComunidades);

        configurarBackButton();

        modificarEmail(tvCorreo);
        modificarContrasenya();
        modificarIdioma(tvIdioma);
        modificarComunidades(tvComunidades);
    }

    /**
     * Una vez se crea la activity, se asignan valores a los textViews del correo, idioma y CCAA
     */
    private void inicializacionDatos(TextView tvCorreo, TextView tvIdioma, TextView tvComunidades) {
        actualizarTvEmail(tvCorreo);
        actualizarTvComunidades(tvComunidades);
        asignarTvIdioma(tvIdioma, MainActivity.idioma);
        asignarPosicionIdioma();
    }

    private void configurarBackButton() {
        ImageButton btnBack = findViewById(R.id.datos_personales_btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Método que permite al usuario, al clickar en el layout pertinente, modificar su email, actualizando
     * la base de datos
     * @param tvCorreo      El TextView que cambiará con el nuevo email
     */
    private void modificarEmail(final TextView tvCorreo) {
        LinearLayout llEmail = findViewById(R.id.datos_personales_layout_email);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View viewDialog = getLayoutInflater().inflate(R.layout.dialog_modificar_datos_personales, null);
                asignarIDsDialog();
                inputLayout_Actual.setHint(getResources().getString(R.string.email_actual));
                inputLayout_Nuevo.setHint(getResources().getString(R.string.email_nuevo));
                inputLayout_Confirmar.setHint(getResources().getString(R.string.email_confirmar));
                inputEditText_Actual.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputEditText_Nuevo.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputEditText_Confirmar.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                // Se asigna el máximo de carácteres permitidos a los inputs
                asignarMaxCaracteres(40);

                correctoActual = false;
                correctoNuevo = false;
                correctoConfirmar = false;

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setView(viewDialog)
                        //.setTitle("Modificar email")
                        .setNegativeButton(getResources().getString(R.string.cancelar), null)
                        .setPositiveButton(getResources().getString(R.string.modificar), null);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button btn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        btn.setBackground(getResources().getDrawable(R.drawable.button_reverse));
                        btn.setTextColor(getResources().getColor(R.color.md_text_white));

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String actual = inputEditText_Actual.getText().toString().trim();
                                final String nuevo = inputEditText_Nuevo.getText().toString().trim();
                                final String confirmar = inputEditText_Confirmar.getText().toString().trim();

                                //EMAIL ACTUAL
                                if(actual.equals(MainActivity.socio.getEmail())) {
                                    correctoActual = true;
                                    inputLayout_Actual.setErrorEnabled(false);
                                    inputLayout_Actual.setErrorIconDrawable(null);
                                }
                                else {
                                    correctoActual = false;
                                    inputLayout_Actual.setErrorEnabled(true);
                                    inputLayout_Actual.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                                    if(actual.isEmpty()){
                                        inputLayout_Actual.setError(getResources().getString(R.string.campo_invalido_vacio));
                                    }
                                    else{
                                        inputLayout_Actual.setError(getResources().getString(R.string.email_incorrecto));
                                    }
                                } // email actual

                                //EMAIL NUEVO
                                if (Patterns.EMAIL_ADDRESS.matcher(nuevo).matches()) {
                                    correctoNuevo = true;
                                    inputLayout_Nuevo.setErrorEnabled(false);
                                    inputLayout_Nuevo.setErrorIconDrawable(null);
                                }
                                else {
                                    correctoNuevo = false;
                                    inputLayout_Nuevo.setErrorEnabled(true);
                                    inputLayout_Nuevo.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                                    if(nuevo.isEmpty()){
                                        inputLayout_Nuevo.setError(getResources().getString(R.string.campo_invalido_vacio));
                                    }
                                    else{
                                        inputLayout_Nuevo.setError(getResources().getString(R.string.email_invalido));
                                    }
                                } // email nuevo

                                //EMAIL CONFIRMAR
                                if (confirmar.equals(nuevo) && !confirmar.isEmpty()) {
                                    correctoConfirmar = true;
                                    inputLayout_Confirmar.setErrorEnabled(false);
                                    inputLayout_Confirmar.setErrorIconDrawable(null);
                                }
                                else {
                                    correctoConfirmar = false;
                                    inputLayout_Confirmar.setErrorEnabled(true);
                                    inputLayout_Confirmar.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                                    if (!confirmar.isEmpty()){
                                        inputLayout_Confirmar.setError(getResources().getString(R.string.campo_confirmar_error));
                                    }
                                    else{
                                        inputLayout_Confirmar.setError(getResources().getString(R.string.campo_invalido_vacio));
                                    }
                                } //email confirmar

                                // Si los tres campos son correctos se actualiza el socio en la BD
                                if(correctoActual &&
                                        correctoNuevo &&
                                        correctoConfirmar){

                                    // Nuevo dialog 'Cargando...'
                                    DialogProgress dp = new DialogProgress(context);
                                    final AlertDialog alertDialog = dp.setProgressDialog("Actualizando base de datos...");

                                    // Llamada a la API para buscar el socio 'recortado', pues para
                                    // poder actualizarlo en la BD no se puede con el socio al 'completo', que es el que
                                    //tenemos en la MainActivity.socio
                                    Call<Socio> callSocio = socioService.getSocioByID(socio.getId(), false);
                                    callSocio.clone().enqueue(new Callback<Socio>() {
                                        @Override
                                        public void onResponse(Call<Socio> call, Response<Socio> response) {
                                            switch (response.code()){
                                                case 200:
                                                case 202:
                                                case 204:
                                                    // Recogemos el socio y le cambiamos el email al nuevo
                                                    socio = response.body();
                                                    if (socio != null) {
                                                        socio.setEmail(nuevo);
                                                        // Llamada a la API para actualizar el socio con el nuevo email en la base de datos
                                                        Call<Socio> callUpdateSocio = socioService.updateSocio(socio.getId(), socio);
                                                        callUpdateSocio.enqueue(new Callback<Socio>() {
                                                            @Override
                                                            public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                                switch(response.code()){
                                                                    case 200:
                                                                    case 201:
                                                                    case 202:
                                                                    case 203:
                                                                    case 204: //se actualizó con éxito el usuario
                                                                        CustomToast.mostrarSuccess(context, getLayoutInflater(), getResources().getString(R.string.email_actualizado));
                                                                        // Cambiamos el email del socio de la MainActivity y actualizamos el campo Email del layout
                                                                        MainActivity.socio.setEmail(nuevo);
                                                                        actualizarTvEmail(tvCorreo);
                                                                        break;
                                                                    default: //error al intentar actualizar el socio
                                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                                        break;
                                                                }
                                                                // Cerramos tanto el dialog de 'Cargando..' como el de 'Modificar Email'
                                                                alertDialog.dismiss();
                                                                dialog.dismiss();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Socio> call, Throwable t) {
                                                                alertDialog.dismiss();
                                                                CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                    }
                                                    break;
                                                default:
                                                    CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                    break;
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Socio> call, Throwable t) {
                                            CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Método que permite al usuario, al clickar en el layout pertinente, modificar su contraseña, actualizando
     * la base de datos
     */
    private void modificarContrasenya() {
        LinearLayout llContrasenya = findViewById(R.id.datos_personales_layout_contrasenya);
        llContrasenya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarIDsDialog();
                inputLayout_Actual.setHint(getResources().getString(R.string.contrasenya_actual));
                inputLayout_Nuevo.setHint(getResources().getString(R.string.contrasenya_nueva));
                inputLayout_Confirmar.setHint(getResources().getString(R.string.contrasenya_confirmar));
                inputEditText_Actual.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                inputEditText_Nuevo.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                inputEditText_Confirmar.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);

                // Se asigna el máximo de carácteres permitidos a los inputs
                asignarMaxCaracteres(16);

                correctoActual = false;
                correctoNuevo = false;
                correctoConfirmar = false;

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setView(viewDialog)
                        //.setTitle("Modificar email")
                        .setNegativeButton(getResources().getString(R.string.cancelar), null)
                        .setPositiveButton(getResources().getString(R.string.modificar), null);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button btn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        btn.setBackground(getResources().getDrawable(R.drawable.button_reverse));
                        btn.setTextColor(getResources().getColor(R.color.md_text_white));

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String actual = inputEditText_Actual.getText().toString().trim();
                                final String nuevo = inputEditText_Nuevo.getText().toString().trim();
                                final String confirmar = inputEditText_Confirmar.getText().toString().trim();

                                //CONTRASEÑA ACTUAL
                                if (Utils.encriptarString(actual).equals(MainActivity.socio.getContrasenya())){
                                    correctoActual = true;
                                    inputLayout_Actual.setErrorEnabled(false);
                                    inputLayout_Actual.setErrorIconDrawable(null);
                                }
                                else {
                                    correctoActual = false;
                                    inputLayout_Actual.setErrorEnabled(true);
                                    inputLayout_Actual.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                                    if(actual.isEmpty()){
                                        inputLayout_Actual.setError(getResources().getString(R.string.campo_invalido_vacio));
                                    }
                                    else{
                                        inputLayout_Actual.setError(getResources().getString(R.string.contrasenya_incorrecta));
                                    }
                                } // contraseña actual

                                //CONTRASEÑA NUEVA
                                Pattern password_patern = Pattern.compile("^" +
                                        "(?=.*[0-9])" +     	//al menos 1 número
                                        "(?=.*[a-z])" +     	//al menos 1 letra minúscula
                                        "(?=.*[A-Z])" +     	//al menos 1 letra mayúscula
                                        "(?=\\S+$)" +       	//sin espacios en blanco
                                        ".{6,}" +           	//al menos 6 carácteres
                                        "$");
                                // Si la contraseña nueva cumple este patrón, la damos por buena
                                if (password_patern.matcher(nuevo).matches()) {
                                    correctoNuevo = true;
                                    inputLayout_Nuevo.setErrorEnabled(false);
                                    inputLayout_Nuevo.setErrorIconDrawable(null);
                                } else {
                                    correctoNuevo = false;
                                    inputLayout_Nuevo.setErrorEnabled(true);
                                    inputLayout_Nuevo.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                                    if(nuevo.isEmpty()){
                                        inputLayout_Nuevo.setError(getResources().getString(R.string.campo_invalido_vacio));
                                    } else {
                                        inputLayout_Nuevo.setError(getResources().getString(R.string.contrasenya_invalida));
                                    }
                                } // contraseña nueva

                                //CONTRASEÑA CONFIRMAR
                                if (confirmar.equals(nuevo) && !confirmar.isEmpty()) {
                                    correctoConfirmar = true;
                                    inputLayout_Confirmar.setErrorEnabled(false);
                                    inputLayout_Confirmar.setErrorIconDrawable(null);
                                } else {
                                    correctoConfirmar = false;
                                    inputLayout_Confirmar.setErrorEnabled(true);
                                    inputLayout_Confirmar.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                                    if (!confirmar.isEmpty()){
                                        inputLayout_Confirmar.setError(getResources().getString(R.string.campo_confirmar_error));
                                    }
                                    else{
                                        inputLayout_Confirmar.setError(getResources().getString(R.string.campo_invalido_vacio));
                                    }
                                } //contraseña confirmar

                                // Si los tres campos son correctos se actualiza el socio en la BD
                                if(correctoActual &&
                                    correctoNuevo &&
                                    correctoConfirmar){
                                    // Encriptamos la contraseña
                                    final String contrasenyaEncriptada = Utils.encriptarString(nuevo);

                                    // Abrimos AlertDialog 'Cargando...'
                                    DialogProgress dp = new DialogProgress(context);
                                    final AlertDialog alertDialog = dp.setProgressDialog("Actualizando base de datos...");

                                    // Si es null significa que tenemos la versión 'recortada' del socio
                                    // y por tanto no es necesario llamar a la API y buscarlo de nuevo
                                    if (socio.getComunidades() == null){
                                        socio.setContrasenya(contrasenyaEncriptada);

                                        Call<Socio> socioCall = socioService.updateSocio(socio.getId(), socio);
                                        socioCall.clone().enqueue(new Callback<Socio>() {
                                            @Override
                                            public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                switch(response.code()){
                                                    case 200:
                                                    case 201:
                                                    case 202:
                                                    case 203:
                                                    case 204: //se actualizó con éxito el usuario
                                                        CustomToast.mostrarSuccess(context, getLayoutInflater(), getResources().getString(R.string.contrasenya_actualizada));
                                                        MainActivity.socio.setContrasenya(contrasenyaEncriptada);
                                                        break;
                                                    default:
                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                        break;
                                                }
                                                alertDialog.dismiss();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<Socio> call, Throwable t) {
                                                alertDialog.dismiss();
                                                dialog.dismiss();
                                                CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                            }
                                        });

                                    } else { // Si no es null tenemos que llamar y recoger la versión 'recortada'
                                        Call<Socio> callSocio = socioService.getSocioByID(socio.getId(), false);
                                        callSocio.clone().enqueue(new Callback<Socio>() {
                                            @Override
                                            public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                switch (response.code()){
                                                    case 200:
                                                    case 202:
                                                    case 204:
                                                        // Recogemos el socio y le cambiamos la contraseña a la nueva
                                                        socio = response.body();
                                                        if (socio != null) {
                                                            socio.setContrasenya(contrasenyaEncriptada);

                                                            // Llamada a la API para actualizar el socio con el nuevo email en la base de datos
                                                            Call<Socio> socioCall = socioService.updateSocio(socio.getId(), socio);
                                                            socioCall.clone().enqueue(new Callback<Socio>() {
                                                                @Override
                                                                public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                                    switch(response.code()){
                                                                        case 200:
                                                                        case 201:
                                                                        case 202:
                                                                        case 203:
                                                                        case 204: //se actualizó con éxito el usuario
                                                                            CustomToast.mostrarSuccess(context, getLayoutInflater(), getResources().getString(R.string.contrasenya_actualizada));
                                                                            MainActivity.socio.setContrasenya(contrasenyaEncriptada);
                                                                            break;
                                                                        default:
                                                                            CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                                            break;
                                                                    }
                                                                    alertDialog.dismiss();
                                                                    dialog.dismiss();
                                                                }

                                                                @Override
                                                                public void onFailure(Call<Socio> call, Throwable t) {
                                                                    alertDialog.dismiss();
                                                                    dialog.dismiss();
                                                                    CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                                                }
                                                            });
                                                        }
                                                        break;
                                                    default:
                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                        break;
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<Socio> call, Throwable t) {
                                                CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Método que permite al usuario, al clickar en el layout pertinente, modificar las CCAA
     * las cuales le interesan, para luego poder recibir notificaciones. Se actualiza la base de datos
     * @param tvComunidades     El TextView que cambiará con las nuevas comunidades
     */
    private void modificarComunidades(final TextView tvComunidades) {
        LinearLayout llComunidades = findViewById(R.id.datos_personales_layout_comunidades);
        llComunidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrimos AlertDialog 'Cargando...'
                DialogProgress dp = new DialogProgress(context);
                final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));

                // Llamamos a la API para recuperar todas las CCAA
                ComunidadService comunidadService = Api.getApi().create(ComunidadService.class);
                Call<ArrayList<Comunidad>> comunidadesCall = comunidadService.getComunidades();
                comunidadesCall.enqueue(new Callback<ArrayList<Comunidad>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Comunidad>> call, Response<ArrayList<Comunidad>> response) {
                        switch(response.code()){
                            case 200:
                            case 202:
                            case 204:
                                final ArrayList<Comunidad> comunidades = response.body();

                                if(comunidades != null){
                                    // Creamos el Array de Strings con todas las comunidades recogidas
                                    final String[] arrayComunidades = new String[comunidades.size()];
                                    for(int i = 0; i < comunidades.size(); i++){
                                        arrayComunidades[i] = comunidades.get(i).getNombre();
                                    }
                                    // Creamos el Array de booleanos
                                    final boolean[] arrayBooleans = new boolean[arrayComunidades.length];
                                    // Creamos el ArrayList (String) de las comunidades que el socio tiene como favoritas
                                    ArrayList<String> comunidadesSuscritas = new ArrayList<>();
                                    // Iteramos en el ArrayList de comunidades del socio y añadimos al ArrayList(String) cada
                                    //  comunidad que encontremos (sólo su nombre)
                                    for(Comunidad comunidad : MainActivity.socio.getComunidadesInteres()){
                                        comunidadesSuscritas.add(comunidad.getNombre());
                                    }
                                    // Iteramos el Array (String) de comunidades y si esa comunidad está dentro del
                                    //  ArrayList(String) de comunidadesSuscritas, marcamos el booleano correspondiente como true
                                    //  si no, como false
                                    for (int i = 0; i < arrayComunidades.length; i++){
                                        arrayBooleans[i] = comunidadesSuscritas.contains(arrayComunidades[i]);
                                    }
                                    ad.dismiss();

                                    //FIXME
                                    //  Arreglar problema con limpiar seleccion, cancelar
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(DatosPersonalesActivity.this);
                                    builder.setTitle(getResources().getString(R.string.indique_comunidades))
                                            .setMultiChoiceItems(arrayComunidades, arrayBooleans, new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int pos, boolean isChecked) {
                                                    arrayBooleans[pos] = isChecked;
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
//                                            .setNeutralButton(getResources().getString(R.string.limpiar), null)//new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    for (int i = 0; i < arrayBooleans.length; i++) {
//                                                        arrayBooleans[i] = false;
//                                                    }
//                                                }
//                                            })
                                            .setPositiveButton(getResources().getString(R.string.aceptar), null);
                                    AlertDialog dialog = builder.create();
                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(final DialogInterface dialog) {
                                            Button btn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                            btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    int ccaaSeleccionadas = 0;
                                                    for (int i = 0; i < arrayBooleans.length; i++) {
                                                        if (arrayBooleans[i]){
                                                            ccaaSeleccionadas++;
                                                        }
                                                    }

                                                    if (ccaaSeleccionadas > 0) {
                                                        // Limpiamos las CCAA interesadas que hubiese
                                                        socio.getComunidadesInteres().clear();
                                                        for (int i = 0; i < arrayBooleans.length; i++){
                                                            if (arrayBooleans[i]){
                                                                socio.getComunidadesInteres().add(comunidades.get(i));
                                                            }
                                                        }

                                                        //FIXME
                                                        //  Problema 1 - no se actualiza la tabla de comunidadesInteresadas
                                                        //  Problema 2 - no deja marcar la CCAA que tiene como comunidad del socio
                                                        //      Es decir, si la CCAA de donde es el socio es de Andalucia, si se marca
                                                        //      Andalucia como CCAA interesada, tira error 500
                                                        Call<Socio> callUpdateSocio = socioService.updateSocio(MainActivity.socio.getId(), MainActivity.socio);
                                                        callUpdateSocio.enqueue(new Callback<Socio>() {
                                                            @Override
                                                            public void onResponse(Call<Socio> call, Response<Socio> response) {
                                                                switch(response.code()) {
                                                                    case 204:
//                                                                        actualizarTvComunidades(tvComunidades);
                                                                        Toast.makeText(DatosPersonalesActivity.this, "Todo ok", Toast.LENGTH_SHORT).show();
                                                                        // pendiente --> modificar las CCAA interes del socio en el MainActivity
                                                                        //MainActivity.socio.setComunidadesInteres(socio.getComunidadesInteres());
                                                                        break;
                                                                    default:
                                                                        Toast.makeText(DatosPersonalesActivity.this, response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                }
                                                                actualizarTvComunidades(tvComunidades);
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Socio> call, Throwable t) {
                                                                Toast.makeText(DatosPersonalesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        dialog.dismiss();
                                                    } else {
                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), getResources().getString(R.string.error_ccaa_minima));
                                                    }

//                                                    // Limpiamos las CCAA interesadas que hubiese
//                                                    socio.getComunidadesInteres().clear();
//                                                    for (int i = 0; i < arrayBooleans.length; i++){
//                                                        if (arrayBooleans[i]){
//                                                            socio.getComunidadesInteres().add(comunidades.get(i));
//                                                        }
//                                                    }
//
//                                                    if  (socio.getComunidadesInteres().size() < 1) {
//                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), getResources().getString(R.string.error_ccaa_minima));
//                                                    } else {
//                                                        //FIXME
//                                                        //  Problema 1 - no se actualiza la tabla de comunidadesInteresadas
//                                                        //  Problema 2 - no deja marcar la CCAA que tiene como comunidad del socio
//                                                        //      Es decir, si la CCAA de donde es el socio es de Andalucia, si se marca
//                                                        //      Andalucia como CCAA interesada, tira error 500
//                                                        Call<Socio> callUpdateSocio = socioService.updateSocio(MainActivity.socio.getId(), MainActivity.socio);
//                                                        callUpdateSocio.enqueue(new Callback<Socio>() {
//                                                            @Override
//                                                            public void onResponse(Call<Socio> call, Response<Socio> response) {
//                                                                switch(response.code()) {
//                                                                    case 204:
//                                                                        actualizarTvComunidades(tvComunidades);
//                                                                        Toast.makeText(DatosPersonalesActivity.this, "Todo ok, se actualizó la bd", Toast.LENGTH_SHORT).show();
//                                                                        // pendiente --> modificar las CCAA interes del socio en el MainActivity
//                                                                        //MainActivity.socio.setComunidadesInteres(socio.getComunidadesInteres());
//                                                                        break;
//                                                                    default:
//                                                                        Toast.makeText(DatosPersonalesActivity.this, response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
//                                                                        break;
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void onFailure(Call<Socio> call, Throwable t) {
//                                                                Toast.makeText(DatosPersonalesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
//                                                        dialog.dismiss();
//                                                    }
                                                }
                                            });
//                                            Button btnNeutral = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
//                                            btnNeutral.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    v.
//                                                    for (int i = 0; i < arrayBooleans.length; i++) {
//                                                    }
//                                                }
//                                            });
                                        }
                                    });
                                    dialog.show();
//                                        @Override
//                                        public void onShow(final DialogInterface dialog) {
//                                            Button btn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
//                                            .setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    // Limpiamos las CCAA interesadas que hubiese
//                                                    socio.getComunidadesInteres().clear();
//                                                    for (int i = 0; i < arrayBooleans.length; i++){
//                                                        if (arrayBooleans[i]){
//                                                            socio.getComunidadesInteres().add(comunidades.get(i));
//                                                        }
//                                                    }
//
//                                                    //FIXME
//                                                    // No se puede actualizar las comunidades que le interesan al socio
//                                                    // porque no se actualizar el socio con las nuevas comunidades tal cual,
//                                                    //  se tiene que actualizar la tabla de ComunidadesInteres y no sé cómo hacerlo
//                                                    Call<Socio> callUpdateSocio = socioService.updateSocio(MainActivity.socio.getId(), MainActivity.socio);
//                                                    callUpdateSocio.enqueue(new Callback<Socio>() {
//                                                        @Override
//                                                        public void onResponse(Call<Socio> call, Response<Socio> response) {
//                                                            switch(response.code()) {
//                                                                case 204:
//                                                                    Toast.makeText(DatosPersonalesActivity.this, "Todo ok, se actualizó la bd", Toast.LENGTH_SHORT).show();
//                                                                    // pendiente --> modificar las CCAA interes del socio en el MainActivity
//                                                                    //MainActivity.socio.setComunidadesInteres(socio.getComunidadesInteres());
//                                                                    break;
//                                                                default:
//                                                                    Toast.makeText(DatosPersonalesActivity.this, response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
//                                                                    break;
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(Call<Socio> call, Throwable t) {
//                                                            Toast.makeText(DatosPersonalesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//
//                                                    //CustomToast.mostrarInfo(context, getLayoutInflater(), socio.getComunidadesInteres().toString());
//                                                }
//                                            })
//                                            .create();
//                                    builder.show();
                                }
                                break;
                            default:
                                CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                ad.dismiss();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Comunidad>> call, Throwable t) {
                        CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                    }
                });
            }
        });
    }

    /**
     * Método que permite al usuario, al clickar en el layout pertinente, modificar el idioma de la app
     * @param tvIdioma      El TextView que cambiará con el nuevo idioma seleccionado
     */
    private void modificarIdioma(final TextView tvIdioma) {
        LinearLayout llIdioma = findViewById(R.id.datos_personales_layout_idioma);
        llIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] idiomas = getResources().getStringArray(R.array.idiomas);

                final AlertDialog.Builder builder = new AlertDialog.Builder(DatosPersonalesActivity.this);
                builder.setTitle(getResources().getString(R.string.seleccione_idioma))
                        .setSingleChoiceItems(idiomas, posicionIdioma, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                posicionIdioma = i;
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String lang;
                                switch(posicionIdioma) {
                                    case 0:
                                        lang = "es";
                                        break;
                                    case 1:
                                        lang = "ca";
                                        break;
                                    default:
                                        lang = "en";
                                        break;
                                }
                                Utils.configurarIdioma(context, lang);
                                recreate();
                                asignarIdioma(tvIdioma);
                            }
                        });
                builder.create();
                builder.show();
            }
        });
    }

    /**
     * Modifica el TextView de las CCAA que el interesan al socio
     * @param tv    TextView
     */
    private void actualizarTvComunidades(TextView tv) {
        String resultado = "";
        ArrayList<Comunidad> comunidades = MainActivity.socio.getComunidadesInteres();

        for (int i = 0; i < comunidades.size(); i++){
            resultado += comunidades.get(i).getNombre();
            if (i < (comunidades.size() - 1)){
                resultado += ", ";
            }
        }

        tv.setText(resultado);
    }

    private void asignarMaxCaracteres(int num) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(num);
        inputEditText_Actual.setFilters(filterArray);
        inputEditText_Nuevo.setFilters(filterArray);
        inputEditText_Confirmar.setFilters(filterArray);
    }

    private void actualizarTvEmail(TextView tvCorreo) {
        tvCorreo.setText(MainActivity.socio.getEmail());
    }

    private void asignarIDsDialog() {
        viewDialog = getLayoutInflater().inflate(R.layout.dialog_modificar_datos_personales, null);
        inputLayout_Actual = viewDialog.findViewById(R.id.dialog_modificar_datos_layoutActual);
        inputLayout_Nuevo = viewDialog.findViewById(R.id.dialog_modificar_datos_layoutNuevo);
        inputLayout_Confirmar = viewDialog.findViewById(R.id.dialog_modificar_datos_layoutConfirmar);
        inputEditText_Actual = viewDialog.findViewById(R.id.dialog_modificar_datos_etActual);
        inputEditText_Nuevo = viewDialog.findViewById(R.id.dialog_modificar_datos_etNuevo);
        inputEditText_Confirmar = viewDialog.findViewById(R.id.dialog_modificar_datos_etConfirmar);
    }

    private void asignarIdioma(TextView tvIdioma) {
        File fileConfig = new File(getExternalFilesDir(null).getPath() + File.separator + "config.cfg");

        String idioma;
        switch(posicionIdioma) {
            case 0:
                idioma = "es";
                break;
            case 1:
                idioma = "ca";
                break;
            default:
                idioma = "en";
                break;
        }
        MainActivity.idioma = idioma;
        asignarTvIdioma(tvIdioma, idioma);
        Archivador.guardarConfig(fileConfig, idioma);
    }

    private void asignarTvIdioma(TextView tvIdioma, String idioma) {
        switch(idioma) {
            case "es":
                tvIdioma.setText(getResources().getString(R.string.castellano));
                break;
            case "ca":
                tvIdioma.setText(getResources().getString(R.string.catalan));
                break;
            default:
                tvIdioma.setText(getResources().getString(R.string.ingles));
                break;
        }
    }

    private void asignarPosicionIdioma() {
        switch (MainActivity.idioma) {
            case "es":
                posicionIdioma = 0;
                break;
            case "ca":
                posicionIdioma = 1;
                break;
            default:
                posicionIdioma = 2;
                break;
            }
    }

}
