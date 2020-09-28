package com.example.wallpapernamo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wallpapernamo.R;
import com.example.wallpapernamo.models.Category;
import com.example.wallpapernamo.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private  List<Wallpaper> wallpaperList;

    public WallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_wallpapers,parent,false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        Wallpaper w = wallpaperList.get(position);
        holder.textView.setText(w.title);
        Glide.with(mCtx)
                .load(w.url)
                .into(holder.imageView);
        //Check if wallpaper is favourite
        if(w.isFavourite)
            holder.checkBoxFav.setChecked(true);
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
            buttonShare.setOnClickListener(this);
        }
        //Click listeners for the checkbox and buttons for share and download
        @Override
        public void onClick(View v) {
            //whenever button share and download are pressed this method gets called

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
            }else{
                //Remove from favourite list
                dbFav.child(w.id).setValue(null);
            }
        }
    }
}
