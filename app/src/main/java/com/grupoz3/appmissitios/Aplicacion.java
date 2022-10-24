package com.grupoz3.appmissitios;

import android.app.Application;

import com.grupoz3.appmissitios.datos.LugaresLista;
import com.grupoz3.appmissitios.datos.RepositorioLugares;
import com.grupoz3.appmissitios.modelo.GeoPunto;
import com.grupoz3.appmissitios.presentacion.AdaptadorLugares;

public class Aplicacion extends Application {

    public RepositorioLugares lugares = new LugaresLista();
    public AdaptadorLugares adaptador = new AdaptadorLugares(lugares);
    public GeoPunto posicionActual = new GeoPunto(0.0,0.0);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public RepositorioLugares getLugares() {
        return lugares;
    }
}
