package com.unusualapps.whatsappstickers.activities;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.unusualapps.whatsappstickers.Event.PackDetailActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.adapter.StickerDetailAdapter;
import com.unusualapps.whatsappstickers.constants.Constants;
import com.unusualapps.whatsappstickers.identities.StickerPacksContainer;
import com.unusualapps.whatsappstickers.model.Pack;
import com.unusualapps.whatsappstickers.utils.Common;
import com.unusualapps.whatsappstickers.utils.FileUtils;
import com.unusualapps.whatsappstickers.utils.StickerPacksManager;
import com.unusualapps.whatsappstickers.whatsapp_api.AddStickerPackActivity;
import com.unusualapps.whatsappstickers.whatsapp_api.Sticker;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerContentProvider;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerPack;
import com.unusualapps.whatsappstickers.whatsapp_api.WhitelistCheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PackDetailActivity extends AddStickerPackActivity implements View.OnClickListener, PackDetailActivityEvent {

    Pack pack;

    private ImageButton btnBack;
    private LinearLayout btnAddPack;
    private ImageView img;
    private TextView tvName;
    private TextView tvAuthor;
    private RecyclerView rcv;

    StickerDetailAdapter adapter;
    private Dialog alertDialog;
    private Context context;
    private ProgressDialog dialog;


    private ArrayList<Uri> uries;
    List<File> files;


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


        StickerPacksManager.stickerPacksContainer = new StickerPacksContainer("", "", StickerPacksManager.getStickerPacks(this));
        uries = new ArrayList<>();
        files = new ArrayList<>();
        context = this;

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPack:
                addPack(pack);
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    private void addPack(Pack pack) {
        boolean b = WhitelistCheck.isWhitelisted(this, "." + pack.name + Common.KEY_APP);

        if (!b) {
            StickerPacksManager.stickerPacksContainer.getStickerPacks().clear();
            new TaskGetUriFromUrl().execute(pack);
        } else {
            Toast.makeText(context, "This package has been added WhatsApp!", Toast.LENGTH_SHORT).show();
        }
    }


    class TaskGetUriFromUrl extends AsyncTask<Pack, Void, Pack> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
            uries.clear();
        }

        @Override
        protected Pack doInBackground(Pack... packs) {
            try {
                for (int i = 0; i < packs[0].listSticker.size(); i++) {
                    uries.add(Uri.fromFile(saveImageToExternal(getBitmapFromURL(packs[0].listSticker.get(i).urlImage))));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return packs[0];
        }

        @Override
        protected void onPostExecute(Pack pack) {
            super.onPostExecute(pack);
            saveStickerPack(uries, pack.name, pack.author);
        }
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

                //fixme: create pack

                String identifier = "." + name + Common.KEY_APP;
                StickerPack stickerPack = new StickerPack(identifier, name, author, Objects.requireNonNull(uries.toArray())[0].toString(), "", "", "", "");

                //Save the sticker images locally and get the list of new stickers for pack
                List<Sticker> stickerList = StickerPacksManager.saveStickerPackFilesLocally(stickerPack.identifier, uries, this);
                stickerPack.setStickers(stickerList);

                //Generate image tray icon
                String stickerPath = Constants.STICKERS_DIRECTORY_PATH + identifier;
//                String trayIconFile = FileUtils.generateRandomIdentifier() + ".png";
                String trayIconFile = System.currentTimeMillis() + ".png";
                StickerPacksManager.createStickerPackTrayIconFile(uries.get(0), Uri.parse(stickerPath + "/" + trayIconFile), this);
                stickerPack.trayImageFile = trayIconFile;

                //Save stickerPack created to write in json
                StickerPacksManager.stickerPacksContainer.addStickerPack(stickerPack);
                StickerPacksManager.saveStickerPacksToJson(StickerPacksManager.stickerPacksContainer);
                insertStickerPackInContentProvider(stickerPack);

                dialog.dismiss();
                addStickerPackToWhatsApp(stickerPack.identifier, stickerPack.name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < files.size(); i++) {
                files.get(i).delete();
            }
            progressDialog.dismiss();
        }).start();
    }

    private void insertStickerPackInContentProvider(StickerPack stickerPack) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("stickerPack", new Gson().toJson(stickerPack));
        getContentResolver().insert(StickerContentProvider.AUTHORITY_URI, contentValues);
    }

    public Bitmap getBitmapFromURL(String src) {

        Bitmap bitmap = null;
        try {
            URL url = new URL(src);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }

        return bitmap;
    }


    public File saveImageToExternal(Bitmap bm) throws IOException {
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //Creates app specific folder
        path.mkdirs();
        File imageFile = new File(path, System.currentTimeMillis() + ".jpg"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.d("hblong", "TestActivity | onScanCompleted: " + path);
                    Log.d("hblong", "TestActivity | onScanCompleted: " + uri.toString());
                }
            });
        } catch (Exception e) {
            throw new IOException();
        }

        files.add(imageFile);
        return imageFile;
    }

    public File saveImageFile(Bitmap bm) throws IOException {
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)); //Creates app specific folder
        path.mkdirs();
        String fileName = pack.name + "-" + pack.author + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(path, fileName); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.d("hblong", "TestActivity | onScanCompleted: " + path);
                    Log.d("hblong", "TestActivity | onScanCompleted: " + uri.toString());
                }
            });
        } catch (Exception e) {
            throw new IOException();
        }

        files.add(imageFile);
        return imageFile;
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
        btnAddPack.setOnClickListener(v -> {
            addPack(pack);
        });
        btnSave.setOnClickListener(v -> {
            new TaskDownloadImage().execute(listSticker.urlImage);
        });

        btnShare.setOnClickListener(v -> {
            //todo: share app
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

    class TaskDownloadImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Bitmap bitmap = getBitmapFromURL(strings[0]);
            File file = null;
            try {
                file = saveImageFile(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.exists() ? "Save success!" : "Error";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }


}