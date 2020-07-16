package com.example.parctfoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class adaptadorDeEmociones extends BaseAdapter {
    private Context miContexto;
    private ArrayList<Emocion> arrEmociones = new ArrayList<>();

    public adaptadorDeEmociones(Context miContexto, ArrayList<Emocion> arrEmociones) {
        this.miContexto = miContexto;
        this.arrEmociones = arrEmociones;
    }

    @Override
    public int getCount() {
        return arrEmociones.size();
    }

    @Override
    public Emocion getItem(int position) {
        Emocion emocion = arrEmociones.get(position);
        return emocion;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        protected ImageView tickCruz;
        protected TextView Nombre;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) miContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_emociones, parent, false);

            holder.tickCruz = convertView.findViewById(R.id.fototickCruz);
            holder.Nombre = convertView.findViewById(R.id.nombreEmocion);
            holder.tickCruz.setFocusable(false);
            holder.Nombre.setFocusable(false);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.Nombre.setText(arrEmociones.get(position).getNombre());

        if(arrEmociones.get(position).getHayoNo())
        {
            holder.tickCruz.setImageResource(R.drawable.tick);
        }
        else {
            holder.tickCruz.setImageResource(R.drawable.cruz);
        }


        return convertView;
    }
}
