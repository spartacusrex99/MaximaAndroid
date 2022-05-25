package com.minima.maximaandroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContactAdapter extends ArrayAdapter<Contact> {

    Context mContext;

    Contact[] mContacts;

    public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH );

    public ContactAdapter(@NonNull Context zContext, int resource, @NonNull Contact[] objects) {
        super(zContext, resource, objects);
        mContext    = zContext;
        mContacts   = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.contact_view, null);
        }

        Contact contact = getItem(position);

        TextView name        = v.findViewById(R.id.contact_name);
        TextView lastseen    = v.findViewById(R.id.contact_lastseen);
        TextView statustime  = v.findViewById(R.id.contact_status_time);
        TextView statuschain = v.findViewById(R.id.contact_status_chain);

        name.setText(contact.mName);
        lastseen.setText( DATEFORMAT.format(new Date(contact.mLastSeen)));

        if(contact.getTimeStatus()){
            statustime.setTextColor(Color.parseColor("#00FF00"));
        }else{
            statustime.setTextColor(Color.parseColor("#FF0000"));
        }

        if(contact.getChainStatus()){
            statuschain.setTextColor(Color.parseColor("#00FF00"));
        }else{
            statuschain.setTextColor(Color.parseColor("#FF0000"));
        }

        //Set some Text..
        statustime.setText("NETWORK");
        statuschain.setText("CHAIN");

        return v;
    }
}
