package com.grupoz3.appmissitios.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.grupoz3.appmissitios.Aplicacion;
import com.grupoz3.appmissitios.R;
import com.grupoz3.appmissitios.casos_uso.CasosUsoLugar;
import com.grupoz3.appmissitios.datos.RepositorioLugares;
import com.grupoz3.appmissitios.modelo.Lugar;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;

public class VistaLugarActivity extends AppCompatActivity {

    private RepositorioLugares lugares;
    private CasosUsoLugar usoLugar;
    private int pos;
    private Lugar lugar;
    final static int RESULTADO_EDITAR = 1;
    private TextView nombre, tipo,direccion,telefono,url,comentario,fecha, hora;
    private ImageView logo_tipo, foto, galeria, camara, eliminar;
    private RatingBar valoracion;
    final static  int RESULTADO_GALERIA =2;
    final static  int RESULTADO_FOTO=3;
    private Uri uriUltimaFoto;
    //permiso de galeria
    private static final int SOLICITUD_PERMISO_LECTURA =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", 0);
        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares);
        lugar = lugares.elemento(pos);
        foto = findViewById(R.id.foto);
        galeria = findViewById(R.id.galeria);
        camara = findViewById(R.id.camara);
        eliminar = findViewById(R.id.eliminar);

        actualizaVistas();
        llamar();
        verWeb();
        abrirGaleria();
        tomarFotoCamara();
        eliminarFoto();
    }

    public void actualizaVistas() {
        nombre = findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());

        logo_tipo = findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());

        tipo = findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());

        direccion = findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());

        telefono = findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));

        url = findViewById(R.id.url);
        url.setText(lugar.getUrl());

        comentario = findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());

        fecha = findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(new Date(lugar.getFecha())));

        hora = findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(new Date(lugar.getFecha())));

        valoracion = findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override public void onRatingChanged(RatingBar ratingBar, float valor,
            boolean fromUser) {
                        lugar.setValoracion(valor);
                    }
        });

        usoLugar.visualizarFoto(lugar,foto);

    }

    public void llamar(){
        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.llamarTelefono(lugar);
            }
        });
    }

    public void verWeb(){
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.verPgWeb(lugar);
            }
        });
    }

    public void abrirGaleria(){
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.ponerDeGaleria(RESULTADO_GALERIA);
            }
        });
    }

    public void tomarFotoCamara(){
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriUltimaFoto = usoLugar.tomarFoto(RESULTADO_FOTO);
            }
        });
    }

    public void eliminarFoto(){
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               usoLugar.eliminar_Foto(pos,foto,v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                usoLugar.compartir(lugar);
                return true;
            case R.id.accion_llegar:
                usoLugar.verMapa(lugar);
                return true;
            case R.id.accion_editar:
                usoLugar.editar(pos,RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                usoLugar.borrar(pos);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR){
            actualizaVistas();
            findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode ==RESULTADO_GALERIA){
            if (resultCode== Activity.RESULT_OK){
                usoLugar.ponerFoto(pos, data.getDataString(),foto);
            } else{
                Toast.makeText(this,"Imagen no cargada correctamente",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode==RESULTADO_FOTO){
            if (resultCode==Activity.RESULT_OK && uriUltimaFoto!=null){
                lugar.setFoto(uriUltimaFoto.toString());
                usoLugar.ponerFoto(pos,lugar.getFoto(),foto);
            } else {
                Toast.makeText(this,"Error al tomar la foto",
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==SOLICITUD_PERMISO_LECTURA){
            if (grantResults.length==1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                usoLugar.ponerFoto(pos,lugar.getFoto(),foto);
            }else {
                usoLugar.ponerFoto(pos,"",foto);
            }

        }
    }
}
