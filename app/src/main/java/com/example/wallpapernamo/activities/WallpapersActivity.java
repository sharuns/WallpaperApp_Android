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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {

    List<Wallpaper> wallpaperList;
    List<Wallpaper> favList;
    RecyclerView recyclerView ;
    WallpapersAdapter adapter;

    DatabaseReference dbWallpapers, dbFav;
    ProgressBar progressBar;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();

        final String category = intent.getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        wallpaperList = new ArrayList<>();
        favList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter =  new WallpapersAdapter(this, wallpaperList,this.getWindowManager(),this); //public WallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList, WindowManager windowManager, Activity m_activity)

        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressbar);



        dbWallpapers = FirebaseDatabase.getInstance().getReference("images")
        .child(category);


        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            dbFav = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);

            fetchFavWallpapers(category);
        }else{
            fetchWallpapers(category);
        }
    }

    //    //Fetches all the Favourite wallpapers of a particular category
    private void fetchFavWallpapers(final String category){
        progressBar.setVisibility(View.VISIBLE);
        dbFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.exists()){

                    for(DataSnapshot wallpaperSnapShot : snapshot.getChildren()){

                        String id = wallpaperSnapShot.getKey();
                        String title  = wallpaperSnapShot.child("title").getValue(String.class);
                        String desc = wallpaperSnapShot.child("desc").getValue(String.class);
                        String url = wallpaperSnapShot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id,title,desc,url,category);
                        favList.add(w);
                    }

                }
                fetchWallpapers(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //Fetches all the wallpapers of a particular category
    private void fetchWallpapers(final String category){
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

                        Wallpaper w = new Wallpaper(id,title,desc,url,category);
                        if(isFavourite(w)){
                            w.isFavourite = true;
                        }
                        wallpaperList.add(w);
                    }
                    adapter.notifyDataSetChanged(); //To load to the recycler view
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean isFavourite(Wallpaper w){
        for(Wallpaper f:favList){
            if(f.id.equals(w.id)){
                return true;
            }
        }
        return false;
    }
}