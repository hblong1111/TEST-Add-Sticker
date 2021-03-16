package com.unusualapps.whatsappstickers.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;
import com.unusualapps.whatsappstickers.whatsapp_api.Sticker;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

public class StickerPackLocalUtils {

    public static void deleteSticker(AppDatabase db, StickerLocal sticker) {
        deleteStickerCache(sticker);
        db.stickerDao().delete(sticker);
    }

    private static void deleteStickerCache(StickerLocal stickerLocal) {
        File file = null;
        try {
            file = new File(new URI(stickerLocal.getUriString()));
            file.delete();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("hblong", "StickerPackLocalUtils | deleteStickerCache: ", e);
        }

    }

    public static void deletePackLocal(AppDatabase db, int idPack) {
        List<StickerLocal> stickerLocals = db.stickerDao().getAllForPack(idPack);

        deleteListStickerInCache(stickerLocals);

        db.stickerDao().deleteAll(stickerLocals);
        db.packDao().delete(new PackLocal(idPack));
    }

    private static void deleteListStickerInCache(List<StickerLocal> stickerLocals) {
        for (int i = 0; i < stickerLocals.size(); i++) {
            try {
                File file = new File(new URI(stickerLocals.get(i).getUriString()));

                file.delete();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.e("hblong", "StickerPackLocalUtils | deleteListStickerInCache: ", e);
            }
        }
    }
}
