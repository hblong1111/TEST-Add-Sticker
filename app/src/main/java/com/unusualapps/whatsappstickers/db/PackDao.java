package com.unusualapps.whatsappstickers.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.unusualapps.whatsappstickers.model.db_local.PackLocal;

import java.util.List;

@Dao
public interface PackDao {
    @Query("SELECT * FROM PackLocal")
    List<PackLocal> getAll();

    @Query("SELECT * FROM PackLocal where id=:id")
    PackLocal findById(int id);


    @Insert
    long insert(PackLocal packLocal);

    @Delete
    void delete(PackLocal packLocal);

    @Update
    void update(PackLocal packLocal);

    @Delete
    void deleteAll(List<PackLocal> packLocals);

}
