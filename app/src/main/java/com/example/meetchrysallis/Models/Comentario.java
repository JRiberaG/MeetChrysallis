package com.example.meetchrysallis.Models;

import java.util.Date;

public class Comentario {
    private short idEvento;
    private int idSocio;
    private int id;
    private boolean mostrarNombre;
    private Date fecha;
    private boolean activo;
    private Socio socio;
    private Evento evento;
    private String body;

    //Constructores
    public Comentario(){}
    public Comentario(short idEvento, int idSocio, int id, boolean mostrarNombre, Date fecha, boolean activo, Socio socio, Evento evento, String body) {
        this.idEvento = idEvento;
        this.idSocio = idSocio;
        this.id = id;
        this.mostrarNombre = mostrarNombre;
        this.fecha = fecha;
        this.activo = activo;
        this.socio = socio;
        this.evento = evento;
        this.body = body;
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

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
