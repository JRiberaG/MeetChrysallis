package com.example.meetchrysallis.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Socio implements Serializable {
    private int id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasenya;
    private String telefono;
    private String poblacion;
    private byte idComunidad;
    private boolean activo;
    @SerializedName("Asistir")
    private ArrayList<Asistir> asistencias;
    private Comunidad Comunidades;
    @SerializedName("Comunidades1")
    private ArrayList<Comunidad> comunidadesInteres;
    private ArrayList<Comentario> Comentario;

    //Constructores
    public Socio(){}

    public Socio(int id, String email, String contrasenya) {
        this.id = id;
        this.email = email;
        this.contrasenya = contrasenya;
    }

    public Socio(String email, String contrasenya) {
        this.email = email;
        this.contrasenya = contrasenya;
    }

    public Socio(int id, String dni, String nombre, String apellidos, String email, String contrasenya, String telefono, String poblacion, byte idComunidad, boolean activo, ArrayList<Asistir> asistencias, Comunidad comunidades, ArrayList<Comunidad> comunidadesInteres, ArrayList<com.example.meetchrysallis.Models.Comentario> comentario) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenya = contrasenya;
        this.telefono = telefono;
        this.poblacion = poblacion;
        this.idComunidad = idComunidad;
        this.activo = activo;
        this.asistencias = asistencias;
        Comunidades = comunidades;
        this.comunidadesInteres = comunidadesInteres;
        Comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public byte getIdComunidad() {
        return idComunidad;
    }

    public void setIdComunidad(byte idComunidad) {
        this.idComunidad = idComunidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public ArrayList<Asistir> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(ArrayList<Asistir> asistencias) {
        this.asistencias = asistencias;
    }

    public Comunidad getComunidades() {
        return Comunidades;
    }

    public void setComunidades(Comunidad comunidades) {
        Comunidades = comunidades;
    }

    public ArrayList<Comunidad> getComunidadesInteres() {
        return comunidadesInteres;
    }

    public void setComunidadesInteres(ArrayList<Comunidad> comunidadesInteres) {
        this.comunidadesInteres = comunidadesInteres;
    }

    public ArrayList<Comentario> getComentario() {
        return Comentario;
    }

    public void setComentario(ArrayList<Comentario> comentario) {
        Comentario = comentario;
    }
}
