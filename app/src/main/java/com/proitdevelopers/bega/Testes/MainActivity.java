package com.proitdevelopers.bega.Testes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.activities.ProdutosActivity;
import com.proitdevelopers.bega.activities.RegistroActivity;
import com.proitdevelopers.bega.activities.SplashActivity;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.EstabelecimentoAdapter;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;

import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.model.Estabelecimento;
import com.proitdevelopers.bega.model.Produtos;
import com.proitdevelopers.bega.model.UsuarioPerfil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_ONE;
import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_THREE;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogTerminarSessao;

    private String TAG = "MainActivity";

    List<Estabelecimento> estabelecimentoList = new ArrayList<>();


    private RecyclerView recyclerView;
    private EstabelecimentoAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;


    TextView txtUserInfo;


    private UsuarioPerfil usuarioPerfil = new UsuarioPerfil();

    boolean txtUserIn = false;

    List<Produtos> produtosList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Common.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bega");
        setSupportActionBar(toolbar);



        txtUserInfo = findViewById(R.id.txtUserInfo);

        gridLayoutManager = new GridLayoutManager(this, AppPref.getInstance().getListGridViewMode());
        recyclerView = (RecyclerView) findViewById(R.id.rv);

        dialogTerminarSessao = new Dialog(this);
        dialogTerminarSessao.setContentView(R.layout.layout_terminar_sessao);
        Button dialog_btn_cancelar_sessao = dialogTerminarSessao.findViewById(R.id.dialog_btn_cancelar_sessao);
        Button dialog_btn_terminar_sessao = dialogTerminarSessao.findViewById(R.id.dialog_btn_terminar_sessao);
        dialog_btn_cancelar_sessao.setOnClickListener(this);
        dialog_btn_terminar_sessao.setOnClickListener(this);





        verificaoPerfil();
