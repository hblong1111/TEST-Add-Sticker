package com.unusualapps.whatsappstickers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.adapter.StickerLocalAdapter;
import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.db.DatabaseModule;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;
import com.unusualapps.whatsappstickers.utils.Common;
import com.unusualapps.whatsappstickers.utils.FileUtils;
import com.unusualapps.whatsappstickers.view_model.PackLocalDetailActivityViewModel;
import com.unusualapps.whatsappstickers.view_model.ViewModelFactory;

import java.util.List;

public class PackLocalDetailActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private LinearLayout btnAddPack;
    private ImageView img;
    private TextView tvName;
    private TextView tvAuthor;
    private RecyclerView rcv;
    private ImageButton btnMore;
    private TextView tvError;

    PackLocalDetailActivityViewModel model;

    PackLocal packLocal;

    AppDatabase db;

    List<StickerLocal> listSticker;

    StickerLocalAdapter adapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_local_detail);

        model = new ViewModelProvider(this, new ViewModelFactory()).get(PackLocalDetailActivityViewModel.class);

        packLocal = (PackLocal) getIntent().getSerializableExtra(Common.CODE_PUT_PACK);

        db = DatabaseModule.getInstance(getApplication());

        listSticker = db.stickerDao().getAll();

        adapter = new StickerLocalAdapter(listSticker);

        initView();


        tvName.setText(packLocal.getName());
        tvAuthor.setText(packLocal.getAuthor());

        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new GridLayoutManager(this, 4));

        model.getStickerLocal().observe(this, packLocal1 -> {
            tvName.setText(packLocal.getName());
            tvAuthor.setText(packLocal.getAuthor());
        });

        model.getStickerLocal().observe(this, stickerLocals -> {
            updateView(stickerLocals.size());
        });

    }

    private void updateView(int size) {
        if (size >= 3) {
            tvError.setVisibility(View.INVISIBLE);
            btnAddPack.setBackgroundColor(ContextCompat.getColor(this, R.color.gray50));
        } else {
            tvError.setVisibility(View.VISIBLE);
            btnAddPack.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        btnAddPack = findViewById(R.id.btnAddPack);
        img = findViewById(R.id.img);
        tvName = findViewById(R.id.tvName);
        tvAuthor = findViewById(R.id.tvAuthor);
        rcv = findViewById(R.id.rcv);
        btnMore = findViewById(R.id.btnMore);
        tvError = findViewById(R.id.tvError);
    }
}