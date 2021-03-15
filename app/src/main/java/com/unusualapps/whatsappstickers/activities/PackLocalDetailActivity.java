package com.unusualapps.whatsappstickers.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.unusualapps.whatsappstickers.constants.Constants;
import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.db.DatabaseModule;
import com.unusualapps.whatsappstickers.image_edit.EditImageActivity;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;
import com.unusualapps.whatsappstickers.utils.Common;
import com.unusualapps.whatsappstickers.utils.FileUtils;
import com.unusualapps.whatsappstickers.utils.ImageUtils;
import com.unusualapps.whatsappstickers.utils.StickerPackLocalUtils;
import com.unusualapps.whatsappstickers.utils.StickerPacksManager;
import com.unusualapps.whatsappstickers.view_model.PackLocalDetailActivityViewModel;
import com.unusualapps.whatsappstickers.view_model.ViewModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.unusualapps.whatsappstickers.fragment.tutorial.CreateFragment.addImageToGallery;

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
    private Dialog alertDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_local_detail);

        model = new ViewModelProvider(this, new ViewModelFactory()).get(PackLocalDetailActivityViewModel.class);

        packLocal = (PackLocal) getIntent().getSerializableExtra(Common.CODE_PUT_PACK);

        db = DatabaseModule.getInstance(getApplication());

        listSticker = new ArrayList<>();
        adapter = new StickerLocalAdapter(listSticker, this);

        initView();


        tvName.setText(packLocal.getName());
        tvAuthor.setText(packLocal.getAuthor());

        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new GridLayoutManager(this, 4));


        model.getStickerLocal().observe(this, stickerLocals -> {
            if (stickerLocals.size() > 0) {
                img.setImageURI(stickerLocals.get(0).getUri());
            }
            listSticker.clear();
            listSticker.addAll(stickerLocals);
            adapter.notifyDataSetChanged();
            updateView(listSticker.size());

        });
        model.getListSticker(db,packLocal.getId());

    }

    private void updateView(int size) {
        if (size >= 3) {
            tvError.setVisibility(View.INVISIBLE);
            btnAddPack.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            btnAddPack.setClickable(true);
        } else {
            tvError.setVisibility(View.VISIBLE);
            btnAddPack.setBackgroundColor(ContextCompat.getColor(this, R.color.gray50));
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
        btnAddPack.setOnClickListener(this);
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
                    StickerPackLocalUtils.deletePackLocal(db, packLocal.getId());
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

        model.getListSticker(db,packLocal.getId());
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
        } else {
            showDialogStickerDetail(listSticker.get(position - 1));
        }
    }


    private void showDialogStickerDetail(StickerLocal stickerLocal) {

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


        btnAddPack.setVisibility(this.btnAddPack.getVisibility());
        btnAddPack.setBackground(this.btnAddPack.getBackground());
        btnAddPack.setClickable(this.btnAddPack.isClickable());


        btnShare.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);

        img.setImageURI(stickerLocal.getUri());

        tvAuthor.setText(packLocal.getAuthor());

        btnBack.setOnClickListener(v -> alertDialog.dismiss());
        btnAddPack.setOnClickListener(v -> {
            Toast.makeText(this, "ADd", Toast.LENGTH_SHORT).show();
        });


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

                Uri uri = saveFileToCache(data.getData());

                StickerLocal stickerLocal = new StickerLocal(0, packLocal.getId(), uri.toString());
                db.stickerDao().insert(stickerLocal);
                db.stickerDao().getAllForPack(packLocal.getId());
            }
        }

    }

    private Uri saveFileToCache(Uri data) {
        File parent = new File(getCacheDir().getPath() + "/stickersCreated");
        parent.mkdirs();

        try {
            File file = new File(parent, System.currentTimeMillis() + ".PNG");
            file.createNewFile();
            byte[] bitmapdata = ImageUtils.compressImageToBytes(data, 70, 512, 512, this, Bitmap.CompressFormat.PNG);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}