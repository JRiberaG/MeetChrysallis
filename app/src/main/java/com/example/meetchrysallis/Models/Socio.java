package com.example.meetchrysallis.Models;

import java.io.Serializable;

public class Socio implements Serializable {

    private int id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasenya;
    private String telefono;
    private String poblacion;
    private Comunidad comuniad;
    private boolean activo;

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
        this.comuniad = null;
        this.activo = true;
    }
    public Socio(int id, String dni, String nombre, String apellidos, String email, String contrasenya, String telefono, String poblacion, Comunidad comuniad, boolean activo) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenya = contrasenya;
        this.telefono = telefono;
        this.poblacion = poblacion;
        this.comuniad = comuniad;
        this.activo = activo;
    }
}
