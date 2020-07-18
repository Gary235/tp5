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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;


public class FragResultados extends Fragment {

    private PieChartView chart ,chart2,chart3;


    TextView txtEdad,    txtResultados;
    ListView listEmociones;
    ImageButton btnVolver;
    ImageView imgWarning;
    ArrayList<Boolean> arrBool = new ArrayList<>();
    ProgressBar progressBar;
    adaptadorDeEmociones adaptadorDeEmociones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_resultados, container, false);;

        chart =  v.findViewById(R.id.chart);
        chart2 = v.findViewById(R.id.chart2);
        chart3 = v.findViewById(R.id.chart3);
        imgWarning = v.findViewById(R.id.imgWarning);
        listEmociones = v.findViewById(R.id.listEmociones);
        txtEdad = v.findViewById(R.id.promEdad);
        btnVolver = v.findViewById(R.id.btnvolver);
        progressBar = v.findViewById(R.id.progress);
        txtResultados = v.findViewById(R.id.Resultados);
        progressBar.setMax(100);

        adaptadorDeEmociones = new adaptadorDeEmociones(getActivity(), MainActivity.arrEmociones);
        txtEdad.setText("");
        listEmociones.setAdapter(null);

        MainActivity main = (MainActivity) getActivity();
        arrBool = main.devolverCheck();
        listEmociones.setAdapter(adaptadorDeEmociones);


        if(!arrBool.get(5)){
            if(arrBool.get(0))
            {
                chart3.setVisibility(View.VISIBLE);
                generateDataSexo();
            }
            if(arrBool.get(1))
            {
                txtEdad.setText( "" + MainActivity.promEdad);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) MainActivity.promEdad);
            }
            if(arrBool.get(0) && arrBool.get(3))
            {
                chart2.setVisibility(View.VISIBLE);
                generateDataMaquillaje();
            }
            if(arrBool.get(4) && arrBool.get(2)) {
                chart.setVisibility(View.VISIBLE);
                generateDataCalvos();
            }
            if(arrBool.get(2)){
                listEmociones.setVisibility(View.VISIBLE);
            }
        }
        else {
            txtResultados.setVisibility(View.VISIBLE);
            if(arrBool.get(0))
            {
                chart3.setVisibility(View.VISIBLE);
                generateGeneral();
            }
            if(arrBool.get(1))
            {
                txtEdad.setText( "" + MainActivity.promEdadAcum);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) MainActivity.promEdadAcum);
            }
            if(arrBool.get(0) && arrBool.get(3))
            {
                chart2.setVisibility(View.VISIBLE);
                generateGeneral();
            }
            if(arrBool.get(4) && arrBool.get(2)) {
                chart.setVisibility(View.VISIBLE);
                generateGeneral();
            }
            if(arrBool.get(2)){
                //listEmociones.setVisibility(View.VISIBLE);
            }


        }


        if(!arrBool.get(0) && !arrBool.get(1) && !arrBool.get(2) && !arrBool.get(3) && !arrBool.get(4)  && !arrBool.get(5))
        {
            imgWarning.setVisibility(View.VISIBLE);
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

        List pieData = new ArrayList<>();
        int calvosF= MainActivity.arrCant.get(0);
        int noCalvosF= MainActivity.arrCant.get(1);
        int total= MainActivity.arrCant.get(0)+MainActivity.arrCant.get(1);
        Log.d("Calvos", "calvos " + calvosF + " no calvos " + noCalvosF);
        if(calvosF == 0 && noCalvosF == 0) {
            float porCalvo= 1;
            float porNCalvo= 1;
            pieData.add(new SliceValue(porCalvo, ChartUtils.pickColor()).setLabel("Calvos: 0" ));
            pieData.add(new SliceValue(porNCalvo, ChartUtils.pickColor()).setLabel("No Calvos: 0" ));
        }
        else {
            float porCalvo= (calvosF*100)/total;
            float porNCalvo= (noCalvosF*100)/total;
            pieData.add(new SliceValue(porCalvo, ChartUtils.pickColor()).setLabel("Calvos: " + calvosF));
            pieData.add(new SliceValue(porNCalvo, ChartUtils.pickColor()).setLabel("No Calvos: " + noCalvosF));
        }
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

        if(JovenesM == 0 && ViejasM == 0) {
            float porJov= 1;
            float porViej= 1;
            pieData.add(new SliceValue(porJov, ChartUtils.pickColor()).setLabel("Jovenes: 0" ));
            pieData.add(new SliceValue(porViej, ChartUtils.pickColor()).setLabel("Viejas: 0" ));
        }
        else {
            float porJov= (JovenesM*100)/total;
            float porViej= (ViejasM*100)/total;
            pieData.add(new SliceValue(porJov, ChartUtils.pickColor()).setLabel("Jovenes: " + JovenesM));
            pieData.add(new SliceValue(porViej, ChartUtils.pickColor()).setLabel("Viejas: " + ViejasM));
        }
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Maquillaje").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart2.setPieChartData(pieChartData);

    }

    private void generateDataSexo() {
        MainActivity main = (MainActivity) getActivity();
        List pieData = new ArrayList<>();
        int Hombres=MainActivity.cantH;
        int Mujeres= MainActivity.cantM;
        float total= Hombres+Mujeres;
        float porH= (Hombres*100)/total;
        float porM= (Mujeres*100)/total;
        pieData.add(new SliceValue(porH, ChartUtils.pickColor()).setLabel("Hombres: " + Hombres));
        pieData.add(new SliceValue(porM, ChartUtils.pickColor()).setLabel("Mujeres: " + Mujeres));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Cantidad").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart3.setPieChartData(pieChartData);

    }

    private void generateGeneral(){
        txtResultados.setText("La cantidad de fotos procesadas es : "+MainActivity.cantFotos);
        List pieData0 = new ArrayList<>();
        int calvosF = MainActivity.arrCantAcum.get(0);
        int noCalvosF = MainActivity.arrCantAcum.get(1);
        int total0 = MainActivity.arrCantAcum.get(0) + MainActivity.arrCantAcum.get(1);
        Log.d("PRUEBAGENERAL", "calvos " + MainActivity.arrCantAcum.get(0) + " no calvos " + MainActivity.arrCantAcum.get(1));

        if(calvosF == 0 && noCalvosF == 0) {
            float porCalvo = 1;
            float porNCalvo = 1;
            pieData0.add(new SliceValue(porCalvo, ChartUtils.pickColor()).setLabel("Calvos: 0" ));
            pieData0.add(new SliceValue(porNCalvo, ChartUtils.pickColor()).setLabel("No Calvos: 0" ));
        }
        else {
            float porCalvo = ( calvosF * 100 ) / total0;
            float porNCalvo = (noCalvosF * 100 ) / total0;
            pieData0.add(new SliceValue(porCalvo, ChartUtils.pickColor()).setLabel("Calvos: " + calvosF));
            pieData0.add(new SliceValue(porNCalvo, ChartUtils.pickColor()).setLabel("No Calvos: " + noCalvosF));
        }
        PieChartData pieChartData0 = new PieChartData(pieData0);
        pieChartData0.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData0.setHasCenterCircle(true).setCenterText1("Felicidad").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart.setPieChartData(pieChartData0);

        ////////////////////////////////////////////////////////////////////////////////////////

        List pieData2 = new ArrayList<>();
        float JovenesM = MainActivity.arrPromAcum.get(0);
        float ViejasM = MainActivity.arrPromAcum.get(1);
        float total2 = MainActivity.arrPromAcum.get(0) + MainActivity.arrPromAcum.get(1);

        if(JovenesM == 0 && ViejasM == 0) {
            float porJov= 1;
            float porViej= 1;
            pieData2.add(new SliceValue(porJov, ChartUtils.pickColor()).setLabel("Jovenes: 0" ));
            pieData2.add(new SliceValue(porViej, ChartUtils.pickColor()).setLabel("Viejas: 0" ));
        }
        else {
            float porJov = ( JovenesM * 100 ) / total2;
            float porViej = ( ViejasM * 100 ) / total2;
            pieData2.add(new SliceValue(porJov, ChartUtils.pickColor()).setLabel("Jovenes: " + JovenesM));
            pieData2.add(new SliceValue(porViej, ChartUtils.pickColor()).setLabel("Viejas: " + ViejasM));
        }
        PieChartData pieChartData2 = new PieChartData(pieData2);
        pieChartData2.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData2.setHasCenterCircle(true).setCenterText1("Maquillaje").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart2.setPieChartData(pieChartData2);

        ////////////////////////////////////////////////////////////////////////////////////////

        List pieData3 = new ArrayList<>();
        int Hombres = MainActivity.cantHAcum;
        int Mujeres = MainActivity.cantMAcum;
        float total3 = Hombres + Mujeres;
        float porH = (Hombres * 100) / total3;
        float porM = (Mujeres * 100) / total3;
        pieData3.add(new SliceValue(porH, ChartUtils.pickColor()).setLabel("Hombres: " + Hombres));
        pieData3.add(new SliceValue(porM, ChartUtils.pickColor()).setLabel("Mujeres: " + Mujeres));
        PieChartData pieChartData3 = new PieChartData(pieData3);
        pieChartData3.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData3.setHasCenterCircle(true).setCenterText1("Cantidad").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        chart3.setPieChartData(pieChartData3);


    }

}