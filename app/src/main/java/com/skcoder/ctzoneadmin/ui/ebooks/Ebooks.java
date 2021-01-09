package com.skcoder.ctzoneadmin.ui.ebooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skcoder.ctzoneadmin.Dasboard;
import com.skcoder.ctzoneadmin.R;
import com.skcoder.ctzoneadmin.ui.notices.notices;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Ebooks extends AppCompatActivity {

    CardView selectEbook;
    EditText ebookTitle;
    Button uploadEbookBtn;
    TextView pdtTextView;
    Uri filePath;
    String pdfName, title;
    String downloadUrl="";

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebooks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Upload Ebooks");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        selectEbook = findViewById(R.id.selectEbook);
        pdtTextView = findViewById(R.id.pdf_textView);
        ebookTitle = findViewById(R.id.ebookTitle);
        uploadEbookBtn = findViewById(R.id.uploadEbookBtn);

        pd = new ProgressDialog(this);

        selectEbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadEbookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = ebookTitle.getText().toString().trim();
                if (title.isEmpty()){
                    ebookTitle.setError("Please give the title");
                    ebookTitle.requestFocus();
                }else if (filePath==null){
                    Toast.makeText(Ebooks.this, "Please select the PDF", Toast.LENGTH_SHORT).show();
                }else {
                    pdfUpload();
                }
            }
        });
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            filePath = data.getData();

            //Toast.makeText(this, "PDF Selected", Toast.LENGTH_SHORT).show();

            if (filePath.toString().startsWith("content://")) {
                Cursor cursor=null;
                try {
                    cursor = Ebooks.this.getContentResolver().query(filePath, null,null,null,null);

                    if (cursor != null && cursor.moveToFirst())
                    {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if (filePath.toString().startsWith("file://")) {
                pdfName = new File(filePath.toString()).getName();
            }

            pdtTextView.setText(pdfName);
        }
    }

    public void pdfUpload(){
        pd.setTitle("Please wait...");
        pd.setMessage("Uploading eBook");
        pd.show();
        StorageReference reference= storageReference.child("Ebooks/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        uploadData(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Ebooks.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(String downloadUrl) {
        String uniqueKey = databaseReference.child("Ebooks").push().getKey();
        HashMap data = new HashMap();
        data.put("Title", title);
        data.put("url", downloadUrl);

        databaseReference.child("Ebooks").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(Ebooks.this, "eBook Uploaded Successfully", Toast.LENGTH_SHORT).show();
                ebookTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Ebooks.this, "Failed to upload PDF", Toast.LENGTH_SHORT).show();
            }
        });

    }


}