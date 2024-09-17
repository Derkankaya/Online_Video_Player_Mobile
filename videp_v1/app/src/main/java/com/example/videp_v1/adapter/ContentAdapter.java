package com.example.videp_v1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.videp_v1.R;
import com.example.videp_v1.model.ContentModel;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ContentModel> list;
    private DatabaseReference reference;
    private ItemClickListener itemClickListener;

    public ContentAdapter(Context context, ArrayList<ContentModel> list, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    public ContentAdapter(Context context, ArrayList<ContentModel> list) {
        this.context = context;
        this.list = list;
    }

    public interface ItemClickListener {
        void onItemClick(ContentModel contentModel);

        abstract ArrayList<ContentModel> doInBackground(Void... voids);

        void onPostExecute(ArrayList<ContentModel> videoList);
    }

    public void setData(ArrayList<ContentModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false), itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentModel model = list.get(position);
        if (model != null) {
            if (model != null && model.getThumbnailUrl() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(model.getThumbnailUrl())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.e("Glide", "Thumbnail yüklenirken hata oluştu", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                // Kaynak hazır olduğunda yapılacak işlemler
                                return false;
                            }
                        })
                        .into(holder.thumbnail);
            } else {
                // Model veya URL null ise, uygun bir işlem yapılmalı
                Log.e("Glide", "Model veya URL null");
            }

            holder.video_title.setText(model.getVideo_title());
            holder.views.setText(model.getViews() + " izlenme");
            holder.date.setText(model.getDate());
            holder.channel_name.setText(model.getPublisher());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;
        TextView video_title, channel_name, views, date;
        CircleImageView channel_logo;

        public ViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
           // thumbnail.setAdjustViewBounds(true);
           // thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);


            video_title = itemView.findViewById(R.id.video_tittle);
            channel_name = itemView.findViewById(R.id.channel_name);
            views = itemView.findViewById(R.id.view_count);
            date = itemView.findViewById(R.id.date);
            //channel_logo = itemView.findViewById(R.id.channel_logo);

            itemView.setOnClickListener(this);
            ContentAdapter.this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (ContentAdapter.this.itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ContentModel clickedItem = list.get(position);
                    ContentAdapter.this.itemClickListener.onItemClick(clickedItem);
                }
            }
        }
    }
}
