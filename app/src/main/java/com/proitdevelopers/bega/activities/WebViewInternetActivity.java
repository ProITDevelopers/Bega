package com.proitdevelopers.bega.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Common;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewInternetActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    WebView webView;
    Drawable toolbarBK;
    String toolbar_Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_internet);

        if(getIntent()!=null){
            toolbar_Title = getIntent().getStringExtra("bega_manual");
            if (getSupportActionBar()!=null){
                getSupportActionBar().setTitle(toolbar_Title);
            }
        }

        toolbarBK = getResources().getDrawable( R.drawable.toolbar_bk );

        if (getSupportActionBar()!=null){
            getSupportActionBar().setBackgroundDrawable(toolbarBK);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        webView = (WebView) findViewById(R.id.webview);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://tutlane.com");



        verifConecxao();

//        String path = Common.BEGA_MANUAL_USER;
//
//        web_view.requestFocus();
//        web_view.getSettings().setJavaScriptEnabled(true);
//        String myPdfUrl = "gymnasium-wandlitz.de/vplan/vplan.pdf";
//        String url = "https://docs.google.com/viewer?embedded = true&url = "+path;
//        web_view.loadUrl(path);
//        web_view.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
//        web_view.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                if (progress < 100) {
//                    progressDialog.show();
//                }
//                if (progress == 100) {
//                    progressDialog.dismiss();
//                }
//            }
//        });
    }

    private void verifConecxao() {

        if (getBaseContext() != null){
            ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr!=null){
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
//                    mostarMsnErro();
                    progressDialog.dismiss();
                }else{
                    carregarPDFView();
                }
            }


        }

    }



    private void carregarPDFView(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando...");
        progressDialog.setCancelable(false);

//        progressBar.setVisibility(ProgressBar.VISIBLE);
//        anelprogressbar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.loadUrl("https://docs.google.com/viewer?embedded=true&url="+Common.BEGA_MANUAL_USER);
        webView.loadUrl("https://drive.google.com/file/d/1RR_ibOrZFLR_piLOwsO357JbMnvPGvaH/view");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);



            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                view.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");

                if (progress < 100){
                    progressDialog.show();
//                    progressBar.setVisibility(ProgressBar.VISIBLE);
//                    anelprogressbar.setProgress(progress);
                }



                if(progress == 100) {
                    progressDialog.dismiss();
//                    progressBar.setVisibility(ProgressBar.GONE);
//                    anelprogressbar.setVisibility(View.GONE);
                }
            }
        });



    }

    private void mostarMsnErro(){

//        if (errorLayout.getVisibility() == View.GONE){
//            errorLayout.setVisibility(View.VISIBLE);
//            coordinatorLayout.setVisibility(View.GONE);
//        }
//
//        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                coordinatorLayout.setVisibility(View.VISIBLE);
//                errorLayout.setVisibility(View.GONE);
//                verifConecxao(viewType);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_refresh:
                verifConecxao();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            finish();
//        }
//
//
//
//        return super.onOptionsItemSelected(item);
//
//
//    }


}