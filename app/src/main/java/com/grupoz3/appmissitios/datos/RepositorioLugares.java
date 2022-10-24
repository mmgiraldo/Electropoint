package com.grupoz3.appmissitios.datos;

import com.grupoz3.appmissitios.modelo.Lugar;

public interface RepositorioLugares {

    Lugar elemento(int id);
    void añade(Lugar lugar);
    int nuevo();
    void borrar(int id);
    int tamaño();
    void actualiza(int id, Lugar lugar);
}
