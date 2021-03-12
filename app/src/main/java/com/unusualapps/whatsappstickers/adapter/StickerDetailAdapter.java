package com.unusualapps.whatsappstickers.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unusualapps.whatsappstickers.Event.PackDetailActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.model.Pack;

import java.util.List;

public class StickerDetailAdapter extends RecyclerView.Adapter<StickerDetailAdapter.ViewHolder> {
    List<Pack.ListSticker> list;
    PackDetailActivityEvent event;

    public StickerDetailAdapter(List<Pack.ListSticker> list, PackDetailActivityEvent event) {
        this.list = list;
        this.event = event;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sticker, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.img).load(list.get(position).urlImage).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            event.stickerClick(list.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
        }
    }
}
