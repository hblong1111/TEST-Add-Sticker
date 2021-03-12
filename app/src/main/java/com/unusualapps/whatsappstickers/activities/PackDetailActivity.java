package com.unusualapps.whatsappstickers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unusualapps.whatsappstickers.Event.PackDetailActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.adapter.StickerDetailAdapter;
import com.unusualapps.whatsappstickers.model.Data;
import com.unusualapps.whatsappstickers.model.Pack;
import com.unusualapps.whatsappstickers.utils.Common;

public class PackDetailActivity extends AppCompatActivity implements View.OnClickListener, PackDetailActivityEvent {

    Pack pack;

    private ImageButton btnBack;
    private LinearLayout btnAddPack;
    private ImageView img;
    private TextView tvName;
    private TextView tvAuthor;
    private RecyclerView rcv;

    StickerDetailAdapter adapter;
    private Dialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_detail);

        btnBack = findViewById(R.id.btnBack);
        btnAddPack = findViewById(R.id.btnAddPack);
        img = findViewById(R.id.img);
        tvName = findViewById(R.id.tvName);
        tvAuthor = findViewById(R.id.tvAuthor);
        rcv = findViewById(R.id.rcv);


        btnAddPack.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        pack = (Pack) getIntent().getSerializableExtra(Common.CODE_PUT_PACK);

        tvAuthor.setText(pack.author);
        tvName.setText(pack.name);

        Glide.with(this).load(pack.listSticker.get(0).urlImage).into(img);

        adapter = new StickerDetailAdapter(pack.listSticker, this);

        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new GridLayoutManager(this, 4));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPack:
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void stickerClick(Pack.ListSticker listSticker) {
        showDialogStickerDetail(listSticker);
    }

    private void showDialogStickerDetail(Pack.ListSticker listSticker) {

        View view
                = LayoutInflater.from(this).inflate(R.layout.dialog_sticker_detail, null, false);
        ImageButton btnBack;
        ImageView img;
        TextView tvAuthor;
        LinearLayout btnAddPack;
        Button btnShare;
        Button btnSave;

        btnBack = view.findViewById(R.id.btnBack);
        img = view.findViewById(R.id.img);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        btnAddPack = view.findViewById(R.id.btnAddPack);
        btnShare = view.findViewById(R.id.btnShare);
        btnSave = view.findViewById(R.id.btnSave);


        Glide.with(img).load(listSticker.urlImage).into(img);

        tvAuthor.setText(pack.author);

        btnBack.setOnClickListener(v -> alertDialog.dismiss());

        //todo: save, share and add sticker


        int width = getResources().getDisplayMetrics().widthPixels;

        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(view);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = (int) (0.9f * width);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        alertDialog.show();
        alertDialog.getWindow().setAttributes(lp);
    }
}