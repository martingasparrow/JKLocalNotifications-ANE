package com.juankpro.ane.localnotif;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by jpazmino on 11/10/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({AlarmIntentService.class})
public class AlarmIntentServiceTest {
    @Mock
    private Context context;
    @Mock
    private Bundle bundle;
    @Mock
    private Intent intent;
    @Mock
    private NotificationIntentFactory intentFactory;
    @Mock
    private NotificationFactory notificationFactory;
    @Mock
    private NotificationManager notificationManager;
    @Mock
    private LocalNotificationEventDispatcher dispatcher;
    private byte[] data = new byte[]{};
    private Notification notification = new Notification();
    private AlarmIntentService subject;

    private AlarmIntentService getSubject() {
        if (subject == null) {
            subject = new AlarmIntentService();
        }
        return subject;
    }

    private void setup() {
        MockitoAnnotations.initMocks(this);

        when(intent.getExtras()).thenReturn(bundle);

        when(bundle.getString(Constants.NOTIFICATION_CODE_KEY)).thenReturn("KeyCode");
        when(bundle.getByteArray(Constants.ACTION_DATA_KEY)).thenReturn(data);

        try {
            PowerMockito.whenNew(LocalNotificationEventDispatcher.class).withArguments("KeyCode", data)
                    .thenReturn(dispatcher);
        } catch(Exception e) { e.printStackTrace(); }

        when(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager);
    }

    @Test
    public void intentService_onReceiveWhenAppIsInForeground_doesNotDisplayNotification() {
        setup();
        when(dispatcher.dispatchInForeground()).thenReturn(true);
        getSubject().onReceive(context, intent);

        verify(notificationManager, never()).notify(anyString(), anyInt(), any(Notification.class));
    }

    @Test
    public void intentService_onReceiveWhenAppIsNotInForeground_displaysNotification() {
        setup();
        when(dispatcher.dispatchInForeground()).thenReturn(false);

        try {
            PowerMockito.whenNew(NotificationIntentFactory.class).withArguments(context, bundle)
                    .thenReturn(intentFactory);

            PowerMockito.whenNew(NotificationFactory.class).withArguments(context, bundle)
                    .thenReturn(notificationFactory);
        } catch(Exception e) { e.printStackTrace(); }

        when(notificationFactory.create(intentFactory)).thenReturn(notification);

        getSubject().onReceive(context, intent);

        verify(notificationManager).notify("KeyCode", Constants.STANDARD_NOTIFICATION_ID, notification);
    }
}