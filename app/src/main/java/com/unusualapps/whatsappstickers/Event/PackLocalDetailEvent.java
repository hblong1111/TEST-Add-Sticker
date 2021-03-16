package com.unusualapps.whatsappstickers.Event;

import android.net.Uri;

import java.util.List;

public interface PackLocalDetailEvent {
    void itemClick(int position);

    void addPackToWhatsApp(List<Uri> uris, String namePack, String author);
}
