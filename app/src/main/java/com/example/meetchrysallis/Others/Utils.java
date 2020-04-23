package com.example.meetchrysallis.Others;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.example.meetchrysallis.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    /**
     * Email de la cuenta de correo que se usará para enviar mensajes automatizados.
     */
    public static final String EMAIL = "project.chrysallis@gmail.com";

    /**
     * Contarseña de la cuenta de correo que se usará para enviar mensajes automatizados.
     */
    public static final String PASSWORD = "SUwpYm63";

    /**
     * Devuelve un String el cual será un fecha formateada, la cual será pasada por parámetro.
     * @param fecha El número a formatear
     * @return      El numero formateado en forma de String
     */
    public static String formateadorFechas(Timestamp fecha){
        String str = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
        return str;
    }

    /**
     * Devuelve un String el cual será un float formateado, el cual será pasado por parámetro.
     * @param num   El número a formatear
     * @return      El numero formateado en forma de String
     */
    public static String formatearFloat(float num){
        String result = new DecimalFormat("###,###.##").format(num);

        return result;
    }

    /**
     * Encripta con el sistema SHA5-512 un string que se le pasará por parámetro.
     * @param str   El string a encriptar
     * @return              El string encriptado
     */
    public static String encriptarString(String str){
        String result;
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(str.getBytes());

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

        return result.toUpperCase();
    }

    /**
     * Captura, en formato de Timestamp, el día y fecha exactas en el momento de la ejecución de la instrucción.
     * @return      El día y hora (Timestamp)
     */
    public static Timestamp capturarTimestampActual(){
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        return timestamp;
    }

    public static void configurarIdioma(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
    }

    public static void asignarImagenComunidad(Context ctx, ImageView iv, byte idComunidad) {
        switch(idComunidad){
            case 1:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_andalucia_rounded));
                break;
            case 2:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_aragon_rounded));
                break;
            case 3:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_islas_canarias_rounded));
                break;
            case 4:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_cantabria_rounded));
                break;
            case 5:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_castilla_leon_rounded));
                break;
            case 6:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_castilla_mancha_rounded));
                break;
            case 7:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_catalunya_rounded));
                break;
            case 8:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_ceuta_rounded));
                break;
            case 9:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_melilla_rounded));
                break;
            case 10:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_comunidad_madrid_rounded));
                break;
            case 11:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_navarra_rounded));
                break;
            case 12:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_comunitat_valenciana_rounded));
                break;
            case 13:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_extremadura_rounded));
                break;
            case 14:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_galicia_rounded));
                break;
            case 15:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_islas_baleares_rounded));
                break;
            case 16:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_la_rioja_rounded));
                break;
            case 17:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_pais_vasco_rounded));
                break;
            case 18:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_asturias_rounded));
                break;
            case 19:
                iv.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bandera_region_murcia_rounded));
                break;
        }
    }
}