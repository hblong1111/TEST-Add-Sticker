package com.unusualapps.whatsappstickers.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.unusualapps.whatsappstickers.model.db_local.PackLocal;

import java.util.List;

@Dao
public interface PackDao {
    @Query("SELECT * FROM PackLocal")
    List<PackLocal> getAll();


    @Insert
    long insert(PackLocal packLocal);

    @Delete
    void delete(PackLocal packLocal);

    @Delete
    void deleteAll(List<PackLocal> packLocals);

}
