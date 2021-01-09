package com.skcoder.ctzoneadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.skcoder.ctzoneadmin.ui.notices.DeleteNoticeActivity;

public class Dasboard extends AppCompatActivity {

    CardView notes, assignments, notification, ebooks, youtubelecture, addgallery, addteachers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard);

        notes = findViewById(R.id.tnotes);
        assignments = findViewById(R.id.tassignments);
        notification = findViewById(R.id.tnotices);
        ebooks = findViewById(R.id.tebooks);
        youtubelecture = findViewById(R.id.tyoutubelecture);
        addgallery = findViewById(R.id.tAddGallery);
        addteachers = findViewById(R.id.tTeacherDetails);

        addteachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.skcoder.ctzoneadmin.ui.addteachers.UpdateFaculty.class);
                startActivity(i);
            }
        });

        addgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.skcoder.ctzoneadmin.ui.addgallery.AddGallery.class);
                startActivity(i);
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.skcoder.ctzoneadmin.ui.notes.Notes.class);
                startActivity(i);
            }
        });

        assignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.skcoder.ctzoneadmin.ui.assignment.Assignment.class);
                startActivity(i);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DeleteNoticeActivity.class);
                startActivity(i);
            }
        });

        ebooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.skcoder.ctzoneadmin.ui.ebooks.Ebooks.class);
                startActivity(i);
            }
        });

        youtubelecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.skcoder.ctzoneadmin.ui.youtubelecture.YoutubeLecture.class);
                startActivity(i);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logoutbtn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutbtn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);

        }
    }



}