package com.example.meetchrysallis.Others;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetchrysallis.R;

public class CustomToast {
    /**
     * Muestra un toast de color verde con un icono a la izquierda (tick). Está orientado a que sea
     * mostrado cuando algo haya ido bien.
     * @param context           Context
     * @param layoutInflater    Inflater
     * @param msj               Mensaje que se mostrará
     */
    public static void mostrarSuccess(Context context, LayoutInflater layoutInflater, String msj){
        LayoutInflater inflater = layoutInflater;

        View layout =
                inflater.inflate(R.layout.toast_success,
                        (ViewGroup)((Activity) context).findViewById(R.id.toast_success_layout));
        TextView text = layout.findViewById(R.id.toast_success_text);
        text.setText(msj);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Muestra un toast de color rojo con un icono a la izquierda (exclamación). Está orientado a que sea
     * mostrado cuando algo haya ido mal.
     * @param context           Context
     * @param layoutInflater    Inflater
     * @param msj               Mensaje que se mostrará
     */
    public static void mostrarError(Context context, LayoutInflater layoutInflater, String msj){
        LayoutInflater inflater = layoutInflater;

        View layout =
                inflater.inflate(R.layout.toast_error,
                        (ViewGroup)((Activity) context).findViewById(R.id.toast_error_layout));
        TextView text = layout.findViewById(R.id.toast_error_text);
        text.setText(msj);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Muestra un toast de color azul con un icono a la izquierda (i). Está orientado a que sea
     * mostrado cuando se quiera informar al usuario de algo.
     * @param context           Context
     * @param layoutInflater    Inflater
     * @param msj               Mensaje que se mostrará
     */
    public static void mostrarInfo(Context context, LayoutInflater layoutInflater, String msj){
        LayoutInflater inflater = layoutInflater;

        View layout =
                inflater.inflate(R.layout.toast_info,
                        (ViewGroup)((Activity) context).findViewById(R.id.toast_info_layout));
        TextView text = layout.findViewById(R.id.toast_info_text);
        text.setText(msj);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Muestra un toast de color amarillo con un icono a la izquierda (advertencia). Está orientado a que sea
     * mostrado cuando se quiera avisar al usuario.
     * @param context           Context
     * @param layoutInflater    Inflater
     * @param msj               Mensaje que se mostrará
     */
    public static void mostrarWarning(Context context, LayoutInflater layoutInflater, String msj){
        LayoutInflater inflater = layoutInflater;

        View layout =
                inflater.inflate(R.layout.toast_warning,
                        (ViewGroup)((Activity) context).findViewById(R.id.toast_warning_layout));
        TextView text = layout.findViewById(R.id.toast_warning_text);
        text.setText(msj);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
