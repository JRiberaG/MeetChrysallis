package com.example.meetchrysallis.Models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Asistir implements Serializable {
    private int idSocio;
    private short idEvento;
    private @Nullable byte valoracion;
    private short numAsistentes;
    @SerializedName("Socios")
    private Socio socio;
    @SerializedName("Eventos")
    private Evento evento;

    public Asistir() {
    }

    public Asistir(int idSocio, short idEvento, short numAsistentes, Socio socio, Evento evento) {
        this.idSocio = idSocio;
        this.idEvento = idEvento;
        this.numAsistentes = numAsistentes;
        this.socio = socio;
        this.evento = evento;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public short getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(short idEvento) {
        this.idEvento = idEvento;
    }

    public byte getValoracion() {
        return valoracion;
    }

    public void setValoracion(byte valoracion) {
        this.valoracion = valoracion;
    }

    public short getNumAsistentes() {
        return numAsistentes;
    }

    public void setNumAsistentes(short numAsistentes) {
        this.numAsistentes = numAsistentes;
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
}
