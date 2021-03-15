package com.unusualapps.whatsappstickers.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;

import java.util.List;

public class StickerLocalAdapter extends RecyclerView.Adapter<  StickerLocalAdapter.ViewHolder> {
    List<StickerLocal> list;

    public StickerLocalAdapter(List<StickerLocal> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sticker_local_1, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sticker_local, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) != 0) {
            holder.imageView.setImageURI(list.get(position).getUri());
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "show detail sticker", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "add new sticker", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1 + list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
        }
    }
}
