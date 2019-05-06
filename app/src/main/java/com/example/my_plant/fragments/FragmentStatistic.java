package com.example.my_plant.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_plant.PersistentStorage;
import com.example.my_plant.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import database.DBParams;
import model.Params;

public class FragmentStatistic extends Fragment {
    protected FragmentActivity mActivity;

    private CombinedChart chart;

    private DBParams mDBParams;

    public FragmentStatistic() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle("Влажность");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View v = getView();
        assert v != null;

        chart = v.findViewById(R.id.humidity_chart);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE,
                CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(15f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        final List<Data> finalData = getData();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Log.d("Statistics", "float value formated: " + value);
                Log.d("Statistics", "return value formated: " + finalData.get(Math.min((int) value, finalData.size()-1)).xAxisValue);
                return finalData.get(Math.min((int) value, finalData.size()-1)).xAxisValue;
            }
        });



        CombinedData cdata = new CombinedData();

        //cdata.setData(generateLineData());
        cdata.setData(generateBarData(finalData));


        xAxis.setAxisMaximum(cdata.getXMax() + 0.25f);
        xAxis.setAxisMinimum(cdata.getXMin() - 0.25f);

        chart.setData(cdata);
        chart.invalidate();


    }

    /*private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < count; index++)
            entries.add(new Entry(index + 0.5f, getRandom(15, 5)));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }*/

    private List<Data> getData(){
        final List<Data> data = new ArrayList<>();

        mDBParams = new DBParams(mActivity);
        List<Params> items = mDBParams.getParamsOfProfileForeGraph(PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY));

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        Date date = new Date(getItem(0, items).getDate());
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        int avgHumid = 0;
        int counter = 0;
        int dayNum = 0;

        for (int index = 0; index < items.size(); index++) {

            date = new Date(getItem(index, items).getDate());
            cal.setTime(date);

            Log.d("Statistics", "ДЕНЬ приема: " + cal.get(Calendar.DAY_OF_MONTH));

            if (cal.get(Calendar.DAY_OF_MONTH) == day) {
                avgHumid += getItem(index, items).getHumidity();
                counter++;

            } else {
                Log.d("Statistics", "String day: " + day);
                Log.d("Statistics", "DAY NUM: " + dayNum);
                String xVal = day + "-" + (month + 1);
                data.add(new Data((float)dayNum, (float)avgHumid / counter, xVal));
                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                counter = 1;
                avgHumid = getItem(index, items).getHumidity();
                dayNum++;
            }

        }
        
        Log.d("Statistics", "String day: " + day);
        Log.d("Statistics", "DAY NUM: " + dayNum);
        String xVal = day + "-" + (month + 1);
        data.add(new Data(dayNum, (float)avgHumid / counter, xVal));

        return data;
    }

    private BarData generateBarData(List<Data> params) {
        //List<Data> params = getData();
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < params.size(); i++) {

            Data d = params.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);
        }

        BarDataSet set1 = new BarDataSet(values, "Влажность воздуха");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f;
        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);

        return d;
    }

    /**
     * Demo class representing data.
     */
    private class Data {

        final String xAxisValue;
        final float yValue;
        final float xValue;

        Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

    public Params getItem(int position, List<Params> items) {
        return (items != null && !items.isEmpty()) ? items.get(position) : null;
    }

    /*public List<Params> getItems() {
        return mItems;
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
