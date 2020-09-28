package com.example.wallpapernamo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;


import com.example.wallpapernamo.R;
import com.example.wallpapernamo.adapters.WallpapersAdapter;
import com.example.wallpapernamo.models.Wallpaper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {

    List<Wallpaper> wallpaperList;
    RecyclerView recyclerView ;
    WallpapersAdapter adapter;

    DatabaseReference dbWallpapers;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        Intent intent = getIntent();

        String category = intent.getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        wallpaperList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter =  new WallpapersAdapter(this, wallpaperList,category);

        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressbar);



        dbWallpapers = FirebaseDatabase.getInstance().getReference("images")
        .child(category);

        progressBar.setVisibility(View.VISIBLE);
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                    if(snapshot.exists()){

                            for(DataSnapshot wallpaperSnapShot : snapshot.getChildren()){

                                String id = wallpaperSnapShot.getKey();
                                String title  = wallpaperSnapShot.child("title").getValue(String.class);
                                String desc = wallpaperSnapShot.child("desc").getValue(String.class);
                                String url = wallpaperSnapShot.child("url").getValue(String.class);

                                Wallpaper w = new Wallpaper(id,title,desc,url);
                                wallpaperList.add(w);
                            }
                            adapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}