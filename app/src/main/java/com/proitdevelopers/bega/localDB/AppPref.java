package com.proitdevelopers.bega.localDB;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.proitdevelopers.bega.MyApplication;
import com.proitdevelopers.bega.model.UsuarioPerfil;

public class AppPref {

    private static final String SHARED_PREF_NAME = "my_shared_preff";
    private static AppPref mInstance;
//    private Context mCtx;
    private static final String KEY_USER = "myuser";
    private static final String KEY_AUTH_TOKEN_TIME = "auth_token_time";
    private static final String KEY_AUTH_TOKEN = "auth_user_token";
    private static final String KEY_AUTH_COOKIE_TOKEN = "auth_cookie_token";
    private static final String ListGridViewMode = "list_grid_view_mode";


    private static final String SHARED_PREF_TIME = "sharedPreferencesTime";
    private static final String MILLIS_LEFT = "millisLeft";
    private static final String TIMER_RUNNING = "timerRunning";
    private static final String END_TIME = "endTime";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

//    public AppPref(Context mCtx) {
//        this.mCtx = mCtx;
//    }

//    public static synchronized AppPref getInstance(Context mCtx){
//        if (mInstance==null){
//            mInstance = new AppPref(mCtx);
//        }
//        return mInstance;
//    }

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
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        editor.putString(KEY_USER, json);
        editor.apply();

    }



    public UsuarioPerfil getUser(){
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_USER, null);
//        Usuario usuario = gson.fromJson(json, Usuario.class);

        return  gson.fromJson(json, UsuarioPerfil.class);
    }

    public void saveAuthToken(String authToken) {
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.apply();
    }

    public String getAuthToken() {
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }


    public void saveAuthCookieToken(String authToken) {
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_COOKIE_TOKEN, authToken);
        editor.apply();
    }

    public String getAuthCookieToken() {
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

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
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ListGridViewMode, viewValue);
        editor.apply();
    }

    public int getListGridViewMode() {
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        return sharedPreferences.getInt(ListGridViewMode, 1);
    }






    public void clearAppPrefs(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
