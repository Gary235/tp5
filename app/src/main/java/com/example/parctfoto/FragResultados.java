package com.example.parctfoto;

import android.app.Fragment;
import android.graphics.Color;
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

    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    TextView txtCalvos, txtAccesorios, txtEmociones;
    ImageButton btnVolver;
    ArrayList<Boolean> arrBool = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_resultados, container, false);;

        chart =  v.findViewById(R.id.chart);
        generateDataCalvos();
        txtCalvos = v.findViewById(R.id.txtCalvos);
        txtEmociones = v.findViewById(R.id.txtEmociones);
        btnVolver = v.findViewById(R.id.btnvolver);

        txtCalvos.setText("");
        txtEmociones.setText("");

        MainActivity main = (MainActivity) getActivity();
        arrBool = main.devolverCheck();

        if(arrBool.get(0))
        {
            generateDataSexo();
            chart.setVisibility(View.VISIBLE);
        }
        if(arrBool.get(1))
        {
            txtEmociones.setText( ""+MainActivity.promEdad);
        }
        if(arrBool.get(0) && arrBool.get(3))
        {
            generateDataMaquillaje();
            chart.setVisibility(View.VISIBLE);
        }
        if(arrBool.get(4) && arrBool.get(2)) {
            generateDataCalvos();
            chart.setVisibility(View.VISIBLE);
        }
        if(arrBool.get(2)){
            txtEmociones.setText(MainActivity.mensajeEmociones);
        }

        if(txtCalvos.getText() == "" && txtEmociones.getText() == "" && chart.getVisibility()==View.GONE )
        {
            txtCalvos.setText("Habilite los rasgos para\nobtener Resultados");
        }


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.holder.setVisibility(View.GONE);
            }
        });

        return v;
    }
    private void generateDataCalvos() {
        MainActivity main = (MainActivity) getActivity();
        List pieData = new ArrayList<>();
        int calvosF= MainActivity.arrCant.get(0);
        int noCalvosF= MainActivity.arrCant.get(1);
        int total= MainActivity.arrCant.get(0)+MainActivity.arrCant.get(1);
        float porCalvo= (calvosF*100)/total;
        float porNCalvo= (noCalvosF*100)/total;
        pieData.add(new SliceValue(porCalvo, Color.BLUE).setLabel("Calvos"));
        pieData.add(new SliceValue(porNCalvo, Color.GRAY).setLabel("No Calvos"));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Felicidad").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart.setPieChartData(pieChartData);

    }
    private void generateDataMaquillaje() {
        MainActivity main = (MainActivity) getActivity();
        List pieData = new ArrayList<>();
        float JovenesM=MainActivity.arrProm.get(0);
        float ViejasM= MainActivity.arrProm.get(1);
        float total= MainActivity.arrProm.get(0)+MainActivity.arrProm.get(1);
        float porJov= (JovenesM*100)/total;
        float porViej= (ViejasM*100)/total;
        pieData.add(new SliceValue(porJov, Color.BLUE).setLabel("Jovenes"));
        pieData.add(new SliceValue(porViej, Color.GRAY).setLabel("Viejas"));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Maquillaje").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart.setPieChartData(pieChartData);

    }
    private void generateDataSexo() {
        MainActivity main = (MainActivity) getActivity();
        List pieData = new ArrayList<>();
        int Hombres=MainActivity.cantH;
        int Mujeres= MainActivity.cantM;
        float total= Hombres+Mujeres;
        float porH= (Hombres*100)/total;
        float porM= (Mujeres*100)/total;
        pieData.add(new SliceValue(porH, Color.BLUE).setLabel("Hombres"));
        pieData.add(new SliceValue(porM, Color.GRAY).setLabel("Mujeres"));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Cantidad").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart.setPieChartData(pieChartData);

    }



}