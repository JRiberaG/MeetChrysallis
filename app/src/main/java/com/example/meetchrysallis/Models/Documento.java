package com.example.meetchrysallis.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Documento implements Serializable {
    private int id;
    private String url;
    private String datos;
    @Nullable
    private short idEvento;
    private Evento evento;

    public Documento() {
    }

    public Documento(int id, String url, String datos, short idEvento) {
        this.id = id;
        this.url = url;
        this.datos = datos;
        this.idEvento = idEvento;
    }

    public Documento(int id, String url, String datos, short idEvento, Evento evento) {
        this.id = id;
        this.url = url;
        this.datos = datos;
        this.idEvento = idEvento;
        this.evento = evento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public short getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(short idEvento) {
        this.idEvento = idEvento;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}
