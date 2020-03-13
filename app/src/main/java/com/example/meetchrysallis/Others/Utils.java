package com.example.meetchrysallis.Others;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Utils {

    //This is your from email
    public static final String EMAIL = "project.chrysallis@gmail.com";

    //This is your from email password
    public static final String PASSWORD = "SUwpYm63";

    public static String formateadorFechas(Timestamp fecha){
        String str = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
        return str;
    }

    public static String formatearFloat(float valoracionMedia){
        String result = new DecimalFormat("###,###.##").format(valoracionMedia);

        return result;
    }
}