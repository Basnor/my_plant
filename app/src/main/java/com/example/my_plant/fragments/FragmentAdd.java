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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.BluetoothSPP;
import com.example.bluetooth.BluetoothState;
import com.example.bluetooth.DeviceList;
import com.example.my_plant.PersistentStorage;
import com.example.my_plant.R;
import com.example.my_plant.TypeListActivity;

import database.DBCollection;
import database.DBProfile;
import model.Collection;
import model.Profile;

public class FragmentAdd extends Fragment {

    private static final String TAG = FragmentAdd.class.getSimpleName();

    protected FragmentActivity mActivity;

    private BluetoothSPP bt;

    private EditText name;
    TextView txtType;
    Collection mCollection;
    TextView txtDevise;
    String address_profile;

    private DBCollection mDBCollection;
    private DBProfile mDBProfile;
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
        this.mDBProfile = new DBProfile(mActivity);

        View v = getView();
        assert v != null;

        name = v.findViewById(R.id.edit_profile_name);
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

        Button btnAdd = v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (isEmpty(name) || isEmpty(txtType) || isEmpty(txtDevise) || address_profile.isEmpty() || (mCollection == null)) {
                    Toast.makeText(getActivity().getApplicationContext()
                            , "Не все поля заполнены."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                String name_profile = name.getText().toString();

                // add the profile to database
                Profile createdProfile = mDBProfile.createProfile(name_profile, mCollection.getId(), address_profile);
                Log.d(TAG, "added profile : "+ createdProfile.getName());

                // fill PersistentStorage to get new fields/params of mainFragment
                PersistentStorage.addLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY, createdProfile.getId());
                PersistentStorage.addLongProperty(PersistentStorage.UPDATE_TIME_KEY, (long) 0);
                PersistentStorage.addLongProperty(PersistentStorage.WATER_TIME_KEY, (long) 0);
                PersistentStorage.addIntProperty(PersistentStorage.HUMIDITY_KEY, 0);
                PersistentStorage.addIntProperty(PersistentStorage.TEMPERATURE_KEY, 0);
                PersistentStorage.addIntProperty(PersistentStorage.LIGHT_KEY, 0);

/*
                Toast.makeText(getApplicationContext()
                        , "Создан новый тип растения."
                        , Toast.LENGTH_SHORT).show();*/

                // переключиться на mainFragment
                FragmentMain fragment = new FragmentMain();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();

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

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDBCollection.close();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private boolean isEmpty(TextView textView) {
        return textView.getText().length() == 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bt.connect(data);
                }catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext()
                            , "Connection failed."
                            , Toast.LENGTH_SHORT).show();
                }


                address_profile = data.getExtras().getString(BluetoothState.DEVICE_ADDRESS);

                txtDevise.setText(address_profile);

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
                mCollection = mDBCollection.getCollectionById(id_collection);

                txtType.setText(mCollection.getTypeName());

            }
        }

    }

}
