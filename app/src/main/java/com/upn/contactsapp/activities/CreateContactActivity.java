package com.upn.contactsapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upn.contactsapp.AppDatabase;
import com.upn.contactsapp.R;
import com.upn.contactsapp.daos.ContactDAO;
import com.upn.contactsapp.entities.Contact;
import com.upn.contactsapp.services.ContactService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateContactActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private String imageBase64;
    private RecyclerView recyclerViewContacts;
    private ContactAdapter adapter; // Crea este adaptador para manejar la lista de contactos
    private List<Contact> contacts = new ArrayList<>();
    private int currentPage = 1;
    private final int LIMIT = 10;
    private ContactService page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        ivPhoto = findViewById(R.id.ivPhoto);
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        setupRecyclerView();
        setUpBtnTakePhoto();
        setUpBtnChoosePhoto();


        AppDatabase db = AppDatabase.getInstance(this);
        ContactDAO contactDAO = db.contactDAO();

        Button btnGuardarContacto = findViewById(R.id.btnGuardarContacto);
        EditText etName = findViewById(R.id.etName);
        EditText etPhone = findViewById(R.id.etPhone);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66d5b903f5859a7042673752.mockapi.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ContactService service = retrofit.create(ContactService.class);
        loadContacts(currentPage, page);

        btnGuardarContacto.setOnClickListener(view -> {

            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();

            Contact contact = new Contact(name, phone);
            contact.image = imageBase64;

            contact.localId = (int) contactDAO.insert(contact);

            Log.i("CONTACT_LOCAL_ID",  String.valueOf(contact.localId));
        });
        setupPaginationButtons(service, page);
    }

    private void setupPaginationButtons(ContactService service, Object page) {
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadContacts(currentPage, (ContactService) page);
            }
        });

        btnNext.setOnClickListener(v -> {
            currentPage++;
            loadContacts(currentPage, (ContactService) page);
        });
    }


    private void loadContacts(int page, ContactService retrofit) {
        ContactService service = (ContactService)  retrofit.create(ContactService.class);
        service.getContacts(page, LIMIT).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contacts.clear(); // Limpiar la lista antes de agregar nuevos contactos
                    contacts.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.e("MAIN_APP", t.getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(contacts); // Asegúrate de crear este adaptador
        recyclerViewContacts.setAdapter(adapter);
    }

    private void setUpBtnChoosePhoto() {
        Button btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnChoosePhoto.setOnClickListener(view -> {
            openPhotoGallery();
        });
    }

    private void setUpBtnTakePhoto() {
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(view -> {
            // preguntar si tiene permisos para abrir la camara
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // abrir camara
                openCamera();
            } else {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    private void openPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            ivPhoto.setImageBitmap(imageBitmap);
        } if( requestCode == 101&& resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            ivPhoto.setImageURI(selectedImage);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
