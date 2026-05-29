package com.findra.model;

public class Denunciante {

    private String nombre;
    private String vinculo;
    private String tel;

    public Denunciante() {
    }

    public Denunciante(String nombre, String vinculo, String tel) {
        this.nombre = nombre;
        this.vinculo = vinculo;
        this.tel = tel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVinculo() {
        return vinculo;
    }

    public void setVinculo(String vinculo) {
        this.vinculo = vinculo;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
