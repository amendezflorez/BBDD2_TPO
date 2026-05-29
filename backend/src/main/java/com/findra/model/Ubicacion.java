package com.findra.model;

import java.util.List;

public class Ubicacion {

    private String type = "Point";
    private List<Double> coordinates;
    private String descripcion;

    public Ubicacion() {
    }

    public Ubicacion(double longitude, double latitude, String descripcion) {
        this.coordinates = List.of(longitude, latitude);
        this.descripcion = descripcion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
