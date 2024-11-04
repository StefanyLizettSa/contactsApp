package com.upn.contactsapp.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upn.contactsapp.R;
import com.upn.contactsapp.adapters.ContactAdaptar;
import com.upn.contactsapp.entities.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter {
    private List<Contact> contacts;
    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false); // Crea un layout para cada contacto
        return new ContactAdaptar.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        // Configura la imagen si la tienes
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView phoneTextView;

        public ContactViewHolder(View itemView) {
            super(itemView);
        }
    }
}
