package com.example.meetchrysallis.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Socio implements Serializable {
/*"Asistir": [],
        "Comunidades": null,
        "Comunidades1": [],
        "Comentario": [],
        "id": 1,
        "dni": "46144809R",
        "nombre": "Jorge",
        "apellidos": "Ribera",
        "email": "jorge@jorge.com",
        "contrasenya": "contrasenya",
        "telefono": "670000000",
        "poblacion": "Barcelona",
        "idComunidad": 7,
        "activo": true*/
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
    //private Comunidad idComunidad;
    private ArrayList<Asistir> Asistir;
    private Comunidad Comunidades;
    private ArrayList<Comunidad> Comunidades1;
    private ArrayList<Comentario> Comentario;

    //Constructores
    public Socio(){}
    public Socio(String email, String contrasenya){
        this.id = -1;
        this.dni = null;
        this.nombre = null;
        this.apellidos = null;
        this.email = email;
        this.contrasenya = contrasenya;
        this.telefono = null;
        this.poblacion = null;
        //this.idComunidad = null;
        this.activo = true;
    }

    public Socio(int id, String dni, String nombre, String apellidos, String email, String contrasenya, String telefono, String poblacion, byte idComunidad, boolean activo, ArrayList<Asistir> asistir, Comunidad comunidades, ArrayList<Comunidad> comunidades1, ArrayList<Comentario> comentario) {
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
        this.Asistir = asistir;
        this.Comunidades = comunidades;
        this.Comunidades1 = comunidades1;
        this.Comentario = comentario;
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

    public byte getComunidad() {
        return idComunidad;
    }

    public void setComunidad(byte idComunidad) {
        this.idComunidad = idComunidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }








    public byte getIdComunidad() {
        return idComunidad;
    }

    public void setIdComunidad(byte idComunidad) {
        this.idComunidad = idComunidad;
    }

    public ArrayList<com.example.meetchrysallis.Models.Asistir> getAsistir() {
        return Asistir;
    }

    public void setAsistir(ArrayList<com.example.meetchrysallis.Models.Asistir> asistir) {
        Asistir = asistir;
    }

    public Comunidad getComunidades() {
        return Comunidades;
    }

    public void setComunidades(Comunidad comunidades) {
        Comunidades = comunidades;
    }

    public ArrayList<Comunidad> getComunidades1() {
        return Comunidades1;
    }

    public void setComunidades1(ArrayList<Comunidad> comunidades1) {
        Comunidades1 = comunidades1;
    }

    public ArrayList<com.example.meetchrysallis.Models.Comentario> getComentario() {
        return Comentario;
    }

    public void setComentario(ArrayList<com.example.meetchrysallis.Models.Comentario> comentario) {
        Comentario = comentario;
    }
}
