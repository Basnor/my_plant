package com.example.my_plant.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.BluetoothSPP;
import com.example.bluetooth.BluetoothState;
import com.example.bluetooth.DeviceList;
import com.example.my_plant.R;
import com.example.my_plant.TypeListActivity;

import database.DBCollection;
import model.Collection;

public class FragmentAdd extends Fragment {

    protected FragmentActivity mActivity;

    private BluetoothSPP bt;

    TextView txtDevise;
    TextView txtType;

    private DBCollection mDBCollection;
    private Collection mSelectedCollection;
    public static final int REQUEST_CODE_CHOOSE_COLLECTION = 444;

    public FragmentAdd() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle("Добавить растение");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.mDBCollection = new DBCollection(mActivity);

        View v = getView();
        assert v != null;

        txtDevise = v.findViewById(R.id.device);
        txtType = v.findViewById(R.id.type);
        txtDevise.setText("");
        txtType.setText("");

        bt = new BluetoothSPP(mActivity);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getActivity().getApplicationContext()
                    , "Попробуй обновить соединение"
                    , Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        Button btnChooseDevise = v.findViewById(R.id.btn_choose_devise);
        btnChooseDevise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

            }
        });

        Button btnChooseType = v.findViewById(R.id.btn_choose_type);
        btnChooseType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TypeListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_COLLECTION);
            }
        });

        Button btnContinue = v.findViewById(R.id.btnAdd);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO создаем запись в бд

                // TODO отправляем address, plant_id в SharedPref

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity){
            mActivity =(AppCompatActivity) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDBCollection.close();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bt.connect(data);

                String address = data.getExtras().getString(BluetoothState.DEVICE_ADDRESS);

                //TODO записать адрес в preferanses и БД
                //((MainActivity) getActivity()).address = address;
                txtDevise.setText(address);

            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("CONNECTION", "MAKE REQUEST AUTO CONNECT");
                bt.setupService();
            } else {
                Toast.makeText(getActivity().getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_CHOOSE_COLLECTION) {
            if (resultCode == Activity.RESULT_OK) {

                long id_collection = data.getExtras().getLong(TypeListActivity.EXTRA_SELECTED_COLLECTION_ID);
                Collection currentCollection = mDBCollection.getCollectionById(id_collection);

                txtType.setText(currentCollection.getTypeName());
                //TODO записать адрес в preferanses и БД

            }
        }

    }

}
