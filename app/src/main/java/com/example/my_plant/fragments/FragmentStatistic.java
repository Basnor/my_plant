package com.example.my_plant.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.my_plant.HumidityChartConstruction;
import com.example.my_plant.LightChartConstruction;
import com.example.my_plant.R;
import com.example.my_plant.TemperatureChartConstruction;
import com.example.my_plant.WaterListHistory;

public class FragmentStatistic extends Fragment {
    protected FragmentActivity mActivity;

    public FragmentStatistic() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle("Статистика");
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

        Button btnHumidChart = v.findViewById(R.id.btn_humidity_chart);
        btnHumidChart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HumidityChartConstruction.class);
                startActivity(intent);
            }
        });

        Button btnTempChart = v.findViewById(R.id.btn_temperature_chart);
        btnTempChart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TemperatureChartConstruction.class);
                startActivity(intent);
            }
        });

        Button btnLightChart = v.findViewById(R.id.btn_light_chart);
        btnLightChart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), LightChartConstruction.class);
                startActivity(intent);
            }
        });

        Button btnWaterList = v.findViewById(R.id.btn_water_list);
        btnWaterList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), WaterListHistory.class);
                startActivity(intent);
            }
        });

    }

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
