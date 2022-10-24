package com.grupoz3.appmissitios.casos_uso;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.grupoz3.appmissitios.R;
import com.grupoz3.appmissitios.datos.RepositorioLugares;
import com.grupoz3.appmissitios.modelo.GeoPunto;
import com.grupoz3.appmissitios.modelo.Lugar;
import com.grupoz3.appmissitios.presentacion.EdicionLugarActivity;
import com.grupoz3.appmissitios.presentacion.VistaLugarActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.grupoz3.appmissitios.casos_uso.CasosUsoLocalizacion.solicitarPermiso;

public class CasosUsoLugar {

    private Activity actividad;
    private RepositorioLugares lugares;
    private static final int SOLICITUD_PERMISO_LECTURA=0;
    //constructor
    public CasosUsoLugar(Activity actividad, RepositorioLugares lugares) {
        this.actividad = actividad;
        this.lugares = lugares;
    }

    // OPERACIONES BÁSICAS
    public void mostrar(int pos) {
        Intent mostrar = new Intent(actividad, VistaLugarActivity.class);
        mostrar.putExtra("pos", pos);
        actividad.startActivity(mostrar);
    }

    public void borrar(final int id) {
        new AlertDialog.Builder(actividad)
                .setTitle("Borrado de lugar")
                .setIcon(R.mipmap.icono_app)
                .setMessage("¿Seguro de eliminar este lugar?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lugares.borrar(id);
                        actividad.finish();
                    }})
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void eliminar_Foto(final int id, ImageView foto, View v) {
        new AlertDialog.Builder(actividad)
                .setTitle("Eliminar foto")
                .setIcon(R.mipmap.icono_app)
                .setMessage("¿Seguro de eliminar esa foto?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Snackbar.make(v,"Imagen eliminada",Snackbar.LENGTH_LONG).show();
                        ponerFoto(id,"",foto);
                    }})
                .setNegativeButton("NO", null)
                .show();
    }

    public void editar(int pos, int codigoSolicitud) {
        Intent intent_ed_lugar = new Intent(actividad, EdicionLugarActivity.class);
        intent_ed_lugar.putExtra("pos",pos);
        actividad.startActivityForResult(intent_ed_lugar,codigoSolicitud);
    }

    public void guardar(int id, Lugar nuevoLugar){
        lugares.actualiza(id,nuevoLugar);
    }

    // INTENCIONES
    public void compartir(Lugar lugar) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"Observa este lugar "
                +lugar.getNombre() + " - " + lugar.getUrl());
        actividad.startActivity(i);
    }
    public void llamarTelefono(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void verPgWeb(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    public final void verMapa(Lugar lugar) {
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        Uri uri = lugar.getPosicion() != GeoPunto.SIN_POSICION
                ?Uri.parse("geo:" + lat + ',' +
                lon+"?z=18&q="+Uri.encode(lugar.getDireccion()))
                :Uri.parse("geo:0,0?q="+Uri.encode(lugar.getDireccion()));
        Log.d("tag casos uso lugar","vermapa " +uri + " " +
                        Uri.encode(lugar.getDireccion())+ "\n" + lugar.getPosicion() + " geopto "+GeoPunto.SIN_POSICION);
                actividad.startActivity(new Intent("android.intent.action.VIEW",
                        uri));
    }

    public void ponerDeGaleria(int codigoSolicitud){
        String action;
        if (Build.VERSION.SDK_INT>=19){
            action = Intent.ACTION_OPEN_DOCUMENT;
        }else {
            action = Intent.ACTION_PICK;
        }
        Intent intent = new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        actividad.startActivityForResult(intent,codigoSolicitud);
    }

    public void ponerFoto(int pos, String uri, ImageView imageView){
        Lugar lugar = lugares.elemento(pos);
        lugar.setFoto(uri);
        visualizarFoto(lugar, imageView);
    }

    public void visualizarFoto(Lugar lugar, ImageView imageView){
        if(lugar.getFoto()!=null && !lugar.getFoto().isEmpty()){
            // GESTION DEL PERMISO DE LECTURA DEL ALMACENAMIENTO
            if (ContextCompat.checkSelfPermission(actividad, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //imageView.setImageURI(Uri.parse(lugar.getFoto()));
                imageView.setImageBitmap(reduceBitmap(actividad,lugar.getFoto(),1024,1024));
            }
        } else {
            imageView.setImageBitmap(null);
            solicitarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE, "Sin el permiso de lectura no es posible abrir la galeria del dispositivo",SOLICITUD_PERMISO_LECTURA,actividad);
        }
    }

    private Bitmap reduceBitmap(Context contexto, String uri,int maxAncho, int maxAlto) {
        try {
            InputStream input = null;
            Uri u = Uri.parse(uri);
            if (u.getScheme().equals("http") || u.getScheme().equals("https")) {
                input = new URL(uri).openStream();
            } else {
                input = contexto.getContentResolver().openInputStream(u);
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso de imagen no encontrado "+e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Toast.makeText(contexto, "Error accediendo a imagen "+e.getMessage() ,
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    public Uri tomarFoto(int codigoSolicitud){
        try{
            Uri uriUltimatoFoto;
            File file = File.createTempFile("img_"+
                    (System.currentTimeMillis()/1000),".jpg",
                    actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            //PERMISO DE LECTURA AL ABRIR LA CAMARA
            if (ContextCompat.checkSelfPermission(actividad,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                solicitarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE,"Sin el permiso de lectura no es posible abrir la camara",SOLICITUD_PERMISO_LECTURA,actividad);
                uriUltimatoFoto = Uri.parse(String.valueOf(R.mipmap.icono_app));
            }
            else if (Build.VERSION.SDK_INT>=24){
                uriUltimatoFoto = FileProvider.getUriForFile(actividad,"misiontic.appmissitios.fileProvider",file);
            } else {
                uriUltimatoFoto = Uri.fromFile(file);
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uriUltimatoFoto);
            actividad.startActivityForResult(intent,codigoSolicitud);
            return uriUltimatoFoto;
        } catch (IOException e) {
            Toast.makeText(actividad,"Error al crear el fichero de imagen "+e.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }


}
