package com.example.parctfoto;

import android.graphics.Bitmap;

public class Emocion {

    private String nombre;
    private Boolean HayoNo;

    public Emocion(String nombre, Boolean hayoNo) {
        this.nombre = nombre;
        HayoNo = hayoNo;
    }

    public Emocion() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Boolean getHayoNo() { return HayoNo; }
    public void setHayoNo(Boolean hayoNo) { HayoNo = hayoNo; }


}
