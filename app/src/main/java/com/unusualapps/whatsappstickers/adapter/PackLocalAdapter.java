package com.unusualapps.whatsappstickers.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.activities.CreateNewPackLocalActivity;
import com.unusualapps.whatsappstickers.activities.PackLocalDetailActivity;
import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.db.DatabaseModule;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;
import com.unusualapps.whatsappstickers.utils.Common;
import com.unusualapps.whatsappstickers.utils.StickerPackLocalUtils;

import java.util.List;


public class PackLocalAdapter extends RecyclerView.Adapter<PackLocalAdapter.ViewHolder> {
    List<PackLocal> list;
    AppDatabase db;

    public PackLocalAdapter(List<PackLocal> list, AppDatabase db) {
        this.list = list;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pack_local, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackLocal packLocal = list.get(position);
        holder.tvAuthor.setText(packLocal.getAuthor());
        holder.tvNamePack.setText(packLocal.getName());

        List<StickerLocal> stickerLocals = db.stickerDao().getAllForPack(packLocal.getId());

        int numberSticker = stickerLocals.size();

        holder.tvNumberSticker.setText(numberSticker + " stickers");

        holder.rcv.setAdapter(new ItemAdapter(stickerLocals));
        holder.rcv.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 6));

        holder.itemView.setOnClickListener(v -> {
            holder.itemView.getContext().startActivity(new Intent(v.getContext(), PackLocalDetailActivity.class).putExtra(Common.CODE_PUT_PACK, packLocal));
        });

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_up, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.item_edit_pack:
                        Intent intent = new Intent(v.getContext(), CreateNewPackLocalActivity.class);
                        intent.putExtra(Common.CODE_PUT_PACK, packLocal);
                        v.getContext().startActivity(intent);
                        break;
                    case R.id.item_delete_pack:
                        StickerPackLocalUtils.deletePackLocal(db,packLocal.getId());
                        list.remove(position);
                        notifyDataSetChanged();
                        break;
                }


                return true;
            });

            popupMenu.show();
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNamePack;
        private TextView tvAuthor;
        private TextView tvNumberSticker;
        private ImageButton btnMore;
        private RecyclerView rcv;


        //itemAdapter
        private ImageView view0;
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamePack = itemView.findViewById(R.id.tvNamePack);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvNumberSticker = itemView.findViewById(R.id.tvNumberSticker);
            btnMore = itemView.findViewById(R.id.btnMore);
            rcv = itemView.findViewById(R.id.rcv);


            view0 = itemView.findViewById(R.id.view0);
            img = itemView.findViewById(R.id.img);

        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<StickerLocal> list;

        public ItemAdapter(List<StickerLocal> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                holder.view0.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.INVISIBLE);
            } else {
                holder.view0.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.VISIBLE);

                holder.img.setImageURI(list.get(position - 1).getUri());
            }
        }

        @Override
        public int getItemCount() {
            return list.size() > 5 ? 6 : list.size() + 1;
        }
    }
}
