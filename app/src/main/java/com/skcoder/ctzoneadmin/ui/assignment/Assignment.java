package com.skcoder.ctzoneadmin.ui.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.skcoder.ctzoneadmin.ui.notes.Notes;

import java.io.File;
import java.util.HashMap;

public class Assignment extends AppCompatActivity {
    CardView pdfSelect;
    private TextView pdfTextView;
    EditText subjectName;
    Spinner selectDepartment, selectSemester;
    Button uploadAssignmentBtn;
    private final int REQ = 1;
    private Uri pdfData;
    private String pdfName, title;
    private String DCategory,SCategory ;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    String downloadUrl="";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        this.setTitle("Upload Assignment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);

        pdfSelect = findViewById(R.id.assignment_pdf_select);
        pdfTextView = findViewById(R.id.assignment_pdf_name);
        subjectName = findViewById(R.id.assignment_subject_name);
        selectDepartment = findViewById(R.id.assignment_select_department);
        selectSemester = findViewById(R.id.assignment_select_semester);
        uploadAssignmentBtn = findViewById(R.id.uploadAssignmentBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] department = new String[]{"Select Department", "Computer Science Department", "Machenical Department", "Civil Department", "Electrical Department", "Automobile Department", "ECE Department"};
        selectDepartment.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, department));

        selectDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DCategory = selectDepartment.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String[] semester = new String[]{"Select Semester", "Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6", "Semester 7", "Semester 8"};
        selectSemester.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, semester));

        selectSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SCategory = selectSemester.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        pdfSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        uploadAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkvalidation();
            }
        });


    }

    private void checkvalidation() {
        title = subjectName.getText().toString();
        if (title.isEmpty()) {
            subjectName.setError("Give the title");
            subjectName.requestFocus();
        }else if (pdfData == null){
            Toast.makeText(this, "Please select PDF", Toast.LENGTH_SHORT).show();
        }else if (DCategory.equals("Select Department")) {
            Toast.makeText(this, "Please select the department", Toast.LENGTH_SHORT).show();
        } else if (SCategory.equals("Select Semester")) {
            Toast.makeText(this, "Please select the semester", Toast.LENGTH_SHORT).show();
        } else{
            uploadPdf();
        }
    }

    private void uploadPdf() {
        pd.setTitle("Please wait...");
        pd.setMessage("Uploading Assignment");
        pd.show();
        StorageReference reference = storageReference.child("Assignment/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                Toast.makeText(Assignment.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void uploadData(String downloadUrl) {
        String uniqueKey = databaseReference.child("assignment").push().getKey();
        HashMap data = new HashMap();
        data.put("assignmentUrl", downloadUrl);
        data.put("assignmentTitle", title);
        data.put("department", DCategory);
        data.put("semester", SCategory);

        databaseReference.child("assignment").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Intent intent = new Intent(getApplicationContext(), Dasboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(Assignment.this, "Assignment Uploaded Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Assignment.this, "Failed to upload Assignment !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfData = data.getData();

            //Toast.makeText(this, "PDF Selected", Toast.LENGTH_SHORT).show();

            if (pdfData.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = Assignment.this.getContentResolver().query(pdfData, null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (pdfData.toString().startsWith("file://")) {
                pdfName = new File(pdfData.toString()).getName();
            }

            pdfTextView.setText(pdfName);
        }
    }
}