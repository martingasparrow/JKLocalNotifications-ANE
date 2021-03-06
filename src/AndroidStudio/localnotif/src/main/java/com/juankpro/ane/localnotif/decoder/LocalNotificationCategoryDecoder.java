package com.juankpro.ane.localnotif.decoder;

import com.adobe.fre.FREContext;
import com.juankpro.ane.localnotif.category.LocalNotificationAction;
import com.juankpro.ane.localnotif.category.LocalNotificationCategory;

/**
 * Created by juank on 11/22/2017.
 */

public class LocalNotificationCategoryDecoder extends FREDecoder<LocalNotificationCategory> {
    public LocalNotificationCategoryDecoder(FREContext context) {
        super(context);
    }

    @Override
    protected LocalNotificationCategory decode() {
        LocalNotificationCategory localNotificationCategory = new LocalNotificationCategory();
        localNotificationCategory.identifier = decodeString("identifier", localNotificationCategory.identifier);
        localNotificationCategory.actions = decodeArray("actions", getActionDecoder(), LocalNotificationAction.class);
        return localNotificationCategory;
    }

    private LocalNotificationActionDecoder getActionDecoder() {
        return new LocalNotificationActionDecoder(getContext());
    }
}
