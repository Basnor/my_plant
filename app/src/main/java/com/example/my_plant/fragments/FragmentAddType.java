package com.example.my_plant.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.my_plant.R;

import database.DBCollection;
import model.Collection;

public class FragmentAddType extends Fragment {

    private static final String TAG = FragmentAddType.class.getSimpleName();

    protected FragmentActivity mActivity;

    private EditText name;
    private EditText humidity;
    private EditText temperature;
    private EditText light;
    private EditText waterPeriod;

    private DBCollection mCollectionDB;

    public FragmentAddType() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        View v = getView();
        assert v != null;

        mCollectionDB = new DBCollection(mActivity);

        name = v.findViewById(R.id.edit_collection_name);
        humidity = v.findViewById(R.id.edit_collection_humidity);
        temperature = v.findViewById(R.id.edit_collection_temperature);
        light = v.findViewById(R.id.edit_collection_light);
        waterPeriod = v.findViewById(R.id.edit_collection_water_period);


        Button btnAddType = v.findViewById(R.id.btn_add_type);
        btnAddType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (isEmpty(name) || isEmpty(humidity) || isEmpty(temperature) || isEmpty(light) || isEmpty(waterPeriod)) {
                    Toast.makeText(getActivity().getApplicationContext()
                            , "Не все поля заполнены."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                String name_collection = name.getText().toString();
                int humidity_collection = Integer.parseInt(humidity.getText().toString());
                int temperature_collection = Integer.parseInt(temperature.getText().toString());
                int light_collection = Integer.parseInt(light.getText().toString());
                int waterPeriod_collection = Integer.parseInt(waterPeriod.getText().toString());


                if (humidity_collection < 20 || humidity_collection > 80 ||
                        temperature_collection > 50 || light_collection > 100 ||
                        waterPeriod_collection == 0) {
                    Toast.makeText(getActivity().getApplicationContext()
                            , "Ошибка в веденных значениях."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                // add the mCollection to database
                Collection createdCollection = mCollectionDB.createCollection(
                        name_collection, humidity_collection,
                        temperature_collection, light_collection, waterPeriod_collection);

                Log.d(TAG, "added mCollection : "+ createdCollection.getTypeName());

                Toast.makeText(getActivity().getApplicationContext()
                        , "Создан новый тип растения."
                        , Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity){
            mActivity =(AppCompatActivity) context;
        }
    }

}
