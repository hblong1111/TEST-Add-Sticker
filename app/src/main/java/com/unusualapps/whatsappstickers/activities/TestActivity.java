package com.unusualapps.whatsappstickers.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.constants.Constants;
import com.unusualapps.whatsappstickers.utils.FileUtils;
import com.unusualapps.whatsappstickers.utils.StickerPacksManager;
import com.unusualapps.whatsappstickers.whatsapp_api.Sticker;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerContentProvider;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerPack;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerPackDetailsActivity;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 111;
    private ArrayList<Uri> uries;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;
        uries = new ArrayList<>();

        new TaskGetUriFromUrl().execute();

    }

    class TaskGetUriFromUrl extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            uries.add(getImageUri(context, getBitmapFromURL("https://cdn-thethao247.com/upload/kienlv/2020/09/11/tuyen-thu-dt-viet-nam-cong-khai-ban-gai-xinh-nhu-mong1599795990.png")));
            uries.add(getImageUri(context, getBitmapFromURL("https://cdn-thethao247.com/upload/kienlv/2020/09/11/tuyen-thu-dt-viet-nam-cong-khai-ban-gai-xinh-nhu-mong1599795990.png")));
            uries.add(getImageUri(context, getBitmapFromURL("https://cdn-thethao247.com/upload/kienlv/2020/09/11/tuyen-thu-dt-viet-nam-cong-khai-ban-gai-xinh-nhu-mong1599795990.png")));

            Log.d("hblong", "TestActivity | onCreate: " + uries.get(0).toString());
            return null;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void chooseImage(View view) {
        FishBun.with(this)
                .setImageAdapter(new GlideAdapter())
                .setMaxCount(30)
                .exceptGif(true)
                .setActionBarColor(Color.parseColor("#128c7e"), Color.parseColor("#128c7e"), false)
                .setMinCount(3).setActionBarTitleColor(Color.parseColor("#ffffff"))
                .startAlbum();
    }

    private void saveStickerPack(List<Uri> uries, String name, String author) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait a moment while we process your stickers..."); // Setting Message
        progressDialog.setTitle("Processing images"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        new Thread(() -> {
            try {

                Intent intent = new Intent(this, StickerPackDetailsActivity.class);
                intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);

                String identifier = "." + FileUtils.generateRandomIdentifier();
                StickerPack stickerPack = new StickerPack(identifier, name, author, Objects.requireNonNull(uries.toArray())[0].toString(), "", "", "", "");

                //Save the sticker images locally and get the list of new stickers for pack
                List<Sticker> stickerList = StickerPacksManager.saveStickerPackFilesLocally(stickerPack.identifier, uries, this);
                stickerPack.setStickers(stickerList);

                //Generate image tray icon
                String stickerPath = Constants.STICKERS_DIRECTORY_PATH + identifier;
                String trayIconFile = FileUtils.generateRandomIdentifier() + ".png";
                StickerPacksManager.createStickerPackTrayIconFile(uries.get(0), Uri.parse(stickerPath + "/" + trayIconFile), this);
                stickerPack.trayImageFile = trayIconFile;

                //Save stickerPack created to write in json
                StickerPacksManager.stickerPacksContainer.addStickerPack(stickerPack);
                StickerPacksManager.saveStickerPacksToJson(StickerPacksManager.stickerPacksContainer);
                insertStickerPackInContentProvider(stickerPack);

                //Start new activity with stickerpack information
                intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPack);
                startActivity(intent);
                this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }).start();
    }

    private void insertStickerPackInContentProvider(StickerPack stickerPack) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("stickerPack", new Gson().toJson(stickerPack));
        getContentResolver().insert(StickerContentProvider.AUTHORITY_URI, contentValues);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Define.ALBUM_REQUEST_CODE) {
            //TODO: action
            ArrayList<Uri> uries = new ArrayList<>();
            if (resultCode == RESULT_OK) {
                uries = data.getParcelableArrayListExtra(Define.INTENT_PATH);
                if (uries.size() > 0) {
                    this.uries = uries;


                }
            }
        }
    }

    public void save(View view) {

        saveStickerPack(this.uries, "Long" + System.currentTimeMillis(), "HBL" + System.currentTimeMillis());
    }
}