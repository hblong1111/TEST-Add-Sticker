package com.unusualapps.whatsappstickers.Event;

import com.unusualapps.whatsappstickers.model.Data;
import com.unusualapps.whatsappstickers.model.Pack;

public interface HomeActivityEvent {

    void addPackToWhatsApp(Pack pack);

    void seeDetailPack(Pack pack);

    void getListPackLocal();
}
