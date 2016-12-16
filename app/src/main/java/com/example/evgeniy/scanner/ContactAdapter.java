package com.example.evgeniy.scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class ContactAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final ArrayList<Person> mDataSource;

    ContactAdapter(Context context, ArrayList<Person> items) {
        mDataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_contact, parent, false);

        TextView titleTextView = (TextView) rowView.findViewById(R.id.contact_list_title);
        TextView subtitleTextView = (TextView) rowView.findViewById(R.id.contact_list_subtitle);
        // TODO: what do we want to use detail text for?
        TextView detailTextView = (TextView) rowView.findViewById(R.id.contact_list_detail);
        ImageView thumbnailImageView = (ImageView) rowView.findViewById(R.id.contact_list_thumbnail);

        Person person = (Person) getItem(position);

        titleTextView.setText(person.getFullName());
        subtitleTextView.setText(person.getPhone());
        thumbnailImageView.setImageResource(R.drawable.dicaprio);

        return rowView;
    }
}
