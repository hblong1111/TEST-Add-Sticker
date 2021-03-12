package com.unusualapps.whatsappstickers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {

    @SerializedName("list_pack")
    @Expose
    public List<Pack> listPack = null;

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     * @param listPack
     */
    public Data(List<Pack> listPack) {
        super();
        this.listPack = listPack;
    }


}
