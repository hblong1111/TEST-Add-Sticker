package com.unusualapps.whatsappstickers.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;

import java.util.List;

public class PackLocalDetailActivityViewModel extends ViewModel {
    private MutableLiveData<PackLocal> packLocal = new MutableLiveData<>();
    private MutableLiveData<List<StickerLocal>> stickerLocal = new MutableLiveData<>();

    public void getListSticker(AppDatabase db,int id) {
        List<StickerLocal> list = db.stickerDao().getAll();
        stickerLocal.postValue(list);
    }


    public MutableLiveData<PackLocal> getPackLocal() {
        return packLocal;
    }

    public MutableLiveData<List<StickerLocal>> getStickerLocal() {
        return stickerLocal;
    }
}
