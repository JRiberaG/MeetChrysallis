package com.example.meetchrysallis.Others;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

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
}