package com.proitdevelopers.bega.localDB;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.proitdevelopers.bega.MyApplication;
import com.proitdevelopers.bega.model.UsuarioPerfil;

public class AppPref {

    private static final String SHARED_PREF_NAME = "my_shared_preff";
    private static AppPref mInstance;

    private static final String KEY_USER = "myuser";
    private static final String KEY_AUTH_TOKEN_TIME = "auth_token_time";
    private static final String KEY_AUTH_TOKEN = "auth_user_token";
    private static final String KEY_AUTH_COOKIE_TOKEN = "auth_cookie_token";
    private static final String ListGridViewMode = "list_grid_view_mode";

    private static final String MILLIS_LEFT = "millisLeft";
    private static final String TIMER_RUNNING = "timerRunning";
    private static final String END_TIME = "endTime";
    private static final long START_TIME_IN_MILLIS = 100000;



    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;


    public static synchronized AppPref getInstance() {
        if (mInstance == null) {
            mInstance = new AppPref(MyApplication.getInstance().getApplicationContext());
        }
        return mInstance;
    }

    private AppPref(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void saveUser(UsuarioPerfil usuario){

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        editor.putString(KEY_USER, json);
        editor.apply();

    }



    public UsuarioPerfil getUser(){

        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_USER, null);


        return  gson.fromJson(json, UsuarioPerfil.class);
    }

    public void saveAuthToken(String authToken) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.apply();
    }

    public String getAuthToken() {


        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }


    public void saveAuthCookieToken(String authToken) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_COOKIE_TOKEN, authToken);
        editor.apply();
    }

    public String getAuthCookieToken() {

        return sharedPreferences.getString(KEY_AUTH_COOKIE_TOKEN, null);
    }

    public void saveTokenTime(String tokenTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN_TIME, tokenTime);
        editor.apply();
    }

    public String getTokenTime() {

        return sharedPreferences.getString(KEY_AUTH_TOKEN_TIME, null);
    }

    public void saveListGridViewMode(int viewValue) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ListGridViewMode, viewValue);
        editor.apply();
    }

    public int getListGridViewMode() {


        return sharedPreferences.getInt(ListGridViewMode, 1);
    }



    public void saveTimeCLOCK(long mTimeLeftInMillis,boolean mTimerRunning,long mEndTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(MILLIS_LEFT, mTimeLeftInMillis);
        editor.putBoolean(TIMER_RUNNING, mTimerRunning);
        editor.putLong(END_TIME, mEndTime);
        editor.apply();
    }

    public long getMILLIS_LEFT() {

        return sharedPreferences.getLong(MILLIS_LEFT, START_TIME_IN_MILLIS);
    }

    public boolean getTIMER_RUNNING() {

        return sharedPreferences.getBoolean(TIMER_RUNNING, false);
    }

    public long getEND_TIME() {

        return sharedPreferences.getLong(END_TIME, 0);
    }




    public void clearAppPrefs(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
