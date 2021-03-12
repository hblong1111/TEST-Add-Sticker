package com.unusualapps.whatsappstickers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.unusualapps.whatsappstickers.Event.HomeActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.constants.Constants;
import com.unusualapps.whatsappstickers.fragment.my.HomeFragment;
import com.unusualapps.whatsappstickers.identities.StickerPacksContainer;
import com.unusualapps.whatsappstickers.model.Data;
import com.unusualapps.whatsappstickers.model.Pack;
import com.unusualapps.whatsappstickers.utils.Common;
import com.unusualapps.whatsappstickers.utils.FileUtils;
import com.unusualapps.whatsappstickers.utils.StickerPacksManager;
import com.unusualapps.whatsappstickers.whatsapp_api.AddStickerPackActivity;
import com.unusualapps.whatsappstickers.whatsapp_api.Sticker;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerContentProvider;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerPack;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerPackDetailsActivity;
import com.unusualapps.whatsappstickers.whatsapp_api.WhitelistCheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AddStickerPackActivity implements HomeActivityEvent {
    private BottomNavigationView navBottom;

    private ArrayList<Uri> uries;
    private Context context;
    List<File> files;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navBottom = findViewById(R.id.navBottom);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(this)).commit();

        navBottom.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:

                    break;
                case R.id.menu_create:

                    break;
            }

            return true;
        });

        StickerPacksManager.stickerPacksContainer = new StickerPacksContainer("", "", StickerPacksManager.getStickerPacks(this));
        uries = new ArrayList<>();
        files = new ArrayList<>();
        context = this;


        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);

    }

    @Override
    public void addPackToWhatsApp(Pack pack) {

        boolean b = WhitelistCheck.isWhitelisted(this, "." + pack.name+Common.KEY_APP);

        if (!b) {
            new TaskGetUriFromUrl().execute(pack);
        }else {
            Toast.makeText(context, "This package has been added WhatsApp!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void seeDetailPack(Pack pack) {
        Intent intent = new Intent(this, PackDetailActivity.class);
        intent.putExtra(Common.CODE_PUT_PACK, pack);
        startActivity(intent);
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

                String identifier = "." + name+Common.KEY_APP;
                StickerPack stickerPack = new StickerPack(identifier, name, author, Objects.requireNonNull(uries.toArray())[0].toString(), "", "", "", "");

                //Save the sticker images locally and get the list of new stickers for pack
                List<Sticker> stickerList = StickerPacksManager.saveStickerPackFilesLocally(stickerPack.identifier, uries, this);
                stickerPack.setStickers(stickerList);

                //Generate image tray icon
                String stickerPath = Constants.STICKERS_DIRECTORY_PATH + identifier;
//                String trayIconFile = FileUtils.generateRandomIdentifier() + ".png";
                String trayIconFile = System.currentTimeMillis()+ ".png";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
