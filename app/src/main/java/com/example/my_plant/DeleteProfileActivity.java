package com.example.my_plant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import database.DBCollection;
import database.DBProfile;
import model.Collection;
import model.Profile;

public class DeleteProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = "TypeListActivity";

    private ListView mListViewProfile;
    private TextView mTxtEmptyListProfile;

    private ListProfileAdapter mAdapter;
    private List<Profile> mListProfile;
    private DBProfile mDBProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize views
        initViews();

        // fill the listView
        mDBProfile = new DBProfile(this);
        mListProfile = mDBProfile.getAllProfile();
        if (mListProfile != null && !mListProfile.isEmpty()) {
            mAdapter = new ListProfileAdapter(this, mListProfile);
            mListViewProfile.setAdapter(mAdapter);
        } else {
            mTxtEmptyListProfile.setVisibility(View.VISIBLE);
            mListViewProfile.setVisibility(View.GONE);
        }

    }

    private void initViews() {
        this.mTxtEmptyListProfile = findViewById(R.id.txt_empty_list_profile);
        this.mListViewProfile = findViewById(R.id.list_profile);
        this.mListViewProfile.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBProfile.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Profile clickedProfile = mAdapter.getItem(position);
        Log.d(TAG, "clickedItem : " + clickedProfile.getName());

        showDeleteDialogConfirmation(clickedProfile);

    }

    private void showDeleteDialogConfirmation(final Profile profile) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder
                .setMessage("Ты уверен, что хочешь удалить профиль \""
                        + profile.getName() + "\" и все его параметры?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the employee and refresh the list
                if(mDBProfile != null) {
                    mDBProfile.deleteProfile(profile);

                    //refresh the listView
                    mListProfile.remove(profile);
                    if(mListProfile.isEmpty()) {
                        mListViewProfile.setVisibility(View.GONE);
                        mTxtEmptyListProfile.setVisibility(View.VISIBLE);
                    }

                    mAdapter.setItems(mListProfile);
                    mAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(DeleteProfileActivity.this, "Профиль удален успешно", Toast.LENGTH_SHORT).show();

            }
        });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
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