package com.unusualapps.whatsappstickers.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unusualapps.whatsappstickers.Event.HomeActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.model.Pack;
import com.unusualapps.whatsappstickers.utils.SpacesItemDecoration;

import java.util.List;

public class PackHomeFragmentAdapter extends RecyclerView.Adapter<PackHomeFragmentAdapter.ViewHolder> {
    List<Pack> list;

    private HomeActivityEvent event;

    public PackHomeFragmentAdapter(List<Pack> list, HomeActivityEvent event) {
        this.list = list;
        this.event = event;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pack_home_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pack pack = list.get(position);

        holder.tvAuthor.setText(pack.author);
        holder.tvNamePack.setText(pack.name);
        holder.tvNumberSticker.setText(pack.listSticker.size() + " stickers");
        holder.rcv.setAdapter(new ImageAdapter(pack.listSticker, pack));
        holder.rcv.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 5));

        holder.btnAdd.setOnClickListener(v -> event.addPackToWhatsApp(pack));
        holder.itemView.setOnClickListener(v -> event.seeDetailPack(pack));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNamePack;
        private TextView tvAuthor;
        private TextView tvNumberSticker;
        private TextView btnAdd;
        private RecyclerView rcv;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNamePack = itemView.findViewById(R.id.tvNamePack);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvNumberSticker = itemView.findViewById(R.id.tvNumberSticker);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            rcv = itemView.findViewById(R.id.rcv);
            imageView = itemView.findViewById(R.id.imageView);


        }
    }


    private class ImageAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<Pack.ListSticker> list;

        private Pack pack;

        public ImageAdapter(List<Pack.ListSticker> list, Pack pack) {
            this.list = list;
            this.pack = pack;
        }

        @NonNull
        @Override

        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_image, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(holder.imageView.getContext()).load(list.get(position).urlImage).into(holder.imageView);

            holder.imageView.setOnClickListener(v -> event.seeDetailPack(pack));

        }

        @Override
        public int getItemCount() {
            return list.size() > 5 ? 5 : list.size();
        }
    }

}
