package com.example.montgomery_jack_s2429631;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.montgomery_jack_s2429631.exchangeItem;

import java.util.List;

public class searchCodeAdapter extends ArrayAdapter<exchangeItem> {

    public searchCodeAdapter(@NonNull Context context, @NonNull List<exchangeItem> items) {
        super(context, android.R.layout.simple_dropdown_item_1line, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView text = view.findViewById(android.R.id.text1);

        exchangeItem item = getItem(position);

        // Show ANY property you want
        if (item != null) {
            text.setText(item.getCountryCode() + ": " + item.getCountry());   // ← change to whatever you want
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}