package com.proitdevelopers.bega.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.CheckoutAdapter;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.CheckoutOrder;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CheckOutActivity extends AppCompatActivity {


    private String tipoPagamento,numero,endereco,latitude,longitude;

    private int totalItems;
    private String totalCart;
    private String TAG = "CheckOutActivity";
    private RecyclerView recyclerView;
    private Button btn_checkout;

    List<CheckoutOrder> checkOutList = new ArrayList<>();

    CheckoutOrder checkoutOrder = new CheckoutOrder();

    private Realm realm;
    private CheckoutAdapter mAdapter;
    private RealmResults<CartItemProdutos> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Confirmação");
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent()!=null){
            tipoPagamento = getIntent().getStringExtra("tipoPagamento");
            numero = getIntent().getStringExtra("numero");
            endereco = getIntent().getStringExtra("endereco");
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
            totalItems = getIntent().getIntExtra("totalItems",0);
            totalCart = getIntent().getStringExtra("totalCart");
        }

        recyclerView = findViewById(R.id.recyclerView);
        btn_checkout = findViewById(R.id.btn_checkout);

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        checkoutOrder.itens = cartItems;
        checkoutOrder.itens_cart = totalItems;
        checkoutOrder.total_cart = totalCart;
        checkoutOrder.metododPagamento = tipoPagamento;
        checkoutOrder.contacto = numero;
        checkoutOrder.endereco = endereco;


        checkOutList.add(checkoutOrder);


        mAdapter = new CheckoutAdapter(this, checkOutList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifConecxao();
            }
        });






    }

    private void verifConecxao() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                Toast.makeText(this, "Verifique a sua conexão à internet", Toast.LENGTH_SHORT).show();;
            } else {

                abrirActividadePagamento();
            }
        }
    }

    private void abrirActividadePagamento() {
        Intent intent = new Intent(CheckOutActivity.this,PagamentoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tipoPagamento",tipoPagamento);
        intent.putExtra("numero",numero);
        intent.putExtra("endereco",endereco);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.close();
        }


    }

}
