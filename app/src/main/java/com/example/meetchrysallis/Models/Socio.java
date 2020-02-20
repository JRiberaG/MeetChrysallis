package com.example.meetchrysallis.Models;

public class Socio {

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
