package com.unusualapps.whatsappstickers.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.unusualapps.whatsappstickers.Event.PackLocalDetailEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.adapter.StickerLocalAdapter;
import com.unusualapps.whatsappstickers.backgroundRemover.CutOut;
import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.db.DatabaseModule;
import com.unusualapps.whatsappstickers.image_edit.EditImageActivity;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;
import com.unusualapps.whatsappstickers.utils.Common;
import com.unusualapps.whatsappstickers.utils.FileUtils;
import com.unusualapps.whatsappstickers.view_model.PackLocalDetailActivityViewModel;
import com.unusualapps.whatsappstickers.view_model.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PackLocalDetailActivity extends AppCompatActivity implements View.OnClickListener, PackLocalDetailEvent {
    private static final int CODE_REQUEST = 200;
    private static final int REQUEST_CODE_EDIT = 564;
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

        adapter = new StickerLocalAdapter(listSticker, this);

        initView();


        tvName.setText(packLocal.getName());
        tvAuthor.setText(packLocal.getAuthor());

        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new GridLayoutManager(this, 4));


        model.getStickerLocal().observe(this, stickerLocals -> {
            updateView(stickerLocals.size());

            listSticker.clear();
            listSticker.addAll(stickerLocals);
            adapter.notifyDataSetChanged();

        });

    }

    private void updateView(int size) {
        if (size >= 3) {
            tvError.setVisibility(View.INVISIBLE);
            btnAddPack.setBackgroundColor(ContextCompat.getColor(this, R.color.gray50));
            btnAddPack.setClickable(true);
        } else {
            tvError.setVisibility(View.VISIBLE);
            btnAddPack.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            btnAddPack.setClickable(false);
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

        btnBack.setOnClickListener(this);
        btnMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnMore:
                showPopupMenu();
                break;
            case R.id.btnAddPack:
                Toast.makeText(this, "add pack", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnMore);
        popupMenu.getMenuInflater().inflate(R.menu.pop_up, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_edit_pack:
                    Intent intent = new Intent(this, CreateNewPackLocalActivity.class);
                    intent.putExtra(Common.CODE_PUT_PACK, packLocal);
                    startActivity(intent);
                    break;
                case R.id.item_delete_pack:
                    db.packDao().delete(packLocal);
                    onBackPressed();
                    finish();
                    break;
            }
            return true;
        });

        popupMenu.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        packLocal = db.packDao().findById(packLocal.getId());

        tvName.setText(packLocal.getName());
        tvAuthor.setText(packLocal.getAuthor());

        model.getListSticker(db);
    }

    @Override
    public void itemClick(int position) {
        if (position == 0) {
            ImagePicker.with(this)
                    .setFolderMode(true)
                    .setFolderTitle("Album")
                    .setDirectoryName("Image Picker")
                    .setMultipleMode(false)
                    .setShowNumberIndicator(true)
//                    .setMaxSize(1)
//                    .setLimitMessage("You can select up to 10 images")
                    .setRequestCode(CODE_REQUEST)
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST) {

            ArrayList<Uri> uris = new ArrayList<>();
            ArrayList<Image> images = ImagePicker.getImages(data);
            for (int i = 0; i < images.size(); i++) {
                uris.add(images.get(i).getUri());
            }
            if (resultCode == RESULT_OK) {
                if (uris.size() > 0) {
                    Log.d("hblong", "CreateFragment | onActivityResult: " + uris.get(0));
//                    CutOut.activity().src(uris.get(0)).intro().start(this);
                    CropImage.activity(uris.get(0))
                            .setAspectRatio(1, 1)
                            .start(this);
                }
            }
            return;
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("hblong", "PackLocalDetailActivity | onActivityResult: " + resultUri.toString());
                startActivityForResult(new Intent(this, EditImageActivity.class).setData(resultUri), REQUEST_CODE_EDIT);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == REQUEST_CODE_EDIT) {
            if (resultCode == RESULT_OK) {
                ImageView imgTest;

                imgTest = findViewById(R.id.imgTest);
                imgTest.setImageURI(data.getData());
            }
        }

    }
}