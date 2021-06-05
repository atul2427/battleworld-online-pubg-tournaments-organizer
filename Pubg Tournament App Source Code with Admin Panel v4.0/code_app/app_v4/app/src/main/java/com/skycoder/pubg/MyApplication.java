package com.skycoder.pubg;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onesignal.OneSignal;


public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //OneSignal.setRequiresUserPrivacyConsent(true);
        OneSignal.startInit(this).unsubscribeWhenNotificationsAreDisabled(true).init();
        Fresco.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
