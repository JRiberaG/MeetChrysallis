package com.example.meetchrysallis.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class Comentario implements Serializable {
    private short idEvento;
    private int idSocio;
    private int id;
    private boolean mostrarNombre;
    private Timestamp fecha;
    private boolean activo;
    @SerializedName("Socios")
    private Socio socio;
    @SerializedName("Eventos")
    private Evento evento;
    private String body;

    //Constructores
    public Comentario(){}
    public Comentario(short idEvento, int idSocio, int id, boolean mostrarNombre, Timestamp fecha, boolean activo, String body) {
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

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
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
