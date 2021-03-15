package com.unusualapps.whatsappstickers.utils;

import android.content.Context;
import android.util.Log;

import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.model.db_local.StickerLocal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

public class StickerPackLocalUtils {

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
