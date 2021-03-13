package com.unusualapps.whatsappstickers.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;

import java.util.ArrayList;
import java.util.List;

public class HomeActivityViewModel extends ViewModel {
    private MutableLiveData<List<PackLocal>> listPackLocal = new MutableLiveData<>();

    public void getListPackLocal(AppDatabase appDatabase) {
        List<PackLocal> packLocals = new ArrayList<>();
        packLocals.addAll(appDatabase.packDao().getAll());
        listPackLocal.postValue(packLocals);
    }


    public MutableLiveData<List<PackLocal>> getListPackLocal() {
        return listPackLocal;
    }
}
