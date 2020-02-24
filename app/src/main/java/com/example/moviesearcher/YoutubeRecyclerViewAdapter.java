package com.example.moviesearcher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesearcher.models.YoutubeDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class YoutubeRecyclerViewAdapter extends RecyclerView.Adapter<YoutubeRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<YoutubeDataModel> dataSet;

    public YoutubeRecyclerViewAdapter(Context context, ArrayList<YoutubeDataModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public YoutubeRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_youtube_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final YoutubeRecyclerViewAdapter.ViewHolder holder, int position) {

        YoutubeDataModel object = dataSet.get(position);

        holder.upDate.setText(object.getUpDate());
        holder.title.setText(object.getTitle());
        holder.desc.setText(object.getDesc());
        Picasso.get().load(object.getThumb()).into(holder.thumb);

        final String url = "https://www.youtube.com/watch?v=" + object.getVideoId();

        holder.list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("testas", url);
                Uri data = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(data);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout list;
        TextView upDate;
        ImageView thumb;
        TextView title;
        TextView desc;

        ViewHolder(@NonNull View view) {
            super(view);
            list = view.findViewById(R.id.rvYTList);
            upDate = view.findViewById(R.id.tvUploadDate);
            thumb = view.findViewById(R.id.thumbnail);
            title = view.findViewById(R.id.tvTitle);
            desc = view.findViewById(R.id.tvDescription);
        }
    }
}
