package com.findra.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Menor {

    private String nombre;
    private int edad;
    private String sexo;
    private String cabello;
    private String ojos;
    private String estatura;
    private String peso;
    private String ropa;
    private String senas;

    @Field("ultima_ubicacion")
    private Ubicacion ultimaUbicacion;

    @Field("foto_url")
    private String fotoUrl;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCabello() {
        return cabello;
    }

    public void setCabello(String cabello) {
        this.cabello = cabello;
    }

    public String getOjos() {
        return ojos;
    }

    public void setOjos(String ojos) {
        this.ojos = ojos;
    }

    public String getEstatura() {
        return estatura;
    }

    public void setEstatura(String estatura) {
        this.estatura = estatura;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getRopa() {
        return ropa;
    }

    public void setRopa(String ropa) {
        this.ropa = ropa;
    }

    public String getSenas() {
        return senas;
    }

    public void setSenas(String senas) {
        this.senas = senas;
    }

    public Ubicacion getUltimaUbicacion() {
        return ultimaUbicacion;
    }

    public void setUltimaUbicacion(Ubicacion ultimaUbicacion) {
        this.ultimaUbicacion = ultimaUbicacion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
