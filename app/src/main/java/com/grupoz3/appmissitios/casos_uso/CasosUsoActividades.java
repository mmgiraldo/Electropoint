package com.grupoz3.appmissitios.casos_uso;

import android.app.Activity;
import android.content.Intent;

import com.grupoz3.appmissitios.presentacion.AcercaDeActivity;
import com.grupoz3.appmissitios.presentacion.PreferenciasActivity;

public class CasosUsoActividades {

    protected Activity actividad;
    //constructor
    public CasosUsoActividades(Activity actividad) {
        this.actividad = actividad;
    }

    public void lanzarAcercaDe() {
        actividad.startActivity(new Intent(actividad, AcercaDeActivity.class));
    }

    public void lanzarPreferencias(int codigoSolicitud) {
        actividad.startActivityForResult
                (new Intent(actividad, PreferenciasActivity.class),codigoSolicitud);
    }
}
