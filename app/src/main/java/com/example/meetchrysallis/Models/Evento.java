package com.example.meetchrysallis.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Evento implements Serializable {

    private short id;
    private String titulo;
    private Date fecha;
    private String ubicacion;
    private String descripcion;
    private @Nullable Date fecha_limite;
    private Comunidad comunidad;
    private Administrador admin;
    private List<Asistir> asistir;
    private List<Documento> documentos;
    private List<Notificacion> notificaciones;

    public Evento(){}

    public Evento(short id, String titulo, Date fecha, String ubicacion, String descripcion, @Nullable Date fecha_limite, Comunidad comunidad, Administrador admin, List<Asistir> asistir, List<Documento> documentos, List<Notificacion> notificaciones) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.fecha_limite = fecha_limite;
        this.comunidad = comunidad;
        this.admin = admin;
        this.asistir = asistir;
        this.documentos = documentos;
        this.notificaciones = notificaciones;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
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
    public Date getFecha_limite() {
        return fecha_limite;
    }

    public void setFecha_limite(@Nullable Date fecha_limite) {
        this.fecha_limite = fecha_limite;
    }

    public Comunidad getComunidad() {
        return comunidad;
    }

    public void setComunidad(Comunidad comunidad) {
        this.comunidad = comunidad;
    }

    public Administrador getAdmin() {
        return admin;
    }

    public void setAdmin(Administrador admin) {
        this.admin = admin;
    }

    public List<Asistir> getAsistir() {
        return asistir;
    }

    public void setAsistir(List<Asistir> asistir) {
        this.asistir = asistir;
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
}
