package com.example.meetchrysallis.Models;

import java.io.Serializable;
import java.util.List;

public class Comunidad implements Serializable {
    private byte id;
    private String nombre;
    private List<Socio> socios;
    private List<Socio> socios1;
    private List<Evento> eventos;
    private List<Administrador> administradores;

    public Comunidad(){}
    public Comunidad(byte id, String nombre, List<Socio> socios, List<Socio> socios1, List<Evento> eventos, List<Administrador> administradores) {
        this.id = id;
        this.nombre = nombre;
        this.socios = socios;
        this.socios1 = socios1;
        this.eventos = eventos;
        this.administradores = administradores;
    }

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

    public List<Socio> getSocios() {
        return socios;
    }

    public void setSocios(List<Socio> socios) {
        this.socios = socios;
    }

    public List<Socio> getSocios1() {
        return socios1;
    }

    public void setSocios1(List<Socio> socios1) {
        this.socios1 = socios1;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(List<Administrador> administradores) {
        this.administradores = administradores;
    }
}
