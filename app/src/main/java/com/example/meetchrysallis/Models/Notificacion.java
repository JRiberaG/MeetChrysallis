package com.example.meetchrysallis.Models;

import java.io.Serializable;

public class Notificacion implements Serializable {
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
