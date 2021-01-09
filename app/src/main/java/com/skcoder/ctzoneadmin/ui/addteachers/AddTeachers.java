package com.skcoder.ctzoneadmin.ui.addteachers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skcoder.ctzoneadmin.Dasboard;
import com.skcoder.ctzoneadmin.R;
import com.skcoder.ctzoneadmin.ui.notices.NoticeData;
import com.skcoder.ctzoneadmin.ui.notices.notices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeachers extends AppCompatActivity {

    CircleImageView profileImage;
    EditText name, email, post;
    private Bitmap bitmap;
    Button addTeacher;
    private final int REQ = 1;
    private String Category, teacherName, teacherEmail, teacherPost;
    private Spinner teacherDepartment;

    String downloadUrl;

    private DatabaseReference reference;
    private StorageReference storageReference;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teachers);
        this.setTitle("Add Teacher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        name = (EditText) findViewById(R.id.et_name);
        email = (EditText) findViewById(R.id.et_email);
        post = (EditText) findViewById(R.id.et_post);
        teacherDepartment = findViewById(R.id.t_category);
        addTeacher = (Button) findViewById(R.id.addteacher_btn);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference().child("Teachers");

        pd = new ProgressDialog(this);

        String[] items = new String[]{"Select Department", "Computer Science Department", "Machenical Department", "Civil Department", "Electrical Department", "Automobile Department", "ECE Department"};
        teacherDepartment.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));

        teacherDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category = teacherDepartment.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkvalidation();
            }
        });

    }

    private void checkvalidation() {
        teacherName = name.getText().toString();
        teacherEmail = email.getText().toString();
        teacherPost = post.getText().toString();

        if (teacherName.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
        } else if (teacherEmail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
        } else if (teacherPost.isEmpty()) {
            post.setError("Post is required");
            post.requestFocus();
        } else if (Category.equals("Select Department")) {
            Toast.makeText(this, "Please select the teacher category", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            pd.setMessage("Uploading...");
            pd.show();
            insertData();
        } else {
            pd.setMessage("Uploading...");
            pd.show();

            insertImage();
        }

    }

    private void insertImage() {
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child(finalimg + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(AddTeachers.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    insertData();
                                }
                            });
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(AddTeachers.this, "Something Went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertData() {
        reference = reference.child(Category);
        final String uniquekey = reference.push().getKey();
        String purl = downloadUrl;
        String txtName = name.getText().toString();
        String txtEmail = email.getText().toString();
        String txtPost = post.getText().toString();

        HashMap hashMap = new HashMap();
        hashMap.put("purl", purl);
        hashMap.put("name", txtName);
        hashMap.put("email", txtEmail);
        hashMap.put("post", txtPost);
        hashMap.put("key", uniquekey);

        reference.child(uniquekey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(AddTeachers.this, "Data Uploaded Successfuly", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UpdateFaculty.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddTeachers.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            profileImage.setImageBitmap(bitmap);
        }
    }


}