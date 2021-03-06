package com.example.meetchrysallis.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meetchrysallis.Activities.DatosPersonalesActivity;
import com.example.meetchrysallis.Activities.LoginActivity;
import com.example.meetchrysallis.Activities.MainActivity;
import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.JavaMailAPI;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class OpcionesFragment extends Fragment {

    private Context context;
    private View view;

    // Constante para el Dialog Contacto
    private static final int MIN_MSJ = 10;

    private int posicionIdioma = 0; // por defecto será 0

    public OpcionesFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_opciones, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Configuración de los distintos layouts
        configurarModificarDatos();
        configurarIdioma();
        configurarContactar();
        configurarLogout();
    }

    private void configurarIdioma() {
        LinearLayout llIdioma = view.findViewById(R.id.opciones_layout_idioma);
        asignarPosicionIdioma();
        llIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] idiomas = getResources().getStringArray(R.array.idiomas);

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

                                // Actualizamos el layout
                                try {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new OpcionesFragment(context))
                                            .commit();
                                } catch (NullPointerException e) {e.printStackTrace();}

                                // Actualizamos el idioma del menú
                                BottomNavigationView botNavBar = getActivity().findViewById(R.id.bottom_nav_bar);
                                botNavBar.getMenu().findItem(R.id.nav_home).setTitle(R.string.nav_home);
                                botNavBar.getMenu().findItem(R.id.nav_eventos).setTitle(R.string.nav_eventos);
                                botNavBar.getMenu().findItem(R.id.nav_opciones).setTitle(R.string.nav_opciones);

                                asignarIdioma();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
    }

    /**
     * Abre una nueva activity en la que el usuario podrá modificar sus datos, tales como
     * correo, contraseña, idioma, ...
     */
    private void configurarModificarDatos() {
        LinearLayout llModificarDatos = view.findViewById(R.id.opciones_layout_modificarDatos);
        llModificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DatosPersonalesActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * En caso de que el usuario clicke en el layout, se abrirá un Dialog en el que, poniendo
     * sus datos y un mensaje, podrá comunicarse con el equipo de desarrolladores para cualquier
     * sugerencia, duda, queja, etc.
     */
    private void configurarContactar() {
        LinearLayout llContactar = view.findViewById(R.id.opciones_layout_contactar);
        final AlertDialog dialogContactar = configurarAlertContacto();

        llContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogContactar.show();
            }
        });
    }

    /**
     * En caso de que el usuario clicke en el layout, cerrará sesión, enviándolo a la activity del login
     * y borrando sus credenciales
     */
    private void configurarLogout() {
        LinearLayout llLogout = view.findViewById(R.id.opciones_layout_cerrarSesion);
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File fileCreds = new File(context.getExternalFilesDir(null).getPath() + File.separator + "cred.json");
                File fileCreds = Archivador.recuperarFicheroCredenciales(context);

                try{
                    fileCreds.delete();
                    Intent intent = new Intent(context, LoginActivity.class);
                    // Añadimos esta flag para indicar que venimos desde el Logout
                    intent.putExtra("bool", true);
                    startActivity(intent);
                } catch (Exception e) {
                    //No se pudieron borrar las credenciales
                }
            }
        });
    }

    private void asignarIdioma() {
        File fileConfig = new File(context.getExternalFilesDir(null).getPath() + File.separator + "config.cfg");

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
        Archivador.guardarConfig(fileConfig, idioma);
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

    private AlertDialog configurarAlertContacto() {
        final View viewDialog = getLayoutInflater().inflate(R.layout.dialog_contactar, null);
        final TextInputLayout inputLayoutEmail    = viewDialog.findViewById(R.id.dialog_contacto_layoutEmail);
        final TextInputLayout inputLayoutMensaje  = viewDialog.findViewById(R.id.dialog_contacto_layoutMensaje);

        final TextInputEditText inputEditTextNombre   = viewDialog.findViewById(R.id.dialog_contacto_etNombre);
        final TextInputEditText inputEditTextEmail    = viewDialog.findViewById(R.id.dialog_contacto_etEmail);
        final TextInputEditText inputEditTextAsunto   = viewDialog.findViewById(R.id.dialog_contacto_etAsunto);
        final TextInputEditText inputEditTextMensaje  = viewDialog.findViewById(R.id.dialog_contacto_etMensaje);

        final Spinner spinner = viewDialog.findViewById(R.id.dialog_contacto_spinner);

        final ImageView ivError = viewDialog.findViewById(R.id.dialog_contacto_ivErrorMotivo);

        //Creamos un adaptador
        ArrayAdapter<String > spinnerAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.motivos));
        //Indicamos el tipo de DropDown
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Asignamos el adaptador
        spinner.setAdapter(spinnerAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(viewDialog)
                //.setTitle(getResources().getString(R.string.formulario_contacto))
                .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new OpcionesFragment(context))
                                    .commit();
                        } catch (NullPointerException e) {
                            System.err.println("Error NullPointerException: " + e.toString());
                        }

                        dialog.cancel();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.enviar), null);
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
                        final String strNombre = inputEditTextNombre.getText().toString().trim();
                        final String strEmail = inputEditTextEmail.getText().toString().trim();
                        final String strAsunto = inputEditTextAsunto.getText().toString().trim();
                        final String strMensaje = inputEditTextMensaje.getText().toString().trim();

                        boolean emailCorrecto = false,
                                spinnerCorrecto = false,
                                msjCorrecto = false;

                        // EMAIL
                        if (Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                            emailCorrecto = true;
                            inputLayoutEmail.setErrorEnabled(false);
                            inputLayoutEmail.setErrorIconDrawable(null);
                        } else {
                            inputLayoutEmail.setErrorEnabled(true);
                            inputLayoutEmail.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                            inputLayoutEmail.setError(getResources().getString(R.string.email_invalido));
                        }

                        // MENSAJE
                        if (!strMensaje.isEmpty() && strMensaje.length() > MIN_MSJ) {
                            msjCorrecto = true;
                            inputLayoutMensaje.setErrorEnabled(false);
                            inputLayoutMensaje.setErrorIconDrawable(null);
                        } else {
                            inputLayoutMensaje.setErrorEnabled(true);
                            inputLayoutMensaje.setErrorIconDrawable(R.drawable.ic_error_red_24dp);
                            inputLayoutMensaje.setError(getResources().getString(R.string.error_campo_minimo_caracteres));
                        }

                        // SPINNER
                        if (spinner.getSelectedItemPosition() > 0) {
                            spinnerCorrecto = true;
                            spinner.setBackground(getResources().getDrawable(R.drawable.background_spinner));
                            spinner.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.background_spinner_popup));
                            ivError.setVisibility(View.GONE);
                        } else {
                            spinner.setBackground(getResources().getDrawable(R.drawable.background_spinner_error));
                            spinner.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.background_spinner_popup_error));
                            ivError.setVisibility(View.VISIBLE);
                        }

                        if (emailCorrecto &&
                                spinnerCorrecto &&
                                msjCorrecto) {

                            String asunto = "MeetChrysallis > Form Contacto";
//                            String mensaje =
//                                            "**********************\n" +
//                                            "* ID: " + MainActivity.socio.getId() + "\n" +
//                                            "* Nombre: " + strNombre + "\n" +
//                                            "* Email: " + strEmail + "\n" +
//                                            "* Motivo: " + spinner.getSelectedItem() + "\n" +
//                                            "* Asunto: " + strAsunto + "\n" +
//                                            "**********************\n\n" +
//                                            strMensaje;
                            String mensaje =
                                    "DATOS DE CONTACTO\n" +
                                            "\tID: " + MainActivity.socio.getId() + "\n" +
                                            "\tNombre: " + strNombre + "\n" +
                                            "\tEmail: " + strEmail + "\n\n" +
                                            "DATOS DEL MENSAJE\n" +
                                            "\tMotivo: " + spinner.getSelectedItem() + "\n" +
                                            "\tAsunto: " + strAsunto + "\n\n" +
                                            strMensaje;

                            try {
                                JavaMailAPI javaMailAPI = new JavaMailAPI(context, Utils.EMAIL, asunto, mensaje);
                                javaMailAPI.execute();

                                Thread.sleep(800);

                                CustomToast.mostrarSuccess(context, getLayoutInflater(), getResources().getString(R.string.mensaje_enviado), false);

                            } catch (Exception e) {
                                System.err.println(e.toString());
                                CustomToast.mostrarError(context, getLayoutInflater(), getResources().getString(R.string.no_se_envio_mensaje), true);
                            }
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }
}
