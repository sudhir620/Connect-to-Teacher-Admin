package com.skcoder.ctzoneadmin.ui.notices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skcoder.ctzoneadmin.R;

import java.util.ArrayList;
import java.util.Collections;

public class DeleteNoticeActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView deleteRecyclerView;
    ProgressBar deleteProgressBar;
    private ArrayList<NoticeData> list;
    private NoticeAdaper adaper;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Notices");

        reference = FirebaseDatabase.getInstance().getReference().child("Notice");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), notices.class));
            }
        });

        deleteRecyclerView = findViewById(R.id.delete_notice_recyclerview);
        deleteProgressBar = findViewById(R.id.progress_bar);

        deleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deleteRecyclerView.setHasFixedSize(true);

        getNotice();

    }

    private void getNotice() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NoticeData data = snapshot.getValue(NoticeData.class);
                    list.add(data);
                }

                Collections.reverse(list);
                adaper = new NoticeAdaper(DeleteNoticeActivity.this, list);
                adaper.notifyDataSetChanged();
                deleteProgressBar.setVisibility(View.GONE);
                deleteRecyclerView.setAdapter(adaper);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                deleteProgressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNoticeActivity.this, "Error : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}