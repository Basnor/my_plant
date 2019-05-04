package com.example.my_plant.fragments;

import android.content.Context;;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.my_plant.ListProfileAdapter;
import com.example.my_plant.PersistentStorage;
import com.example.my_plant.R;

import java.util.List;

import database.DBProfile;
import model.Profile;

public class FragmentProfileList extends Fragment {

    private static final String TAG = FragmentProfileList.class.getSimpleName();

    protected FragmentActivity mActivity;

    private ListView mListViewProfile;
    private TextView mTxtEmptyListProfile;

    private ListProfileAdapter mAdapter;
    private List<Profile> mListProfile;
    private DBProfile mDBProfile;

    public FragmentProfileList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle("Выбрать растение");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();

        // fill the listView
        mDBProfile = new DBProfile(mActivity);
        mListProfile = mDBProfile.getAllProfile();
        if (mListProfile != null && !mListProfile.isEmpty()) {
            mAdapter = new ListProfileAdapter(mActivity, mListProfile);
            mListViewProfile.setAdapter(mAdapter);
        } else {
            mTxtEmptyListProfile.setVisibility(View.VISIBLE);
            mListViewProfile.setVisibility(View.GONE);
        }

    }

    private void initViews() {
        View v = getView();
        assert v != null;

        this.mTxtEmptyListProfile = v.findViewById(R.id.txt_empty_list_profile);
        this.mListViewProfile = v.findViewById(R.id.list_profile);
        this.mListViewProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Profile clickedProfile = mAdapter.getItem(position);
                Log.d(TAG, "clickedItem : " + clickedProfile.getName());

                // fill PersistentStorage to get new fields/params of mainFragment
                PersistentStorage.addLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY, clickedProfile.getId());

                //TODO найти последнюю запись в БД

                // нулевые значения (если бд Params пуста)
                PersistentStorage.addLongProperty(PersistentStorage.UPDATE_TIME_KEY, (long) 0);
                PersistentStorage.addLongProperty(PersistentStorage.WATER_TIME_KEY, (long) 0);
                PersistentStorage.addIntProperty(PersistentStorage.HUMIDITY_KEY, 0);
                PersistentStorage.addIntProperty(PersistentStorage.TEMPERATURE_KEY, 0);
                PersistentStorage.addIntProperty(PersistentStorage.LIGHT_KEY, 0);

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
    public void onDestroy() {
        super.onDestroy();
        mDBProfile.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) context;
        }
    }

}
