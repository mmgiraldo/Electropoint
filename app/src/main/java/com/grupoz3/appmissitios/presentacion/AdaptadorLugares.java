package com.grupoz3.appmissitios.presentacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.grupoz3.appmissitios.Aplicacion;
import com.grupoz3.appmissitios.R;
import com.grupoz3.appmissitios.datos.RepositorioLugares;
import com.grupoz3.appmissitios.modelo.GeoPunto;
import com.grupoz3.appmissitios.modelo.Lugar;


public class AdaptadorLugares extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> {

    protected RepositorioLugares lugares;
    protected View.OnClickListener onClickListener;

    public AdaptadorLugares(RepositorioLugares lugares) {
        this.lugares = lugares;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre, direccion, distancia ;
        public ImageView foto;
        public RatingBar valoracion;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            direccion = itemView.findViewById(R.id.direccion);
            foto = itemView.findViewById(R.id.foto);
            valoracion = itemView.findViewById(R.id.valoracion);
            distancia = itemView.findViewById(R.id.distancia);
        }

        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(Lugar lugar) {
            nombre.setText(lugar.getNombre());
            direccion.setText(lugar.getDireccion());
            int id = R.drawable.otros;
            switch(lugar.getTipo()) {
                case RESTAURANTE:id = R.drawable.restaurante; break;
                case BAR: id = R.drawable.bar; break;
                case COPAS: id = R.drawable.copas; break;
                case ESPECTACULO:id = R.drawable.espectaculos; break;
                case HOTEL: id = R.drawable.hotel; break;
                case COMPRAS: id = R.drawable.compras; break;
                case EDUCACION: id = R.drawable.educacion; break;
                case DEPORTE: id = R.drawable.deporte; break;
                case NATURALEZA: id = R.drawable.naturaleza; break;
                case GASOLINERA: id = R.drawable.gasolinera; break;
            }
            foto.setImageResource(id);
            foto.setScaleType(ImageView.ScaleType.FIT_END);
            valoracion.setRating(lugar.getValoracion());

            GeoPunto pos = ((Aplicacion)itemView.getContext().getApplicationContext()).posicionActual;
            if(pos.equals(GeoPunto.SIN_POSICION)|| lugar.getPosicion().equals(GeoPunto.SIN_POSICION)){
                distancia.setText("---Km");
            } else {
                int d = (int) pos.distancia(lugar.getPosicion());
                if (d<2000){
                    distancia.setText(d+" m");
                } else {
                    distancia.setText(d/1000+ " Km");
                }
            }

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista,parent,false);
        vista.setOnClickListener(onClickListener);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lugar lugar = lugares.elemento(position);
        holder.personaliza(lugar);
    }

    @Override
    public int getItemCount() {
        return lugares.tamaño();
    }


}
