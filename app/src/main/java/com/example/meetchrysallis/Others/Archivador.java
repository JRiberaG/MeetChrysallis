package com.example.meetchrysallis.Others;

import com.example.meetchrysallis.Models.Socio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase con métodos estáticos que servirá para leer/escribir en ficheros
 */
public class Archivador {
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

    public static void guardarConfig(File file, String idioma) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write("idioma:" + idioma);
            bw.newLine();
        } catch (IOException e) {
            //Error al abrir el archivo
            //System.out.println(e.toString());
            System.err.println(e.toString());
        } finally{
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    //Error al cerrar el archivo
                    //System.out.println(e.toString());
                    System.err.println(e.toString());
                }
            }
        }
    }

    public static String leerConfig(File file){
        String idioma = "";

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String linea = br.readLine();

            while(linea != null){
                String [] lineaSplit = linea.split(":");
                idioma = lineaSplit[1];
                linea = br.readLine();
            }

        } catch (FileNotFoundException e) {
            //No se encontró el archivo
        } catch (IOException e) {
            //Error en la lectura del archivo
            System.err.println(e.toString());
            //System.out.println(e.toString());
        } catch (Exception e){
            idioma = "";
        } finally{
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    //Problema al cerrar el archivo
                    //System.out.println(e.toString());
                    System.err.println(e.toString());
                }
            }
        }

        return idioma;
    }
}
