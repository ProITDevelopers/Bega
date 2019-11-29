package com.proitdevelopers.bega.apiCookie;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor  implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            String cookies = originalResponse.headers().get("Set-Cookie");
//            AppPref.getInstance().saveAuthCookieToken(cookies);
        }
        return originalResponse;
    }
}
