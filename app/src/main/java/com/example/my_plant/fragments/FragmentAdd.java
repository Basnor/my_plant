package com.example.my_plant.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.BluetoothState;
import com.example.bluetooth.DeviceList;
import com.example.my_plant.MainActivity;
import com.example.my_plant.R;

public class FragmentAdd extends Fragment {

    String[] plantType = {"алое", "кактус", "гладиолус"};
    TextView dummyTextView;

    public FragmentAdd() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dummyTextView = getView().findViewById(R.id.device);
        dummyTextView.setText("");

        Button btnConnect = getView().findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((MainActivity) getActivity()).bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    ((MainActivity) getActivity()).bt.disconnect();
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

            }
        });

        Button btnContinue = getView().findViewById(R.id.btnContinue);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO создаем запись в бд

                // TODO отправляем address в SharedPref

            }
        });

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, plantType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = getView().findViewById(R.id.spinnerType);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Название");

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        dummyTextView.setText("2");
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                ((MainActivity) getActivity()).bt.connect(data);

                String address = data.getExtras().getString(BluetoothState.DEVICE_ADDRESS);

                ((MainActivity) getActivity()).address = address;
                dummyTextView.setText(address);

            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("CONNECTION", "MAKE REQUEST AUTO CONNECT");
                ((MainActivity) getActivity()).bt.setupService();
            } else {
                Toast.makeText(getActivity().getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

}
