package com.unusualapps.whatsappstickers.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;

import java.util.List;

@Dao
public interface StickerDao {
    @Query("SELECT * FROM StickerLocal")
    List<StickerLocal> getAll();


    @Insert
    void insertAll(StickerLocal stickerLocal);

    @Delete
    void delete(StickerLocal stickerLocal);
}
