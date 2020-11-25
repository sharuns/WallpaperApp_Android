package com.example.wallpapernamo.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wallpapernamo.R;

import java.io.IOException;

public class testact extends AppCompatActivity {

    ImageView imageView = null;
    String resid = null;
    Display display = null;
    Dialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_actvitylayout);
        dialog = new Dialog(this);

     //View overlay =  findViewById(R.id.myid);
     //overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //Another method to make image fullscreen

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Makes the image fullScreen

        display = getWindowManager().getDefaultDisplay();

        resid = getIntent().getStringExtra("testID"); //gets the image Uri from wallpaperAdaptor


       /* if(display != null){ //test code
            //Display display = windowManager.getDefaultDisplay();
            Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show();
        }*/

        imageView = findViewById(R.id.test_view); //Connecting the layout to its source
        imageView.setScaleType(ImageView.ScaleType.FIT_XY); //Scaling the image and loading using Glide
        Glide.with(this)
                .asBitmap()
                .load(resid)
                .into(imageView);

    }


    public void SetLockScreen(View view) {
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(this);
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            //Wallpaper w = w1;
            Glide.with(this)
                    .asBitmap()
                    .load(resid)
                    .into(new SimpleTarget<Bitmap>(width,height) {
                              @RequiresApi(api = Build.VERSION_CODES.N)
                              @Override
                              public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                  findViewById(R.id.progressbar).setVisibility(View.GONE);

                                  try {
                                     // myWallpaperManager.setBitmap(resource);
                                      myWallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                                      Toast.makeText(testact.this, "Lock screen Set", Toast.LENGTH_SHORT).show();

                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }

                              }
                          }
                    );
        dialog.dismiss();
    }

    public void SetBothScreen(View view) {


       findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(this);
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //Wallpaper w = w1;
        Glide.with(this)
                .asBitmap()
                .load(resid)
                .into(new SimpleTarget<Bitmap>(width,height) {
                          @RequiresApi(api = Build.VERSION_CODES.N)
                          @Override
                          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                              findViewById(R.id.progressbar).setVisibility(View.GONE);

                              try {
                                  myWallpaperManager.setBitmap(resource);
                                  myWallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                                  Toast.makeText(testact.this, "Both screen Set", Toast.LENGTH_SHORT).show();

                              } catch (IOException e) {
                                  e.printStackTrace();
                              }

                          }
                      }
                );
        dialog.dismiss();
    }

    public void SetHomeScreen(View view) {
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(this);
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //Wallpaper w = w1;
        Glide.with(this)
                .asBitmap()
                .load(resid)
                .into(new SimpleTarget<Bitmap>(width,height) {
                          @RequiresApi(api = Build.VERSION_CODES.N)
                          @Override
                          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                              findViewById(R.id.progressbar).setVisibility(View.GONE);

                              try {
                                  myWallpaperManager.setBitmap(resource);
                                  //myWallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                                  Toast.makeText(testact.this, "Home screen Set", Toast.LENGTH_SHORT).show();

                              } catch (IOException e) {
                                  e.printStackTrace();
                              }

                          }
                      }
                );
        dialog.dismiss();
    }

    public void SetAswall(View view) {

        dialog.setContentView(R.layout.popupmenu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
