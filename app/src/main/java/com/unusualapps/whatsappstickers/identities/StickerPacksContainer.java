package com.unusualapps.whatsappstickers.identities;

import com.unusualapps.whatsappstickers.whatsapp_api.Sticker;
import com.unusualapps.whatsappstickers.whatsapp_api.StickerPack;

import java.util.ArrayList;
import java.util.List;

public class StickerPacksContainer {
    private String androidPlayStoreLink;
    private String iosAppStoreLink;
    private List<StickerPack> stickerPacks;

    public StickerPacksContainer() {
        stickerPacks = new ArrayList<>();
    }

    public StickerPacksContainer(String androidPlayStoreLink, String iosAppStoreLink, List<StickerPack> stickerPacks) {
        this.androidPlayStoreLink = androidPlayStoreLink;
        this.iosAppStoreLink = iosAppStoreLink;
        this.stickerPacks = stickerPacks;
    }

    public void addStickerPack(StickerPack stickerPack) {
        this.stickerPacks.add(stickerPack);
    }

    public StickerPack removeStickerPack(int index) {
        return this.stickerPacks.remove(index);
    }


    public List<StickerPack> getStickerPacks() {
        return stickerPacks;
    }

}
