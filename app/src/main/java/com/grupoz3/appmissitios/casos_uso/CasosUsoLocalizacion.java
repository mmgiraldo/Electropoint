package com.grupoz3.appmissitios.casos_uso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.grupoz3.appmissitios.Aplicacion;
import com.grupoz3.appmissitios.R;
import com.grupoz3.appmissitios.modelo.GeoPunto;
import com.grupoz3.appmissitios.presentacion.AdaptadorLugares;

import static android.content.Context.LOCATION_SERVICE;

public class CasosUsoLocalizacion implements LocationListener {

    private static final String TAG = "info log";
    private Activity actividad;
    private int codigoPermiso;
    private LocationManager manejadorLoc;
    private Location mejorLoc;
    private GeoPunto posicionActual;
    private AdaptadorLugares adaptador;
    private static final long DOS_MINUTOS = 2*60*1000;

    public CasosUsoLocalizacion(Activity actividad, int codigoPermiso) {
        this.actividad = actividad;
        this.codigoPermiso = codigoPermiso;
        manejadorLoc = (LocationManager) actividad.getSystemService(LOCATION_SERVICE);
        posicionActual =((Aplicacion)actividad.getApplication()).posicionActual;
        adaptador =((Aplicacion)actividad.getApplication()).adaptador;
        ultimaLocalizacion();
    }

    public boolean hayPermisoLocalizacion(){
        return (ActivityCompat.checkSelfPermission(actividad, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("MissingPermission")
    public void ultimaLocalizacion(){
        if (hayPermisoLocalizacion()){
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            } if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION, "Sin el permiso de localización no puede visualizar la distancia", codigoPermiso,actividad);
        }
    }

    public static void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad){
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud del permiso en la APP")
                    .setMessage(justificacion)
                    .setIcon(R.mipmap.icono_app)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(actividad, new String[]{permiso}, requestCode);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(actividad, new String[]{permiso}, requestCode);
        }
    }

    public void permisoConcedido(){
        ultimaLocalizacion();
        activarProveedores();
        adaptador.notifyDataSetChanged();
    }

    @SuppressLint("MissingPermission")
    private void activarProveedores(){
        if (hayPermisoLocalizacion()){
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                manejadorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,20*1000,5, (LocationListener) this);
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                manejadorLoc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10*1000,10, (LocationListener) this);
            }
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,"Sin el permiso no puede actualizar las distancias de los sitios",codigoPermiso,actividad);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: "+location);
        actualizaMejorLocaliz(location);
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Cambia estado: "+ provider);
        activarProveedores();
    }

    @Override public void onProviderEnabled(String provider) {
        Log.d(TAG, "Se habilita: "+provider);
        activarProveedores();
    }

    @Override public void onProviderDisabled(String provider) {
        Log.d(TAG, "Se deshabilita: "+provider);
        activarProveedores();
    }

    private void actualizaMejorLocaliz(Location localiz){
        if(localiz !=null && (mejorLoc==null ||
                localiz.getAccuracy()<2*mejorLoc.getAccuracy()
        || localiz.getTime()-mejorLoc.getTime()>DOS_MINUTOS)){
            Log.d(TAG,"nueva localizacion "+localiz.getAltitude());
            mejorLoc = localiz;
            posicionActual.setLatitud(localiz.getLatitude());
            posicionActual.setLongitud(localiz.getLongitude());
        }
    }

    public void activar(){
        if (hayPermisoLocalizacion()) activarProveedores();
    }

    public void desactivar(){
        if (hayPermisoLocalizacion()) manejadorLoc.removeUpdates(this);
    }

}
