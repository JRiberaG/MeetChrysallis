package com.example.meetchrysallis.Models;

import java.util.Date;

public class Comentario {
    private short idEvento;
    private int idSocio;
    private int id;
    private boolean mostrarNombre;
    private Date fecha;
    private boolean activo;

    //Constructores
    public Comentario(){}
    public Comentario(short idEvento, int idSocio, int id, boolean mostrarNombre, Date fecha, boolean activo) {
        this.idEvento = idEvento;
        this.idSocio = idSocio;
        this.id = id;
        this.mostrarNombre = mostrarNombre;
        this.fecha = fecha;
        this.activo = activo;
    }

    public short getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(short idEvento) {
        this.idEvento = idEvento;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMostrarNombre() {
        return mostrarNombre;
    }

    public void setMostrarNombre(boolean mostrarNombre) {
        this.mostrarNombre = mostrarNombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
