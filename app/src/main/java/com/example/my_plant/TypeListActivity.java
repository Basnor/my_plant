package com.example.my_plant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import database.DBCollection;
import model.Collection;

@SuppressLint("Registered")
public class TypeListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = "TypeListActivity";

    public static final int REQUEST_CODE_ADD_COLLECTION = 40;
    public static final String EXTRA_ADDED_COLLECTION = "extra_key_added_collection";
    public static final String EXTRA_SELECTED_COLLECTION_ID = "extra_key_choosing_collection";

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ADD_COLLECTION) {
            if(resultCode == RESULT_OK) {
                // add the added collection to the listCollections and refresh the listView
                if(data != null) {
                    Collection createdCollection = (Collection) data.getSerializableExtra(EXTRA_ADDED_COLLECTION);
                    if(createdCollection != null) {
                        if(mListCollection == null)
                            mListCollection = new ArrayList<Collection>();
                        mListCollection.add(createdCollection);

                        if(mAdapter == null) {
                            if(mListViewCollection.getVisibility() != View.VISIBLE) {
                                mListViewCollection.setVisibility(View.VISIBLE);
                                mTxtEmptyListCollection.setVisibility(View.GONE);
                            }

                            mAdapter = new ListTypeAdapter(this, mListCollection);
                            mListViewCollection.setAdapter(mAdapter);
                        }
                        else {
                            mAdapter.setItems(mListCollection);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
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

        // Create the result Intent and include the typeId
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_COLLECTION_ID, clickedCollection.getId());

        // Set result and finish this Activity
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_type_plant, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add_type:
                Intent intent = new Intent(this, AddTypeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_COLLECTION);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
