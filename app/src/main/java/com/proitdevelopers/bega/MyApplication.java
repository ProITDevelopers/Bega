package com.proitdevelopers.bega;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.proitdevelopers.bega.mySignalR.MySignalRService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.proitdevelopers.bega.helper.Common.DB_REALM;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    private final Context mContext = this;
    private MySignalRService mService;
    private boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        initRealm();

//        startService(new Intent(this,MySignalRService.class));
        Intent intent = new Intent();
        intent.setClass(mContext, MySignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            MySignalRService.LocalBinder binder = (MySignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
