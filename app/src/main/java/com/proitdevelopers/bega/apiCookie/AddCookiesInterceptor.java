package com.proitdevelopers.bega.apiCookie;

import android.text.TextUtils;

import com.proitdevelopers.bega.localDB.AppPref;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String cookie = AppPref.getInstance().getAuthCookieToken();
        String token = AppPref.getInstance().getAuthToken();
        builder.addHeader("Content-type", "application/json");
        builder.addHeader("Accept", "application/json");

//        if (!TextUtils.isEmpty(cookie)) {
//            builder.addHeader("Cookie", cookie);
//        }

        if (!TextUtils.isEmpty(token)) {
            builder.header("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}
