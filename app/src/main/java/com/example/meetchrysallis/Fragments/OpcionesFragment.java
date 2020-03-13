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

        LinearLayout llModificarDatos = getView().findViewById(R.id.opciones_layout_modificarDatos);
        llModificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DatosPersonalesActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout layoutNotis = getView().findViewById(R.id.opciones_layout_notificaciones);
        layoutNotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarInfo(context, getLayoutInflater(), "Configurar las notificaciones");
            }
        });

        LinearLayout llContactar = getView().findViewById(R.id.opciones_layout_contactar);
        llContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarError(context, getLayoutInflater(), "Contactar con el equipo");
            }
        });

        LinearLayout llLogout = getView().findViewById(R.id.opciones_layout_cerrarSesion);
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileCreds = new File(context.getExternalFilesDir(null).getPath() + File.separator + "cred.json");
                try{
                    fileCreds.delete();
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);

                }
                catch(Exception e){
                    //No se pudieron borrar las credenciales
                }
            }
        });
    }
}
