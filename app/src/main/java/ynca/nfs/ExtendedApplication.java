package ynca.nfs;

import android.app.Application;
import android.content.res.Configuration;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ExtendedApplication extends Application {
        // Called when the application is starting, before any other application objects have been created.
        // Overriding this method is totally optional!
        @Override
        public void onCreate() {
            super.onCreate();
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("font/roboto_regular.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            // Required initialization logic here!
        }

        // Called by the system when the device configuration changes while your component is running.
        // Overriding this method is totally optional!
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
        }

        // This is called when the overall system is running low on memory,
        // and would like actively running processes to tighten their belts.
        // Overriding this method is totally optional!
        @Override
        public void onLowMemory() {
            super.onLowMemory();
        }
    }

