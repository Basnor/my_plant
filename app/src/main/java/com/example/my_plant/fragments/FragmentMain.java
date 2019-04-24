package com.example.my_plant.fragments;

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
import com.example.my_plant.MsgExchange;
import com.example.my_plant.PersistentStorage;
import com.example.my_plant.R;

public class FragmentMain extends Fragment {
    protected FragmentActivity mActivity;

    private BluetoothSPP bt;
    private MsgExchange msgExchange;
    public static String address = "98:D3:31:FB:54:46";
    int reqNum = -1;

    TextView txtHumidity, txtTemp, txtUpdateTime, txtLight, txtWatering;

    public FragmentMain() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        View v = getView();
        assert v != null;

        txtHumidity = v.findViewById(R.id.value_humidity);
        txtTemp = v.findViewById(R.id.value_temperature);
        txtUpdateTime = v.findViewById(R.id.value_last_refresh_time);
        txtLight = v.findViewById(R.id.value_light);
        txtWatering = v.findViewById(R.id.value_last_water_time);

        Button btnUpdate = v.findViewById(R.id.btn_init);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reqNum = -1;
                msgExchange.sendGraph();
            }
        });

        bt = new BluetoothSPP(mActivity);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getActivity().getApplicationContext()
                    , "Попробуй обновить соединение"
                    , Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.i("Check", "Received msg: " + message);
                msgExchange.recieveGraph(message);
            }
        });

        msgExchange = new MsgExchange(mActivity);

        msgExchange.setSendStateListener(new MsgExchange.sendStateListener() {
            @Override
            public void setRequest(String msg) {
                bt.send(msg, false);
                Log.d("Check", "Send msg: " + msg);
            }

            @Override
            public int getReqNum() {
                return reqNum;
            }

            @Override
            public void increaseReqNum() {
                reqNum++;
            }

        });

        msgExchange.setAnswerStateListener(new MsgExchange.answerStateListener() {

            @Override
            public void endExchange() {
                txtHumidity.setText(String.valueOf(PersistentStorage.getIntProperty(PersistentStorage.HUMIDITY_KEY)));
                txtTemp.setText(String.valueOf(PersistentStorage.getIntProperty(PersistentStorage.TEMPERATURE_KEY)));
                txtLight.setText(String.valueOf(PersistentStorage.getIntProperty(PersistentStorage.LIGHT_KEY)));
                //txtUpdateTime.setText(PersistentStorage.getLongProperty(PersistentStorage.UPDATE_TIME_KEY));
                //txtWatering.setText(PersistentStorage.getLongProperty(PersistentStorage.WATER_TIME_KEY));
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
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bt.stopService();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bt.getServiceState() != BluetoothState.STATE_CONNECTED) {
            try {
                if (!address.equals(""))
                    bt.connect(address);
            } catch (Exception ignored) {
            }
        }
    }

}
