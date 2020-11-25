package com.example.wallpapernamo.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import android.widget.Toast;


import androidx.annotation.NonNull;


import com.bumptech.glide.Glide;
import com.example.wallpapernamo.R;
import com.example.wallpapernamo.activities.WallpapersActivity;
import com.example.wallpapernamo.models.Category;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private Context mCtx;
    private static int cnt = 0;
    private  List<Category> categoryList;

    public CategoriesAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_categories,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        holder.textView.setText(c.name);
        //Here we are converting the url to image using glide.
        if(0 == cnt){
            Toast.makeText(mCtx, c.thumb, Toast.LENGTH_LONG).show();
            cnt++;
            //DownloadDatabase(c.thumb,"Testing");
        }

        Glide.with(mCtx)
                .load(c.thumb)
                .into(holder.imageView);
    }

    public void DownloadDatabase(final String DownloadUrl, final String fileName) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    try {
                        File root = android.os.Environment.getExternalStorageDirectory();
                        File dir = new File(root.getAbsolutePath() + "/timy/databases");
                        if(dir.exists() == false){
                            dir.mkdirs();
                        }

                        URL url = new URL(DownloadUrl);
                        File file = new File(dir,fileName);

                        long startTime = System.currentTimeMillis();
                        // Log.d("DownloadManager" , "download url:" +url);
                        //Log.d("DownloadManager" , "download file name:" + fileName);

                        URLConnection uconn = url.openConnection();
                        uconn.setConnectTimeout(15000);

                        InputStream is = uconn.getInputStream();



                        ZipInputStream zipinstream = new ZipInputStream(new BufferedInputStream(is));
                        ZipEntry zipEntry;

                        while((zipEntry = zipinstream.getNextEntry()) != null){
                            String zipEntryName = zipEntry.getName();
                            File file1 = new File(file + zipEntryName);
                            if(file1.exists()){

                            }else{
                                if(zipEntry.isDirectory()){
                                    file1.mkdirs();
                                }
                            }
                        }

                        BufferedInputStream bufferinstream = new BufferedInputStream(is);

                        ByteArrayOutputStream baf = new ByteArrayOutputStream();
                        byte[] data = new byte[5000];
                        int current = 0;
                        while((current = bufferinstream.read()) != -1){
                            baf.write(data,0,current);
                        }

                        FileOutputStream fos = new FileOutputStream( file);
                        fos.write(baf.toByteArray());
                        fos.flush();
                        fos.close();
                        //Log.d("DownloadManager" , "download ready in" + ((System.currentTimeMillis() - startTime)/1000) + "sec");
                    }
                    catch(IOException e) {
                        // Log.d("DownloadManager" , "Error:" + e);
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_cat_name);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int p = getAdapterPosition();
            Category c =  categoryList.get(p);

            Intent intent = new Intent(mCtx, WallpapersActivity.class);
            intent.putExtra("category",c.name);
            mCtx.startActivity(intent);
        }
    }
}
