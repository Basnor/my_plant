package com.example.my_plant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import database.DBParams;

@SuppressLint("Registered")
public class WaterListHistory extends AppCompatActivity {

    protected final String[] months = new String[]{
            "Январь", "Февраль", "Март", "Апрель", "Май",
            "Июнь", "Июль", "Август", "Сентябрь", "Октябрь",
            "Ноябрь", "Декабрь"
    };

    private ListView mListViewWater;
    private TextView mTxtEmptyListWater;

    DBParams mDBParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize views
        initViews();

        // fill the listView
        if ( !getWaterList().isEmpty() ) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getWaterList());
            mListViewWater.setAdapter(adapter);
        } else {
            mTxtEmptyListWater.setVisibility(View.VISIBLE);
            mListViewWater.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        this.mListViewWater = findViewById(R.id.list_watering);
        this.mTxtEmptyListWater = findViewById(R.id.txt_empty_list_water);
    }

    private List<String> getWaterList() {
        final List<String> StrWaterList = new ArrayList<>();

        mDBParams = new DBParams(this);
        List<Long> datesUnix = mDBParams.getWaterDatesToProfile(PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY));

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date date;
        int day;
        int month;

        for (int index = 0; index < datesUnix.size(); index++) {

            date = new Date(getItem(index, datesUnix));
            cal.setTime(date);
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);

            String s = months[month % months.length];
            s += ", " + day;
            StrWaterList.add(s);
        }

        return StrWaterList;
    }

    public Long getItem(int position, List<Long> items) {
        return (items != null && !items.isEmpty()) ? items.get(position) : null;
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
