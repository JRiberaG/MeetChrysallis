package com.example.meetchrysallis.Others;

import com.example.meetchrysallis.Models.Socio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase con métodos estáticos que nos servirá para poder leer y escribir JSON en ficheros
 */
public class JsonHelper {
    /**
     * Guarda (escribe) en un JSON las credenciales del usuario (email y contraseña)
     * @param fileCreds El archivo donde se escribirá
     * @param socio     Los datos del socio
     */
    public static void guardarJsonCredenciales(File fileCreds, Socio socio) {
        Gson gson = new GsonBuilder()
                .serializeNulls() //podrá escribir nulos (si los hay)
                .setPrettyPrinting() //lo escribirá con buen formato
                .create(); //crea el builder
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileCreds.getPath());
            gson.toJson(socio, fw);
        } catch (IOException e) {
            //error al abrir el archivo
        }
        finally{
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    //error al cerrar el arhcivo
                }
            }
        }
    }

    /**
     * Lee un fichero JSON en el que recogerá (en caso de que las haya) las credenciales del usuario.
     * @param path  El archivo a leer
     * @return      Los datos del socio. De no existir las credenciales devolverá null
     */
    public static Socio leerJsonCredenciales(String path) {
        Socio socio = null;

        Gson gson = new Gson();
        try {
            socio = gson.fromJson(new FileReader(path), Socio.class);
        } catch (FileNotFoundException e) {
            //no se encontró el archivo y por tanto, no hay credenciales registradas
        }
        return socio;
    }
}