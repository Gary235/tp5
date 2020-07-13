package com.example.parctfoto;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public  FragResultados() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_resultados, container, false);;

        chart =  v.findViewById(R.id.chart);

        generateData();

        return v;
    }
    private void generateData() {

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


}