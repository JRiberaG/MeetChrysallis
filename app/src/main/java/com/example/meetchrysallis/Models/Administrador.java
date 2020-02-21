package com.example.meetchrysallis.Models;

import java.util.List;

public class Administrador {
    private byte id;
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasenya;
    private boolean superadmin;
    private List<Evento> eventos;
    private List<Comunidad> comunidades;

    //Constructores
    public Administrador(){}
    public Administrador(byte id, String nombre, String apellidos, String email, String contrasenya, boolean superadmin, List<Evento> eventos, List<Comunidad> comunidades) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenya = contrasenya;
        this.superadmin = superadmin;
        this.eventos = eventos;
        this.comunidades = comunidades;
    }

    //Getters & Setters
    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
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

    public boolean isSuperadmin() {
        return superadmin;
    }

    public void setSuperadmin(boolean superadmin) {
        this.superadmin = superadmin;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public List<Comunidad> getComunidades() {
        return comunidades;
    }

    public void setComunidades(List<Comunidad> comunidades) {
        this.comunidades = comunidades;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", contrasenya='" + contrasenya + '\'' +
                ", superadmin=" + superadmin +
                ", eventos=" + eventos +
                ", comunidades=" + comunidades +
                '}';
    }
}
