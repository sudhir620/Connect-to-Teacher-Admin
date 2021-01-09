package com.skcoder.ctzoneadmin.ui.youtubelecture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skcoder.ctzoneadmin.Dasboard;
import com.skcoder.ctzoneadmin.R;

import java.util.HashMap;

public class YoutubeLecture extends AppCompatActivity {

    EditText youtubeLink, lectureTopic;
    Button uploadBtn;
    ProgressDialog pd;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_lecture);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Youtube Lecture");

        youtubeLink = findViewById(R.id.youtube_link);
        lectureTopic = findViewById(R.id.lecture_topic);
        uploadBtn = findViewById(R.id.upload_btn);

        pd = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Youtube_Link");

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void checkValidation() {
        String link = youtubeLink.getText().toString();
        String topic = lectureTopic.getText().toString();
        if (link.isEmpty()){
            youtubeLink.setError("Empty");
            youtubeLink.requestFocus();
        }else if (topic.isEmpty()){
            lectureTopic.setError("Empty");
            lectureTopic.requestFocus();
        }else{
            pd.setMessage("Uploading...");
            pd.show();

            final String uniqueKey = reference.push().getKey();
            HashMap hashMap =new HashMap();
            hashMap.put("key", uniqueKey);
            hashMap.put("topic", topic);
            hashMap.put("youtube_link", link);

            reference.child(uniqueKey).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(YoutubeLecture.this, "uploaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Dasboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(YoutubeLecture.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}