//        verifConecxaoEstabelecimentos();
//        verifProdutos();





    }




    private void verifConecxaoEstabelecimentos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                Toast.makeText(this, "Network offline", Toast.LENGTH_SHORT).show();
            } else {
                carregarListaEstabelicimentos();
            }
        }
    }



    private void carregarListaEstabelicimentos() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getAllEstabelecimentos();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                estabelecimentoList = new ArrayList<>();

                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Estabelecimento: "+response.message(), Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(MainActivity.this, "Estabelecimento: "+response.message(), Toast.LENGTH_SHORT).show();

                    if (response.body()!=null){

                        estabelecimentoList = response.body();

//                        setAdapters(estabelecimentoList);

                        verifProdutos();

                    } else {
                        Toast.makeText(MainActivity.this, "Estabelecimento: "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {

                if (!conexaoInternetTrafego(MainActivity.this)){
                    mostrarMensagem(MainActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(MainActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(MainActivity.this,R.string.txtProblemaMsg);
                }
            }
        });
    }

    private void setAdapters(List<Estabelecimento> estabelecimentoList) {

        if (estabelecimentoList.size()>0){
            itemAdapter = new EstabelecimentoAdapter(this, estabelecimentoList, gridLayoutManager);
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(gridLayoutManager);

            itemAdapter.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Estabelecimento estabelecimento = estabelecimentoList.get(position);
                    Toast.makeText(MainActivity.this, "Id: "+estabelecimento.estabelecimentoID+", "+estabelecimento.nomeEstabelecimento, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ProdutosActivity.class);
                    intent.putExtra("idEstabelecimento",estabelecimento.estabelecimentoID);
                    intent.putExtra("logotipo",estabelecimento.logotipo);
                    intent.putExtra("nomeEstabelecimento",estabelecimento.nomeEstabelecimento);
                    startActivity(intent);

                }
            });
        }


    }


    private void verifProdutos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                Toast.makeText(this, "Network offline", Toast.LENGTH_SHORT).show();
            } else {
                carregarProdutos();
            }
        }
    }

    private void carregarProdutos() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Produtos>> rv = apiInterface.getAllProdutos();
        rv.enqueue(new Callback<List<Produtos>>() {
            @Override
            public void onResponse(@NonNull Call<List<Produtos>> call, @NonNull Response<List<Produtos>> response) {

               produtosList = new ArrayList<>();


                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Produtos: "+response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Produtos: "+response.message(), Toast.LENGTH_SHORT).show();
                    if (response.body()!=null){

                        produtosList = response.body();

                        // store products in db
                        AppDatabase.saveProducts(produtosList);

                        setAdapters(estabelecimentoList);

                    } else {
                        Toast.makeText(MainActivity.this, "Produtos: "+response.message(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Produtos>> call, @NonNull Throwable t) {

                if (!MetodosUsados.conexaoInternetTrafego(MainActivity.this,TAG)){
                    mostrarMensagem(MainActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(MainActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(MainActivity.this,R.string.txtProblemaMsg);
                }
            }
        });
    }


    private void mensagemTokenExpirado() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("A sessão expirou!");
        dialog.setMessage("Inicie outra vez a sessão!");

        //Set button
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                logOut();

            }
        });




        dialog.show();
    }




    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dialog_btn_cancelar_sessao:
                dialogTerminarSessao.cancel();
                break;


            case R.id.dialog_btn_terminar_sessao:
                logOut();
                break;



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_navigation_bottom; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.menu_switch_layout) {
            switchLayout();
            switchIcon(item);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            verifConecxaoEstabelecimentos();
            Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_perfil) {

            if(!txtUserIn){
                txtUserIn = true;
                txtUserInfo.setVisibility(View.GONE);
            } else {
                txtUserIn = false;
                txtUserInfo.setVisibility(View.VISIBLE);
            }

            return true;
        }

        if (id == R.id.action_registro) {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
            return true;
        }



        if (id == R.id.action_logout) {
            dialogTerminarSessao.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //            invalidateOptionsMenu();
        MenuItem item =  menu.findItem(R.id.menu_switch_layout);
        loadIcon(item);

        return super.onPrepareOptionsMenu(menu);
    }

    private void loadIcon(MenuItem item) {
        if (AppPref.getInstance().getListGridViewMode() ==  SPAN_COUNT_THREE) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_3));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_1));
        }
    }

    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            gridLayoutManager.setSpanCount(SPAN_COUNT_THREE);

        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);

        }


        try {
            itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppPref.getInstance().saveListGridViewMode(gridLayoutManager.getSpanCount());

    }

    private void switchIcon(MenuItem item) {
//        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_THREE) {

        if (gridLayoutManager.getSpanCount() ==  SPAN_COUNT_THREE) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_3));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_1));
        }




    }

    private void verificaoPerfil() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){

                carregarMeuPerfilOffline();
                Toast.makeText(this, "Network offline", Toast.LENGTH_SHORT).show();
            } else {
                carregarMeuPerfil();
            }
        }
    }

    private void carregarMeuPerfilOffline() {


        try {

            //carregar dados do Usuario

            Common.mCurrentUser = AppPref.getInstance().getUser();
            txtUserInfo.setText(
                    "nomeCompletoOff: "+Common.mCurrentUser.nomeCompleto+"\n"+
                            "userNameOff: "+Common.mCurrentUser.userName+"\n"+
                            "sexoOff: "+Common.mCurrentUser.sexo+"\n"+
                            "imageOff: "+Common.mCurrentUser.imagem
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void carregarMeuPerfil() {


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<UsuarioPerfil>  usuarioCall = apiInterface.getMeuPerfil(cookie);
        Call<List<UsuarioPerfil>>  usuarioCall = apiInterface.getMeuPerfil();
        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Perfil: "+response.message(), Toast.LENGTH_SHORT).show();


                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);

                        Common.mCurrentUser = usuarioPerfil;


                        verifConecxaoEstabelecimentos();

                    }


                } else {

                    Toast.makeText(MainActivity.this, "Perfil: "+response.message(), Toast.LENGTH_SHORT).show();

                    txtUserInfo.setText(
                            String.valueOf("Erro "+response.code())+"\n"+
                                    response.message());

                    mensagemTokenExpirado();

                }








            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {

                txtUserInfo.setText(t.getMessage());
              if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(MainActivity.this,R.string.txtTimeout);
              }else {
                    mostrarMensagem(MainActivity.this,R.string.txtProblemaMsg);
              }


            }
        });
    }





    private void logOut() {

        AppDatabase.clearData();
        AppPref.getInstance().clearAppPrefs();
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }


    public void mostrarMensagem(Context mContexto, int mensagem) {
        Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show();
    }

    private boolean conexaoInternetTrafego(Context context){
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
}
