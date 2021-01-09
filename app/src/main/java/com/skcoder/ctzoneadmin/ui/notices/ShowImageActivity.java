package com.skcoder.ctzoneadmin.ui.notices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.skcoder.ctzoneadmin.R;
import com.squareup.picasso.Picasso;

public class ShowImageActivity extends AppCompatActivity {
    String showImage;
    PhotoView ShowImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Notices");

        Intent intent = getIntent();
        showImage = intent.getStringExtra("img");

        ShowImg = findViewById(R.id.photo_view);

        try {
            Picasso.get().load(showImage).into(ShowImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}