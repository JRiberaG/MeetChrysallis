package com.example.meetchrysallis.Models;

public class Notificacion {
    private int id;
    private int antelacion;
    private Evento evento;

    public Notificacion(){}

    public Notificacion(int id, int antelacion, Evento evento) {
        this.id = id;
        this.antelacion = antelacion;
        this.evento = evento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAntelacion() {
        return antelacion;
    }

    public void setAntelacion(int antelacion) {
        this.antelacion = antelacion;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}
