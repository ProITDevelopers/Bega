package com.proitdevelopers.bega.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.FacturaAdapter;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.model.Factura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class PedidosActivity extends AppCompatActivity {

    private String TAG = "PedidosActivity";
    private ProgressBar progressBar;
    RecyclerView recyclerView;

    List<Factura> facturaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Pedidos");
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        verifConecxaoPedidos();


    }

    private void verifConecxaoPedidos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                Toast.makeText(this, "Network offline", Toast.LENGTH_SHORT).show();
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

                if (!response.isSuccessful()) {
                    Toast.makeText(PedidosActivity.this, "fatura: "+response.message(), Toast.LENGTH_SHORT).show();
                } else {



                    if (response.body()!=null){
                        facturaList = response.body();

                        // Order the list by regist date.
                        Collections.sort(facturaList, new Factura());


                        progressBar.setVisibility(View.GONE);
                        setAdapters(facturaList);


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
                    Toast.makeText(PedidosActivity.this, "Pagamento: "+facturaList.get(position).metododPagamento, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}