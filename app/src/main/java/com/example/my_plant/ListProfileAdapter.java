package com.example.my_plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import model.Profile;

public class ListProfileAdapter extends BaseAdapter {

    public static final String TAG = "ListProfileAdapter";

    private List<Profile> mItems;
    private LayoutInflater mInflater;

    public ListProfileAdapter(Context context, List<Profile> listProfiles) {
        this.setItems(listProfiles);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
    }

    @Override
    public Profile getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if(v == null) {
            v = mInflater.inflate(R.layout.list_item_profile, parent, false);
            holder = new ViewHolder();
            holder.txtProfileName = v.findViewById(R.id.txt_profile_name);
            holder.txtProfileType = v.findViewById(R.id.txt_profile_type);
            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Profile currentItem = getItem(position);
        if(currentItem != null) {
            holder.txtProfileName.setText(currentItem.getName());
            holder.txtProfileType.setText(currentItem.getCollection().getTypeName());
        }

        return v;
    }

    public List<Profile> getItems() {
        return mItems;
    }

    public void setItems(List<Profile> mItems) {
        this.mItems = mItems;
    }

    class ViewHolder {
        TextView txtProfileName;
        TextView txtProfileType;
    }

}