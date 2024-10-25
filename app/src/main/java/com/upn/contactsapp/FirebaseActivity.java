package com.upn.contactsapp;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upn.contactsapp.entities.Contact;
import java.util.UUID;

public class FirebaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        Button btn = findViewById(R.id.btnCreateOnFirebase);
        EditText etName = findViewById(R.id.etName);

        btn.setOnClickListener(v -> {
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("N00297543").child("contacts"); //Donde se va guardar la informacion

            DatabaseReference table = myRef.child("contacts");

            //Contact c1 = new Contact("Luis", "12345678");
            //c1.uuid = UUID.randomUUID().toString();
            //Contact c2 = new Contact("Miguel", "123456");
            //c2.uuid = UUID.randomUUID().toString();
            //table.setValue(c1);
            //Log.i("MAIN_APP","Ok");
            String name = etName.getText().toString();
            Contact c3 = new Contact("Maria", "485456");
            c3.uuid = UUID.randomUUID().toString();
            table.child("2").setValue(c3);

            etName.setText("");
        });
    }
}