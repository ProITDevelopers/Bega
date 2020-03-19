package com.proitdevelopers.bega.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.FacturaAdapter;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ApiInterfaceEncomenda;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.model.Factura;
import com.proitdevelopers.bega.model.LoginEncomenda;
import com.proitdevelopers.bega.model.UsuarioAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class PedidosActivity extends AppCompatActivity {

    private static final String BASE_URL_ENCOMENDA = "http://35.181.153.234:8086/";

    private String TAG = "PedidosActivity";
    private ProgressBar progressBar;
    RecyclerView recyclerView;

    List<Factura> facturaList;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    private String tokenEncomenda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Encomendas");
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        LoginEncomenda loginADAO = new LoginEncomenda();
        loginADAO.telefone = Common.mCurrentUser.contactoMovel;
        loginADAO.palavraPasse = Common.mCurrentUser.contactoMovel;

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        errorLayout = (RelativeLayout) findViewById(R.id.erroLayout);
        btnTentarDeNovo = (TextView) findViewById(R.id.btn);
        btnTentarDeNovo.setText("Tentar de Novo");

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

//        loginEncomenda(loginADAO);

        verifConecxaoPedidos();




    }



    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);

            coordinatorLayout.setVisibility(View.GONE);

        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinatorLayout.setVisibility(View.VISIBLE);

                errorLayout.setVisibility(View.GONE);
                verifConecxaoPedidos();
            }
        });
    }


    private void verifConecxaoPedidos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                mostarMsnErro();
            } else {
                carregarListaFacturas();
            }
        }
    }

    private void carregarListaFacturas() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Factura>> rv = apiInterface.getTodasFacturas();
        rv.enqueue(new Callback<List<Factura>>() {
            @Override
            public void onResponse(@NonNull Call<List<Factura>> call, @NonNull Response<List<Factura>> response) {

                facturaList = new ArrayList<>();

                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        facturaList = response.body();

                        // Order the list by regist date.
                        Collections.sort(facturaList, new Factura());


                        progressBar.setVisibility(View.GONE);
                        setAdapters(facturaList);


                    }
                } else {
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }
                }



                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Factura>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (!MetodosUsados.conexaoInternetTrafego(PedidosActivity.this,TAG)){
                    mostrarMensagem(PedidosActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(PedidosActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(PedidosActivity.this,R.string.txtProblemaMsg);
                }
            }
        });
    }

    private void setAdapters(List<Factura> facturaList) {

        if (facturaList.size()>0){

            Collections.reverse(facturaList);

            FacturaAdapter facturaAdapter = new FacturaAdapter(this,facturaList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            facturaAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(facturaAdapter);
            facturaAdapter.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {

//                    Factura factura = facturaList.get(position);
//                    if (factura.estado.contains("Caminho")){
//                        Intent intent = new Intent(PedidosActivity.this,MotoBoyTrackingActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("toolbarTitle","Minha Encomenda");
//                        intent.putExtra("idFactura",factura.idFactura);
//                        intent.putExtra("tokenEncomenda",tokenEncomenda);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(PedidosActivity.this, "Estado: "+factura.estado + "\n"+
//                                "Poderá ver no mapa, quando o Estado estiver 'A caminho'!",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//


                }
            });
        }
    }


    private void loginEncomenda(LoginEncomenda loginADAO) {


        ApiInterfaceEncomenda apiInterface = getClientEncomenda().create(ApiInterfaceEncomenda.class);
        Call<UsuarioAuth> call = apiInterface.loginADAOCliente(loginADAO);
        call.enqueue(new Callback<UsuarioAuth>() {
            @Override
            public void onResponse(@NonNull Call<UsuarioAuth> call, @NonNull Response<UsuarioAuth> response) {

                if (response.isSuccessful() && response.body() != null) {
                    UsuarioAuth userToken = response.body();


                    tokenEncomenda = userToken.token_acesso;

                    verifConecxaoPedidos();


                }
            }

            @Override
            public void onFailure(@NonNull Call<UsuarioAuth> call, @NonNull Throwable t) {

            }
        });

    }

    public Retrofit getClientEncomenda() {
        Retrofit retrofit;
        OkHttpClient okHttpClient = initOkHttp();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_ENCOMENDA)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

        return retrofit;
    }

    private OkHttpClient initOkHttp() {


        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);



        return httpClient.build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedidos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.menu_refresh) {
            if (errorLayout.getVisibility() == View.VISIBLE){
                errorLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
            verifConecxaoPedidos();
            return true;
        }


//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favoritos) {
//            startActivity(new Intent(this, FavoritosActivity.class));
//
//            return true;
//        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void mensagemTokenExpirado() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("A sessão expirou!");
        dialog.setMessage("Inicie outra vez a sessão!");
        dialog.setCancelable(false);

        //Set button
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {


                dialogInterface.dismiss();


            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {


                dialogInterface.dismiss();


            }
        });



        dialog.show();
    }

}
