package com.example.moviesearcher;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesearcher.models.NiconicoDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NiconicoRecyclerViewAdapter extends RecyclerView.Adapter<NiconicoRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NiconicoDataModel> dataSet;

    public NiconicoRecyclerViewAdapter(Context context, ArrayList<NiconicoDataModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public NiconicoRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_youtube_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NiconicoRecyclerViewAdapter.ViewHolder holder, int position) {

        NiconicoDataModel object = dataSet.get(position);

        holder.upDate.setText(object.getUpDate());
        holder.title.setText(object.getTitle());
        holder.desc.setText(Html.fromHtml(object.getDesc()));
        Picasso.get().load(object.getThumb()).into(holder.thumb);

        final String url = "https://www.nicovideo.jp/watch/" + object.getVideoId();

        holder.list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout list;
        TextView upDate;
        ImageView thumb;
        TextView title;
        TextView desc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            list = itemView.findViewById(R.id.rvYTList);
            upDate = itemView.findViewById(R.id.tvUploadDate);
            thumb = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.tvTitle);
            desc = itemView.findViewById(R.id.tvDescription);
        }
    }
}
