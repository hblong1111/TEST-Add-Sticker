package com.unusualapps.whatsappstickers.model.db_local;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StickerLocal {
    @PrimaryKey(autoGenerate = true)
    private int id;


    private int idPack;

    private String uriString;

    public StickerLocal() {
    }

    public StickerLocal(int id, int idPack, String uriString) {
        this.id = id;
        this.idPack = idPack;
        this.uriString = uriString;
    }

    public int getId() {
        return id;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPack() {
        return idPack;
    }

    public void setIdPack(int idPack) {
        this.idPack = idPack;
    }

    public Uri getUri() {
        return Uri.parse(uriString);
    }

    public void setUri(Uri uri) {
        this.uriString = uri.toString();
    }
}
