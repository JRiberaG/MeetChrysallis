package com.example.meetchrysallis.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.R;

public class OpcionesFragment extends Fragment {
    private Context context;

    public OpcionesFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayout llModificarDatos = getView().findViewById(R.id.opciones_layout_modificarDatos);
        llModificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarSuccess(context, getLayoutInflater(), "Modificar datos");
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
                //borrar credenciales
                CustomToast.mostrarWarning(context, getLayoutInflater(), "Cerrando sesión...");
            }
        });
    }
}
