package com.example.tpcontact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Contact {
    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return name + " (" + phoneNumber + ")";
    }

    public static class ContactAdapter extends ArrayAdapter<Contact> {

        public ContactAdapter(Context context, ArrayList<Contact> contacts) {
            super(context, 0, contacts);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            Contact contact = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        android.R.layout.simple_list_item_2, parent, false);
            }

            // Lookup view for data population
            TextView tvName = convertView.findViewById(android.R.id.text1);
            TextView tvPhone = convertView.findViewById(android.R.id.text2);

            // Populate the data into the template view using the data object
            tvName.setText(contact.getName());
            tvPhone.setText(contact.getPhoneNumber());

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
