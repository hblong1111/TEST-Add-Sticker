package com.unusualapps.whatsappstickers.model.db_local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class PackLocal implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name, author;

    public PackLocal() {
    }

    public PackLocal(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public PackLocal(int idPack) {
        id = idPack;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
