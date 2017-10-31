package com.example.user.shopsap;

/**
 * Created by user on 30.10.2017.
 */

import android.graphics.drawable.BitmapDrawable;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by user on 18.7.2017.
 */

public class CustomAdapter extends BaseAdapter {

    private Context ctx;
    private String[] contentArray;

    public CustomAdapter(Context context, int resource, String[] objects) {
        this.ctx = context;
        this.contentArray = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.spinnerTextView);
        textView.setText(contentArray[position]);
        return row;
    }

    @Override
    public int getCount() {
        return contentArray.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
