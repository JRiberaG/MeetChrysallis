package com.example.meetchrysallis.Models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Evento implements Serializable {

    private short id;
    private String titulo;
    private Timestamp fecha;
    private String ubicacion;
    private String descripcion;
    @Nullable
    private Timestamp fecha_limite;
    @SerializedName("Comunidades")
    private Comunidad comunidad;
    private byte idComunidad;
    //private Administrador admin;
    @SerializedName("Asistir")
    private List<Asistir> asistencia;
    private List<Documento> documentos;
    private List<Notificacion> notificaciones;
    private float valoracionMedia;
    @SerializedName("Comentarios")
    private ArrayList<Comentario> comentarios;

    public Evento(){}

    public Evento(short id, String titulo, Timestamp fecha, String ubicacion, String descripcion, @Nullable Timestamp fecha_limite, Comunidad comunidad, byte idComunidad, List<Asistir> asistencia, List<Documento> documentos, List<Notificacion> notificaciones, float valoracionMedia, ArrayList<Comentario> comentarios) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.fecha_limite = fecha_limite;
        this.comunidad = comunidad;
        this.idComunidad = idComunidad;
        this.asistencia = asistencia;
        this.documentos = documentos;
        this.notificaciones = notificaciones;
        this.valoracionMedia = valoracionMedia;
        this.comentarios = comentarios;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Nullable
    public Timestamp getFecha_limite() {
        return fecha_limite;
    }

    public void setFecha_limite(@Nullable Timestamp fecha_limite) {
        this.fecha_limite = fecha_limite;
    }

    public Comunidad getComunidad() {
        return comunidad;
    }

    public void setComunidad(Comunidad comunidad) {
        this.comunidad = comunidad;
    }

    public byte getIdComunidad() {
        return idComunidad;
    }

    public void setIdComunidad(byte idComunidad) {
        this.idComunidad = idComunidad;
    }

    public List<Asistir> getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(List<Asistir> asistencia) {
        this.asistencia = asistencia;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public float getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(float valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }

    public ArrayList<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
}