package com.example.meetchrysallis.Others;

import android.content.Context;

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
import java.util.Map;

/**
 * Clase con métodos estáticos que servirá para leer/escribir en ficheros
 */
public class Archivador {

    public static File recuperarFicheroCredenciales(Context context) {
        return new File (context.getExternalFilesDir(null).getPath() + File.separator + "cred.cfg");
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
     * Guarda (escribe) la configuración (el idioma seleccionado) del usuario en un fichero dado por parámetro.
     * @param file      El fichero donde se guardará la información
     * @param str       La información a guardar
     */
    public static void guardarConfig(File file, String str) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write("idioma:" + str);
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

    /**
     * Recupera (lee) la configuración (el idioma) del usuario de un fichero dado por parámetro
     * @param file      El fichero a leer
     * @return          La configuración
     */
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

    public static Socio leerFicheroCreds(File file) {
        Socio socio = new Socio();

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String linea = br.readLine();
            int i = 0;

            while(linea != null || i == 2){
                String [] lineaSplit = linea.split(":");
                switch(i) {
                    case 0: // linea 0 --> ID
                        socio.setId(Integer.parseInt(lineaSplit[1]));
                        break;
                    case 1: // linea 1 --> passw
                        socio.setContrasenya(lineaSplit[1]);
                        break;
                    default: // linea 2 --> email
                        socio.setEmail(lineaSplit[1]);
                        break;
                }

                linea = br.readLine();
                i++;
            }

        } catch (FileNotFoundException e) {
            //No se encontró el archivo
            socio = null;
        } catch (IOException e) {
            //Error en la lectura del archivo
            System.err.println(e.toString());
            //System.out.println(e.toString());
        } catch (Exception e){
            socio = null;
        } finally{
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    // problema al cerrar el archivo
                    System.err.println(e.toString());
                }
            }
        }

        return socio;
    }

    public static void guardarFicheroCreds(File file, Socio socio) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write("0:" + socio.getId());
            bw.newLine();
            bw.write("1:" + socio.getContrasenya());
            bw.newLine();
            bw.write("2:" + socio.getEmail());
            bw.newLine();
        } catch (IOException e) {
            //Error al abrir el archivo
            System.err.println(e.toString());
        } finally{
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    //Error al cerrar el archivo
                    System.err.println(e.toString());
                }
            }
        }
    }

    public static void guardarCredenciales(File file, Map<String, String> datos) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(file));
            // Linea índice 0 -> ID
            bw.write("0:"+ datos.get("0"));
            bw.newLine();
            // Linea índice 1 -> Password
            bw.write("1:"+ datos.get("1"));
            bw.newLine();
            // Linea índice 2 -> Email
            bw.write("2:"+ datos.get("2"));
            bw.newLine();
        } catch (IOException e) {
            //Error al abrir el archivo
            System.err.println(e.toString());
        } finally{
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    //Error al cerrar el archivo
                    System.err.println(e.toString());
                }
            }
        }
    }
}
