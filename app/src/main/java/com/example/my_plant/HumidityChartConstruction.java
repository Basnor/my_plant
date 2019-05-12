package com.example.my_plant;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import database.DBParams;
import database.DBProfile;
import model.Params;
import model.Profile;

public class HumidityChartConstruction extends AppCompatActivity {

    CombinedChart chart;

    DBParams mDBParams;
    DBProfile mDBProfile;

    public HumidityChartConstruction() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humid_chart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chart = findViewById(R.id.humidity_chart);
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
        l.setTextSize(12);

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

        final List<HumidityChartConstruction.Data> finalData = getData();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return finalData.get(Math.min((int) value, finalData.size() - 1)).xAxisValue;
            }
        });

        CombinedData cdata = new CombinedData();

        cdata.setData(generateLineData(finalData));
        cdata.setData(generateBarData(finalData));

        xAxis.setAxisMaximum(cdata.getXMax() + 0.25f);
        xAxis.setAxisMinimum(cdata.getXMin() - 0.25f);

        chart.setData(cdata);
        chart.invalidate();
    }

    private List<HumidityChartConstruction.Data> getData() {
        final List<HumidityChartConstruction.Data> data = new ArrayList<>();

        mDBParams = new DBParams(this);
        List<Params> items = mDBParams.getParamsOfProfileChart(PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY));

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        Date date = new Date(getItem(items.size() - 1, items).getDate());
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        int avgHumid = 0;
        int counter = 0;
        int dayNum = 0;

        for (int index = items.size() - 1; index >= 0; index--) {

            date = new Date(getItem(index, items).getDate());
            cal.setTime(date);

            if (cal.get(Calendar.DAY_OF_MONTH) == day) {
                avgHumid += getItem(index, items).getHumidity();
                counter++;

            } else {
                String xVal = formXAxisValue(day, month);
                data.add(new HumidityChartConstruction.Data((float) dayNum, (float) avgHumid / counter, xVal));
                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                counter = 1;
                avgHumid = getItem(index, items).getHumidity();
                dayNum++;
            }

        }

        String xVal = formXAxisValue(day, month);
        data.add(new HumidityChartConstruction.Data(dayNum, (float) avgHumid / counter, xVal));

        return data;
    }

    private String formXAxisValue(int day, int month) {
        String xVal;
        if (month + 1 > 9)
            xVal = day + "-" + (month + 1);
        else xVal = day + "-0" + (month + 1);

        return xVal;
    }

    private BarData generateBarData(List<HumidityChartConstruction.Data> params) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < params.size(); i++) {

            HumidityChartConstruction.Data d = params.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);
        }

        BarDataSet set1 = new BarDataSet(values, "Влажность воздуха за день (%)");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f;
        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);

        return d;
    }

    private LineData generateLineData(List<HumidityChartConstruction.Data> params) {

        mDBProfile = new DBProfile(this);
        Profile profile = mDBProfile.getProfileById(PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY));
        int humid = profile.getCollection().getHumidity();

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < params.size(); i++) {
            HumidityChartConstruction.Data p = params.get(i);
            float val = (float) humid - (float) humid * 8 / 100;

            if (i == 0) {
                values1.add(new Entry(p.xValue - 0.20f, val));
            }

            values1.add(new Entry(p.xValue, val));

            if (i == params.size() - 1) {
                values1.add(new Entry(p.xValue + 0.20f, val));
            }
        }


        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < params.size(); i++) {
            HumidityChartConstruction.Data p = params.get(i);
            float val = (float) humid + (float) humid * 8 / 100;

            if (i == 0) {
                values2.add(new Entry(p.xValue - 0.20f, val));
            }

            values2.add(new Entry(p.xValue, val));

            if (i == params.size() - 1) {
                values2.add(new Entry(p.xValue + 0.20f, val));
            }
        }

        ArrayList<Entry> values3 = new ArrayList<>();

        for (int i = 0; i < params.size(); i++) {
            HumidityChartConstruction.Data p = params.get(i);
            if (i == 0) {
                values3.add(new Entry(p.xValue - 0.20f, humid));
            }

            values3.add(new Entry(p.xValue, humid));

            if (i == params.size() - 1) {
                values3.add(new Entry(p.xValue + 0.20f, humid));
            }
        }

        LineDataSet set1, set2, set3;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values1, "Диапазон нормальной влажности (%)");

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.rgb(255, 80, 80));
            set1.setDrawCircles(false);
            set1.setLineWidth(3f);

            // create a dataset and give it a type
            set2 = new LineDataSet(values2, "Диапазон нормальной влажности (%)");
            set2.setLabel("");

            // draw dashed line
            set2.enableDashedLine(10f, 5f, 0f);
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setColor(Color.rgb(255, 80, 80));
            set2.setDrawCircles(false);
            set2.setLineWidth(3f);
            set2.setFormSize(0.f);


            set3 = new LineDataSet(values3, "Норма влажности за день (%)");
            set3.setColor(Color.rgb(51, 51, 204));
            set3.setLineWidth(3f);
            set3.setDrawCircles(false);
            set3.setAxisDependency(YAxis.AxisDependency.LEFT);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            dataSets.add(set2);
            dataSets.add(set3);

            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            data.setDrawValues(false);

            return data;
        }

        return null;
    }

    public class Data {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}