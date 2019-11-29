package com.proitdevelopers.bega;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.proitdevelopers.bega.helper.Common.DB_REALM;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        initRealm();


    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration defaultRealmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(DB_REALM)
                .build();
        Realm.setDefaultConfiguration(defaultRealmConfiguration);
        Realm.compactRealm(defaultRealmConfiguration);
    }



    public static MyApplication getInstance() {
        return mInstance;
    }
}
