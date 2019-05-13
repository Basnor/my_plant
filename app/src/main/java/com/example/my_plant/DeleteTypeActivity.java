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
import model.Collection;

public class DeleteTypeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = "TypeListActivity";

    private ListView mListViewCollection;
    private TextView mTxtEmptyListCollection;

    private ListTypeAdapter mAdapter;
    private List<Collection> mListCollection;
    private DBCollection mDBCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_collections);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize views
        initViews();

        // fill the listView
        mDBCollection = new DBCollection(this);
        mListCollection = mDBCollection.getAllCollection();
        if(mListCollection != null && !mListCollection.isEmpty()) {
            mAdapter = new ListTypeAdapter(this, mListCollection);
            mListViewCollection.setAdapter(mAdapter);
        }
        else {
            mTxtEmptyListCollection.setVisibility(View.VISIBLE);
            mListViewCollection.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        this.mListViewCollection = findViewById(R.id.list_collection);
        this.mTxtEmptyListCollection = findViewById(R.id.txt_empty_list_collection);
        this.mListViewCollection.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBCollection.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Collection clickedCollection = mAdapter.getItem(position);
        Log.d(TAG, "clickedItem : "+clickedCollection.getTypeName());

        showDeleteDialogConfirmation(clickedCollection);

    }

    private void showDeleteDialogConfirmation(final Collection collection) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder
                .setMessage("Ты уверен, что хочешь удалить \""
                        + collection.getTypeName() + "\" тип растения и все его профили и параметры?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the employee and refresh the list
                if(mDBCollection != null) {
                    mDBCollection.deleteCollection(collection);

                    //refresh the listView
                    mListCollection.remove(collection);
                    if(mListCollection.isEmpty()) {
                        mListViewCollection.setVisibility(View.GONE);
                        mTxtEmptyListCollection.setVisibility(View.VISIBLE);
                    }

                    mAdapter.setItems(mListCollection);
                    mAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(DeleteTypeActivity.this, "Тип удален успешно", Toast.LENGTH_SHORT).show();

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