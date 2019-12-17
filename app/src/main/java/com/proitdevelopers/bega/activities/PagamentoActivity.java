package com.proitdevelopers.bega.activities;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.helper.NotificationHelper;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.LocalEncomenda;
import com.proitdevelopers.bega.model.Order;
import com.proitdevelopers.bega.model.OrderItem;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.msgEnviandoCodigo;
import static com.proitdevelopers.bega.helper.Common.msgErro;
import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.esconderTeclado;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class PagamentoActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "PagamentoActivity";
    NotificationHelper notificationHelper;
    String mNotificationTitle = "Facturação";
    String mNotificationMessage;



    private String tipoPagamento,numero,endereco,latitude,longitude;


    TextView lblStatus;
    AVLoadingIndicatorView loader;
    ImageView iconStatus;
    TextView statusMessage;
    TextView responseTitle;
    TextView btnCheckOrders;
    LinearLayout layoutOrderPlaced;

    private Realm realm;

    List<OrderItem> orderItems;



    @SerializedName("codigoconfirmacao")
    public String codigoConfirmacao;

    @SerializedName("codoperacao")
    public String codigoOperacao;

    private Dialog dialogSenhaEnviarTelefoneCodReset;
    private ShowHidePasswordEditText pinCodigoConfirmacaoTelef;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        notificationHelper = new NotificationHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pagamento");
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
        }

        progressDialog = new ProgressDialog(PagamentoActivity.this);
        progressDialog.setCancelable(false);

        initViews();
        prepareOrder();


    }

    private void initViews() {

        lblStatus = findViewById(R.id.lbl_status);
        loader = findViewById(R.id.loader);
        iconStatus = findViewById(R.id.icon_status);
        statusMessage = findViewById(R.id.status_message);
        responseTitle = findViewById(R.id.title_status);
        btnCheckOrders = findViewById(R.id.btn_check_orders);
        layoutOrderPlaced = findViewById(R.id.layout_order_placed);

        realm = Realm.getDefaultInstance();
        realm.where(CartItemProdutos.class).findAllAsync()
                .addChangeListener(cartItems -> {

                });

        //-------------------------------------------------------------
        //Dialog enviar codigo de confirmacao telefone
        dialogSenhaEnviarTelefoneCodReset = new Dialog(this);
        dialogSenhaEnviarTelefoneCodReset.setContentView(R.layout.layout_enviar_codigo_pagamento);
        dialogSenhaEnviarTelefoneCodReset.setCancelable(false);

        pinCodigoConfirmacaoTelef = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.pinCodigoConfirmacaoTelef);

        Button btn_enviar_cod_resetTelef = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.btn_enviar_cod_resetTelef);
        LinearLayout linearBtnFecharTelef = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.linearBtnFecharTelef);

        btn_enviar_cod_resetTelef.setOnClickListener(this);
        linearBtnFecharTelef.setOnClickListener(this);
        //-------------------------------------------------------------

        btnCheckOrders.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.btn_enviar_cod_resetTelef:
                if (verificarCampoCodigo()){
                    esconderTeclado(PagamentoActivity.this);
                    enviarCodConrfirmacaoPagamento();
                }
                break;

            case R.id.linearBtnFecharTelef:
                limparPinView(pinCodigoConfirmacaoTelef);
                esconderTeclado(PagamentoActivity.this);
                dialogSenhaEnviarTelefoneCodReset.cancel();
                showOrderStatus(false);
                break;

            case R.id.btn_check_orders:
                startActivity(new Intent(PagamentoActivity.this, PedidosActivity.class));
                finish();
                break;



        }


    }

    private void limparPinView(ShowHidePasswordEditText pinCodigoConfirmacaoTelef) {
        pinCodigoConfirmacaoTelef.setText(null);

    }

    private boolean verificarCampoCodigo() {
        codigoConfirmacao = pinCodigoConfirmacaoTelef.getText().toString();


        if (codigoConfirmacao.isEmpty()) {
            mostrarMensagem(PagamentoActivity.this, R.string.txtMsgCondConf);
            pinCodigoConfirmacaoTelef.requestFocus();
            pinCodigoConfirmacaoTelef.setError(msgErro);
            return false;
        }
        if (codigoConfirmacao.length() <2) {
            mostrarMensagem(PagamentoActivity.this, R.string.txtMsgCondConfCod);
            return false;
        }



        return true;

    }

    private void setStatus(int message) {
        lblStatus.setText(message);
    }

    /**
     * STEP 1: Sending all the cart items to server and receiving the
     * unique order id that needs to be sent to PayTM
     */
    private void prepareOrder() {

        setStatus(R.string.msg_preparing_order);

        List<CartItemProdutos> cartItems = realm.where(CartItemProdutos.class).findAll();

        orderItems = new ArrayList<>();
        for (CartItemProdutos cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.produtoId = cartItem.produtos.getIdProduto();
            orderItem.quantidade = cartItem.quantity;


            orderItems.add(orderItem);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verificarConexao();
            }
        }, 1500);



    }

    private void verificarConexao() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){

                Toast.makeText(this, "Network", Toast.LENGTH_SHORT).show();
            } else {
                enviarPedidos();
            }
        }
    }

    private void enviarPedidos() {
        Order order = new Order();
        LocalEncomenda localEncomenda = new LocalEncomenda();

        localEncomenda.nTelefone = numero;
        localEncomenda.pontodeReferencia = endereco;
        localEncomenda.latitude = latitude;
        localEncomenda.longitude = longitude;

        order.localEncomenda = localEncomenda;
        order.orderItems = orderItems;

        if (tipoPagamento.equals("Wallet")){

            facturacaoWallet(order);
        }

        if (tipoPagamento.equals("Multicaixa")){
            facturacaoMulticaixa(order);
        }



    }

    private void facturacaoWallet(Order order) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<String>> facturaWallet = apiInterface.facturaWallet(order);
        facturaWallet.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {

                if (response.isSuccessful()) {

                    if (response.body()!=null){

                        codigoOperacao = response.body().get(0);

                        dialogSenhaEnviarTelefoneCodReset.show();
                    }


//                    showOrderStatus(true);

                } else {

                    showOrderStatus(false);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                showOrderStatus(false);
                if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(PagamentoActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(PagamentoActivity.this,R.string.txtProblemaMsg);
                }


            }
        });
    }

    private void facturacaoMulticaixa(Order order) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> facturaWallet = apiInterface.facturaTPA(order);
        facturaWallet.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    showOrderStatus(true);

                } else {

                    showOrderStatus(false);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                showOrderStatus(false);
                if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(PagamentoActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(PagamentoActivity.this,R.string.txtProblemaMsg);
                }


            }
        });
    }

    private void enviarCodConrfirmacaoPagamento() {

        progressDialog.setMessage(msgEnviandoCodigo);
        progressDialog.show();


        RequestBody codigoconfirmacao = RequestBody.create(MediaType.parse("multipart/form-data"),codigoConfirmacao);
        RequestBody codoperacao = RequestBody.create(MediaType.parse("multipart/form-data"), codigoOperacao);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<String>> enviarCod = apiInterface.enviarConfirCodigoPagamento(codigoconfirmacao,codoperacao);
        enviarCod.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (!response.isSuccessful()) {
//                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
//                    mostrarMensagem(PagamentoActivity.this, "errorResponce.getError()");
                    progressDialog.dismiss();
//                    showOrderStatus(false);
                } else {


                    progressDialog.dismiss();
                    limparPinView(pinCodigoConfirmacaoTelef);
                    dialogSenhaEnviarTelefoneCodReset.cancel();
                    showOrderStatus(true);



                }
            }
            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showOrderStatus(false);
                if (!conexaoInternetTrafego(PagamentoActivity.this,TAG)) {
                    mostrarMensagem(PagamentoActivity.this, R.string.txtMsg);
                } else if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(PagamentoActivity.this, R.string.txtTimeout);
                } else {
                    mostrarMensagem(PagamentoActivity.this, R.string.txtProblemaMsg);
                }

            }
        });



    }


    /*
     * Displaying Order Status on UI. This toggles UI between success and failed cases
     * */
    private void showOrderStatus(boolean isSuccess) {

        loader.setVisibility(View.GONE);
        lblStatus.setVisibility(View.GONE);
        if (isSuccess) {
            iconStatus.setImageResource(R.drawable.ic_checkmark);
            iconStatus.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
            responseTitle.setText(R.string.thank_you);
            statusMessage.setText(R.string.msg_order_placed_successfully);

            mNotificationMessage = statusMessage.getText().toString().trim();

            // as the order placed successfully, clear the cart
            AppDatabase.clearAllCart();


            notificationHelper.createNotification(mNotificationTitle,mNotificationMessage);
        } else {
            iconStatus.setImageResource(R.drawable.ic_camera);
            iconStatus.setColorFilter(ContextCompat.getColor(this, R.color.btn_remove_item));
            responseTitle.setText(R.string.order_failed);
            statusMessage.setText(R.string.msg_order_placed_failed);

            mNotificationMessage = statusMessage.getText().toString().trim();

            notificationHelper.createNotification(mNotificationTitle,mNotificationMessage);
        }

        layoutOrderPlaced.setVisibility(View.VISIBLE);

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
            realm.removeAllChangeListeners();
            realm.close();
        }
    }


}
