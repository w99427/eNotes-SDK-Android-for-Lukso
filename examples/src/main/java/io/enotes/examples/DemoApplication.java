package io.enotes.examples;

import android.app.Application;

import io.enotes.sdk.core.ENotesSDK;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ENotesSDK.config.debugCard=true;
    }


}
