package com.example.meetchrysallis.Models;

public class Comunidad {
    private byte id;
    private String nombre;

    public Comunidad(){}
    public Comunidad(byte id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public byte getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
