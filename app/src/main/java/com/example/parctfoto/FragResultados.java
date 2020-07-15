package com.example.parctfoto;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;


public class FragResultados extends Fragment {

    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    TextView txtCalvos, txtAccesorios, txtEmociones;
    ImageButton btnVolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_resultados, container, false);;

        //chart =  v.findViewById(R.id.chart);
        //generateData();
        txtCalvos = v.findViewById(R.id.txtCalvos);
        txtAccesorios = v.findViewById(R.id.txtAccesorios);
        txtEmociones = v.findViewById(R.id.txtEmociones);
        btnVolver = v.findViewById(R.id.btnvolver);

        txtCalvos.setText("");
        txtAccesorios.setText("" );
        txtEmociones.setText("");


        if(MainActivity.arrCheckBox.get(0) && MainActivity.arrCheckBox.get(3))
        {
            txtAccesorios.setText("Mujeres Jovenes Maquilladas: " + MainActivity.arrProm.get(0) + "\nMujeres Viejas Maquilladas: " + MainActivity.arrProm.get(1) );
        }

        if(MainActivity.arrCheckBox.get(4) && MainActivity.arrCheckBox.get(2)) {
            txtCalvos.setText("Calvos Felices: " + MainActivity.arrCant.get(0) + "\nNo Calvos Felices: " + MainActivity.arrCant.get(1));
        }
        if(MainActivity.arrCheckBox.get(4)){
            txtEmociones.setText(MainActivity.mensajeEmociones);
        }

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.holder.setVisibility(View.GONE);
            }
        });

        return v;
    }
    /*private void generateData() {

        ArrayList<SliceValue> values = new ArrayList<>();
        for (int i = 0; i < MainActivity.arrCant.size(); ++i) {
            Log.d("Cant", "" + MainActivity.arrCant.get(i) );
            SliceValue sliceValue = new SliceValue(MainActivity.arrCant.get(i), ChartUtils.pickColor());
            if(i == 0) sliceValue.setLabel("Calvos");
            else sliceValue.setLabel("No Calvos");
            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasLabelsOnlyForSelected(true);
        data.setHasLabelsOutside(true);
        data.setHasCenterCircle(true);


        chart.setPieChartData(data);
    }
*/

}