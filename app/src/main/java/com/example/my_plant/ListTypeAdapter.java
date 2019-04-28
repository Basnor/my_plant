package com.example.my_plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import model.Collection;

public class ListTypeAdapter extends BaseAdapter {

    public static final String TAG = "ListTypeAdapter";

    private List<Collection> mItems;
    private LayoutInflater mInflater;
    Context mContext;

    public ListTypeAdapter(Context context, List<Collection> listCollections) {
        this.mContext = context;
        this.setItems(listCollections);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
    }

    @Override
    public Collection getItem(int position) {
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
            v = mInflater.inflate(R.layout.list_item_collection, parent, false);
            holder = new ViewHolder();
            holder.txtTypeName = v.findViewById(R.id.txt_collection_name);
            holder.txtHumidity = v.findViewById(R.id.txt_collection_humidity);
            holder.txtTemperature = v.findViewById(R.id.txt_collection_temp);
            holder.txtLight = v.findViewById(R.id.txt_collection_light);
            holder.txtWaterPeriod = v.findViewById(R.id.txt_collection_frequency);
            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Collection currentItem = getItem(position);
        if(currentItem != null) {

            String formattedStr = mContext.getString(R.string.value_humidity,
                    currentItem.getHumidity(), "%");
            holder.txtHumidity.setText(formattedStr);
            formattedStr = mContext.getString(R.string.value_temperature,
                    currentItem.getTemperature());
            holder.txtTemperature.setText(formattedStr);
            formattedStr = mContext.getString(R.string.value_light,
                    currentItem.getLight(), "%");
            holder.txtLight.setText(formattedStr);
            formattedStr = mContext.getString(R.string.value_freq,
                    currentItem.getWaterPeriod());
            holder.txtWaterPeriod.setText(formattedStr);

            holder.txtTypeName.setText(currentItem.getTypeName());
        }

        return v;
    }

    public List<Collection> getItems() {
        return mItems;
    }

    public void setItems(List<Collection> mItems) {
        this.mItems = mItems;
    }

    class ViewHolder {
        TextView txtTypeName;
        TextView txtHumidity;
        TextView txtTemperature;
        TextView txtLight;
        TextView txtWaterPeriod;
    }

}