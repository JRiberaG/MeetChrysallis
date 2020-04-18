package com.example.meetchrysallis.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meetchrysallis.Activities.DatosPersonalesActivity;
import com.example.meetchrysallis.Activities.LoginActivity;
import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.R;

import java.io.File;

public class OpcionesFragment extends Fragment {
    private Context context;

    public OpcionesFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_opciones, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Configuración de los distintos layouts
        configurarModificarDatos();
        configurarNotificaciones();
        configurarContactar();
        configurarLogout();
    }

    /**
     * Abre una nueva activity en la que el usuario podrá modificar sus datos, tales como
     * correo, contraseña, idioma, ...
     */
    private void configurarModificarDatos() {
        LinearLayout llModificarDatos = getView().findViewById(R.id.opciones_layout_modificarDatos);
        llModificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DatosPersonalesActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Abre una nueva activity en la que se podrá configurar las notificaciones
     */
    private void configurarNotificaciones() {
        LinearLayout layoutNotis = getView().findViewById(R.id.opciones_layout_notificaciones);
        layoutNotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                //  Pendiente de programar
                CustomToast.mostrarInfo(context, getLayoutInflater(), "Configurar las notificaciones");
            }
        });
    }

    /**
     * En caso de que el usuario clicke en el layout, se abrirá un Dialog en el que, poniendo
     * sus datos y un mensaje, podrá comunicarse con el equipo de desarrolladores para cualquier
     * sugerencia, duda, queja, etc.
     */
    private void configurarContactar() {
        LinearLayout llContactar = getView().findViewById(R.id.opciones_layout_contactar);
        llContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarError(context, getLayoutInflater(), "Contactar con el equipo");
            }
        });
    }

    /**
     * En caso de que el usuario clicke en el layout, cerrará sesión, enviándolo a la activity del login
     * y borrando sus credenciales
     */
    private void configurarLogout() {
        LinearLayout llLogout = getView().findViewById(R.id.opciones_layout_cerrarSesion);
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File fileCreds = new File(context.getExternalFilesDir(null).getPath() + File.separator + "cred.json");
                File fileCreds = Archivador.recuperarFicheroCreds(context);

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
}
