package com.unusualapps.whatsappstickers.db;

import android.app.Application;

import androidx.room.Room;

public class DatabaseModule {

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Application application) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(application,
                    AppDatabase.class, "database-name")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
