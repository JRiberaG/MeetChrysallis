package com.example.meetchrysallis.Models;

public class Documento {
    private int id;
    private String url;
    private Evento evento;

    public Documento(){}
    public Documento(int id, String url, Evento evento) {
        this.id = id;
        this.url = url;
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

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}
