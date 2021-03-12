package com.unusualapps.whatsappstickers.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Pack implements Serializable  {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("list_sticker")
    @Expose
    public List<ListSticker> listSticker = null;

    /**
     * No args constructor for use in serialization
     */
    public Pack() {
    }

    /**
     * @param listSticker
     * @param author
     * @param name
     * @param id
     */
    public Pack(Integer id, String name, String author, List<ListSticker> listSticker) {
        super();
        this.id = id;
        this.name = name;
        this.author = author;
        this.listSticker = listSticker;
    }

    public class ListSticker implements  Serializable{

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("url_image")
        @Expose
        public String urlImage;

        /**
         * No args constructor for use in serialization
         */
        public ListSticker() {
        }

        /**
         * @param id
         * @param urlImage
         */
        public ListSticker(Integer id, String urlImage) {
            super();
            this.id = id;
            this.urlImage = urlImage;
        }


    }


}