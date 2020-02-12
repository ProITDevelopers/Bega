package com.proitdevelopers.bega.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class MetodosUsados {

    private static String TAG = "FalhaSis";


    public static void mostrarMensagem(Context mContexto, int mensagem) {
        Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show();
    }


    public static void mostrarMensagemTextView(TextView textView, String valorString) {
        textView.setText(valorString);
    }

    public static void mostrarMensagem(Context mContexto, String mensagem) {
        Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show();
    }

    public static boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static String removeAcentos(String text) {
        return text == null ? null :
                Normalizer.normalize(text, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


    public static void esconderTeclado(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

                if (inputMethodManager!=null)
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e){
            Log.i(TAG,"esconder teclado " + e.getMessage() );
        }
    }

    public static void changeStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(color);
        }
    }
    public static void shareTheApp(Context context) {

        final String appPackageName = context.getPackageName();
        String appName = context.getString(R.string.app_name);
        String appCategory = "Snack-foods!";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String postData = "Obtenha o aplicativo " + appName + " para ter acesso aos melhores " + appCategory + "\n" + "https://play.google.com/store/apps/details?id=" + appPackageName;

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Baixar Agora!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, postData);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Partilhar Link da App"));
    }

    public static boolean conexaoInternetTrafego(Context context, String TAG){
        String site = "www.google.com";
        WebView webViewInternet = new WebView(context);
        final boolean[] valorRetorno = new boolean[1];

        webViewInternet.setWebViewClient(new WebViewClient());
        webViewInternet.loadUrl(site);

        webViewInternet.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String descricaoErro, String failingUrl) {
                super.onReceivedError(view, errorCode, descricaoErro, failingUrl);
                if (errorCode == -2) {
                    valorRetorno[0] = false;
                    Log.i(TAG,"webView ERROR " + descricaoErro );
                    Log.i(TAG,"webView ERROR " + errorCode );
                }
            }
        });

        webViewInternet.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                valorRetorno[0] = true;
                Log.i(TAG,"webView " + progress );
            }
        });
        Log.i(TAG,"webView " + valorRetorno[0]);

        return valorRetorno[0];
    }


    public static boolean isConnected(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }






}
