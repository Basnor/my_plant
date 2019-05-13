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

import com.example.my_plant.DeleteProfileActivity;
import com.example.my_plant.DeleteTypeActivity;
import com.example.my_plant.HumidityChartConstruction;
import com.example.my_plant.LightChartConstruction;
import com.example.my_plant.R;
import com.example.my_plant.TemperatureChartConstruction;
import com.example.my_plant.WaterListHistory;

public class FragmentSettings extends Fragment {
    protected FragmentActivity mActivity;


    public FragmentSettings() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle("Настройки");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View v = getView();
        assert v != null;

        Button btnDeleteType = v.findViewById(R.id.btn_delete_type);
        btnDeleteType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DeleteTypeActivity.class);
                startActivity(intent);
            }
        });

        Button btnDeleteProfile = v.findViewById(R.id.btn_delete_profile);
        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DeleteProfileActivity.class);
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
}
