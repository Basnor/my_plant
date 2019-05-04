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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.BluetoothSPP;
import com.example.bluetooth.BluetoothState;
import com.example.my_plant.MsgExchange;
import com.example.my_plant.PersistentStorage;
import com.example.my_plant.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import database.DBCollection;
import database.DBProfile;
import model.Collection;
import model.Profile;

public class FragmentMain extends Fragment {
    protected FragmentActivity mActivity;

    private BluetoothSPP bt;
    private MsgExchange msgExchange;
    public static String address;
    int reqNum = -1;

    TextView txtHumidity, txtTemp, txtUpdateTime, txtLight, txtWatering, txtName, txtType;
    LinearLayout lHumidity, lTemperature, lLight;
    private TextView mTxtEmptyListProfile;
    private LinearLayout mLinearLayout;

    private Profile mProfile;
    private DBProfile mDBProfile;

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity.setTitle("Главная");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();

        mDBProfile = new DBProfile(mActivity);
        List<Profile> mListProfile = mDBProfile.getAllProfile();

        if (mListProfile != null && !mListProfile.isEmpty()) {

        } else {
            mTxtEmptyListProfile.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        }

        putParamsValue();

        Button btnUpdate = getView().findViewById(R.id.btn_init);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reqNum = msgExchange.REQ_INIT;
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
                Log.d("Check", "Knew about putParamsValue() ");
                putParamsValue();
            }

        });

    }

    private void initViews() {
        View v = getView();
        assert v != null;

        txtName = v.findViewById(R.id.txt_name);
        txtType = v.findViewById(R.id.txt_type);

        txtHumidity = v.findViewById(R.id.value_humidity);
        txtTemp = v.findViewById(R.id.value_temperature);
        txtUpdateTime = v.findViewById(R.id.value_last_refresh_time);
        txtLight = v.findViewById(R.id.value_light);
        txtWatering = v.findViewById(R.id.value_last_water_time);

        mTxtEmptyListProfile = v.findViewById(R.id.txt_empty_main);
        mLinearLayout = v.findViewById(R.id.txt_main);

        lHumidity = v.findViewById(R.id.humidity);
        lTemperature = v.findViewById(R.id.temperature);
        lLight = v.findViewById(R.id.light);

    }

    private void putParamsValue() {

        Profile profile = mDBProfile.getProfileById(PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY));
        txtName.setText(profile.getName());
        txtType.setText(profile.getCollection().getTypeName());

        if (PersistentStorage.getIntProperty(PersistentStorage.HUMIDITY_KEY) == 0 ||
                PersistentStorage.getIntProperty(PersistentStorage.TEMPERATURE_KEY) == 0 ||
                PersistentStorage.getIntProperty(PersistentStorage.LIGHT_KEY) == 0) {
            txtHumidity.setText("");
            txtTemp.setText("");
            txtLight.setText("");
            txtUpdateTime.setText("");
            txtWatering.setText("");
            lHumidity.setBackgroundResource(R.drawable.boarder_none);
            lTemperature.setBackgroundResource(R.drawable.boarder_none);
            lLight.setBackgroundResource(R.drawable.boarder_none);

            return;
        }

        String formattedStr = getString(R.string.value_humidity,
                PersistentStorage.getIntProperty(PersistentStorage.HUMIDITY_KEY), "%");
        txtHumidity.setText(formattedStr);
        formattedStr = getString(R.string.value_temperature,
                PersistentStorage.getIntProperty(PersistentStorage.TEMPERATURE_KEY));
        txtTemp.setText(formattedStr);
        formattedStr = getString(R.string.value_light,
                PersistentStorage.getIntProperty(PersistentStorage.LIGHT_KEY), "%");
        txtLight.setText(formattedStr);

        //время обновления
        SimpleDateFormat sdf_pattern = new SimpleDateFormat("dd-MM HH:mm");
        long unixTime = PersistentStorage.getLongProperty(PersistentStorage.UPDATE_TIME_KEY);
        Date curTime = new Date(unixTime);
        txtUpdateTime.setText(sdf_pattern.format(curTime));

        //дата полива
        sdf_pattern = new SimpleDateFormat("dd-MM");
        unixTime = PersistentStorage.getLongProperty(PersistentStorage.WATER_TIME_KEY);
        curTime = new Date(unixTime);
        long zero = new Date(0).getTime();
        if (unixTime == zero) {
            txtWatering.setText("");
        }else txtWatering.setText(sdf_pattern.format(curTime));


        int main_humidity = profile.getCollection().getHumidity();
        int diff = 100 - 100*PersistentStorage.getIntProperty(PersistentStorage.HUMIDITY_KEY)/main_humidity;
        if(diff < 8){
            lHumidity.setBackgroundResource(R.drawable.boarder_green);
        } else if (diff < 15) {
            lHumidity.setBackgroundResource(R.drawable.boarder_yellow);
        } else lHumidity.setBackgroundResource(R.drawable.boarder_red);


        int main_temperature = profile.getCollection().getTemperature();
        diff = 100 - 100*PersistentStorage.getIntProperty(PersistentStorage.TEMPERATURE_KEY)/main_temperature;
        if(diff < 8){
            lTemperature.setBackgroundResource(R.drawable.boarder_green);
        } else if (diff < 15) {
            lTemperature.setBackgroundResource(R.drawable.boarder_yellow);
        } else lTemperature.setBackgroundResource(R.drawable.boarder_red);


        int main_light = profile.getCollection().getLight();
        diff = 100 - 100*PersistentStorage.getIntProperty(PersistentStorage.LIGHT_KEY)/main_light;
        if(diff < 8){
            lLight.setBackgroundResource(R.drawable.boarder_green);
        } else if (diff < 15) {
            lLight.setBackgroundResource(R.drawable.boarder_yellow);
        } else lLight.setBackgroundResource(R.drawable.boarder_red);
        //lLight.setBackgroundResource(R.drawable.boarder_green);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
        mDBProfile.close();

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
                long id_profile = PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY);
                mProfile = mDBProfile.getProfileById(id_profile);

                address = mProfile.getAddress();
                if (!address.equals(""))
                    bt.connect(address);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.on_off_led, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_led:
                reqNum = msgExchange.REQ_BURN;
                msgExchange.sendGraph();
                reqNum++;

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
