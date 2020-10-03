package com.example.wallpapernamo.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wallpapernamo.BuildConfig;
import com.example.wallpapernamo.R;
import com.example.wallpapernamo.fragments.SettingsFragment;
import com.example.wallpapernamo.models.Category;
import com.example.wallpapernamo.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private Activity m_activity;
    private WindowManager windowManager;
    private  List<Wallpaper> wallpaperList;

    public WallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList, WindowManager windowManager, Activity m_activity) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
        this.windowManager = windowManager;
        this.m_activity = m_activity;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_wallpapers,parent,false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, final int position) {
        Wallpaper w = wallpaperList.get(position);
        holder.textView.setText(w.title);
        Glide.with(mCtx)
                .load(w.url)
                .into(holder.imageView);
        //Check if wallpaper is favourite
        if(w.isFavourite)
            holder.checkBoxFav.setChecked(true);


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(mCtx);
                ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

                Display display = windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                Wallpaper w = wallpaperList.get(position);
                Glide.with(mCtx)
                        .asBitmap()
                        .load(w.url)
                        .into(new SimpleTarget<Bitmap>(width,height) {
                                  @RequiresApi(api = Build.VERSION_CODES.N)
                                  @Override
                                  public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                      ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                                      try {
                                          myWallpaperManager.setBitmap(resource);
                                          myWallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                                          Toast.makeText(m_activity, "Wallpaper Set Successfully", Toast.LENGTH_LONG).show();

                                      } catch (IOException e) {
                                          e.printStackTrace();
                                      }

                                  }
                              }
                        );

            }
        });



    }





    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView textView;
        ImageView imageView;

        CheckBox checkBoxFav;
        ImageButton buttonShare, buttonDownload;

        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_title);
            imageView = itemView.findViewById(R.id.image_view);

            checkBoxFav = itemView.findViewById(R.id.checkbox_favourite);
            buttonDownload = itemView.findViewById(R.id.button_download);
            buttonShare = itemView.findViewById(R.id.button_share);

            checkBoxFav.setOnCheckedChangeListener(this);
            buttonDownload.setOnClickListener(this);
            buttonShare.setOnClickListener(this); //Click listeners for the respective buttons
        }
        //Click listeners for the checkbox and buttons for share and download
        @Override
        public void onClick(View v) {
            //whenever button share and download are pressed this method gets called

            //identify which button is pressed : either share or download
            switch(v.getId()){
                case R.id.button_download:
                    Toast.makeText(m_activity, "Wallpaper downloading...", Toast.LENGTH_SHORT).show();
                    downloadWallpaper(wallpaperList.get(getAdapterPosition()));
                    Toast.makeText(m_activity, "Check your internal storage", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_share:
                    Toast.makeText(m_activity, "Select a sharing option...", Toast.LENGTH_SHORT).show();
                    shareWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
            }

        }

        private void shareWallpaper(Wallpaper w){

            ((Activity)mCtx).findViewById(
                    R.id.progressbar
            ).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            //sharing with Intent
                            ((Activity)mCtx).findViewById(
                                    R.id.progressbar
                            ).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM,getLocalBitmapUri(resource));
                            mCtx.startActivity(Intent.createChooser(intent,"wallpaper_app"));
                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bmp){
            Uri bmpUri = null;
            //second parameter is name of the file suffixed with the app name (not needed to be)

            try {
                File file = new File(mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"wallpaperApp"+System.currentTimeMillis()+ ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG,90,out);
                out.close();
                bmpUri = FileProvider.getUriForFile(mCtx, BuildConfig.APPLICATION_ID + ".provider", file);//Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

        private void downloadWallpaper(final Wallpaper wallpaper){
            ((Activity)mCtx).findViewById(
                    R.id.progressbar
            ).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            //sharing with Intent

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri= saveWallpaperAndGetUri(resource, wallpaper.id);
                            ((Activity)mCtx).findViewById(
                                    R.id.progressbar
                            ).setVisibility(View.GONE);

                            if(uri!=null){
                                intent.setDataAndType(uri,"image/*");
                                Toast.makeText(m_activity, "Wallpaper Saved", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


        }

        private Uri saveWallpaperAndGetUri(Bitmap bitmap, String id){

            //if permission is granted
            if(ContextCompat.checkSelfPermission(mCtx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) mCtx,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){//take to settings screen for access

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package", mCtx.getPackageName(),null);
                    intent.setData(uri);

                    mCtx.startActivity(intent);

                }else{
                    ActivityCompat.requestPermissions((Activity)mCtx, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }
                return null;
            }
            //Creating a folder

            //Folder name will be the string used in the end
                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/wallpapersApp");
            folder.mkdirs();

            File file = new File(folder,id + ".jpg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                out.close();
                            ((Activity)mCtx).findViewById(
                                    R.id.progressbar
                            ).setVisibility(View.GONE);
                return  FileProvider.getUriForFile(mCtx, BuildConfig.APPLICATION_ID + ".provider", file);//Uri.fromFile(file);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //Whenever the checked state of the checkbox changes this method gets called
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Toast.makeText(mCtx,"Please Log in first",Toast.LENGTH_LONG).show();
                buttonView.setChecked(false);
                return;
            }


            int position = getAdapterPosition();
            Wallpaper w  =  wallpaperList.get(position);


            //Creating a database reference
            DatabaseReference dbFav = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.category);

            //User already logged in
            if(isChecked){
                //Adding to favourite list
                dbFav.child(w.id).setValue(w);

                if(buttonView.isPressed()) {//gets hit when the checkbox is pressed manually
                    Toast.makeText(m_activity, "Saved as Favorite", Toast.LENGTH_SHORT).show();
                }


            }else{
                //Remove from favourite list
                dbFav.child(w.id).setValue(null);

                Toast.makeText(m_activity, "Removed from Favorite", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
