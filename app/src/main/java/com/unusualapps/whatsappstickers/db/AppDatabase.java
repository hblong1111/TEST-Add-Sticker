package com.unusualapps.whatsappstickers.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;

@Database(entities = {StickerLocal.class, PackLocal.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PackDao packDao();

    public abstract StickerDao stickerDao();
}