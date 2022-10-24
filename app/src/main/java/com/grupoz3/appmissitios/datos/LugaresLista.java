package com.grupoz3.appmissitios.datos;

import com.grupoz3.appmissitios.modelo.TipoLugar;
import com.grupoz3.appmissitios.modelo.Lugar;

import java.util.ArrayList;
import java.util.List;

public class LugaresLista implements RepositorioLugares {

    protected List<Lugar> listaLugares;

    //constructor
    public LugaresLista() {
        listaLugares = new ArrayList<Lugar>();
        añadeEjemplos();
    }

    @Override
    public Lugar elemento(int id) {
        return listaLugares.get(id);
    }

    @Override
    public void añade(Lugar lugar) {
        listaLugares.add(lugar);
    }

    @Override
    public int nuevo() {
        Lugar lugar = new Lugar();
        listaLugares.add(lugar);
        return listaLugares.size()-1;
    }

    @Override
    public void borrar(int id) {
        listaLugares.remove(id);
    }

    @Override
    public int tamaño() {
        return listaLugares.size();
    }

    @Override
    public void actualiza(int id, Lugar lugar) {
        listaLugares.set(id, lugar);
    }

    public void añadeEjemplos(){
        añade(new Lugar("UIS","Calle 9#27",
                -73.121,7.1377,
                TipoLugar.EDUCACION,6344000,
                "https://www.uis.edu.co",
                "Una de las universidades de Colombia",
                5));

        añade(new Lugar("Estadio Atanasio Girardot",
                "Cra. 74 # 48010, Medellín, Antioquia",
                -75.59013,6.256864, TipoLugar.DEPORTE,
                0,"http://comunasdemedellin.com/", "Estadio de la ciudad de medellín",
                (float) 4.5));

        añade(new Lugar("Movistar Arena",
                "Diagonal. 61c #26-36, Bogotá, Cundinamarca",
                -74.07695,4.64888, TipoLugar.ESPECTACULO, 5470183,
                "https://movistararena.co/",
                "Centro de eventos en Bogotá",4));

        añade(new Lugar("Páramo de Santurbán",
                "Silos, Santander",
                -72.90982,7.2466845,
                TipoLugar.NATURALEZA,0, "SIN DATOS",
                "Lugar entre los departamentos Santander y " +
                        "Norte de Santander",
                4));

        añade(new Lugar("Loma Mesa de Ruitoque",
                "Loma Mesa de Ruitoque, Floridablanca," +
                        " Santander",
                0,0, TipoLugar.BAR, 0,
                "SIN DATOS",
                "Lugar en Floridablanca, Santander",
                4));
    }
}
