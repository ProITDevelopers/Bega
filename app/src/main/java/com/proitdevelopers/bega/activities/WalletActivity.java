package com.proitdevelopers.bega.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.proitdevelopers.bega.model.Wallet;
import com.proitdevelopers.bega.model.WalletRequest;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class WalletActivity extends AppCompatActivity {

    private static final String TAG = "WalletActivity";
    private RelativeLayout cardView_hint;
    private CircleImageView imageView;
    private TextView txtNomeCompleto,txtTelefone,txtTelefoneAlternativo,txtEmail,txtContaNumber,txtSaldo;

    ConstraintLayout walletContaLayout;
    AppCompatEditText editTextBI,editTextDataNasc;
    Button btnCriarWallet;

    String bi, dataNasc, data_toShow, date;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    ProgressDialog progressDialog;
    View raiz;

    long minDateMilliseconds,maxDateMilliseconds;

    Wallet wallet = new Wallet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Carteira");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        progressDialog = new ProgressDialog(WalletActivity.this);
        progressDialog.setCancelable(false);

        //carregar dados do Usuario
        Common.mCurrentUser = AppPref.getInstance().getUser();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        verifConecxaoSaldoWallet();


    }

    private void initViews() {
        raiz = findViewById(android.R.id.content);

        cardView_hint = findViewById(R.id.cardView_hint);
        imageView = findViewById(R.id.img);



        txtNomeCompleto = findViewById(R.id.txtNomeCompleto);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtTelefoneAlternativo = findViewById(R.id.txtTelefoneAlternativo);
        txtEmail = findViewById(R.id.txtEmail);
        txtContaNumber = findViewById(R.id.txtContaNumber);
        txtSaldo = findViewById(R.id.txtSaldo);


        walletContaLayout =  findViewById(R.id.walletContaLayout);
        editTextBI = findViewById(R.id.editTextBI);
        editTextDataNasc = findViewById(R.id.editTextDataNasc);
        btnCriarWallet = findViewById(R.id.btnCriarWallet);

        String minDate = "1914-09-01";
        String maxDate = "2000-12-31";

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date daymin = f.parse(minDate);
            Date daymax = f.parse(maxDate);
            minDateMilliseconds = daymin.getTime();
            maxDateMilliseconds = daymax.getTime();


        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Date dataMIN = new Date();
//        Date dataMAX = new Date();
//        minDateMilliseconds = dataMIN.getDate() - 36500;
//        maxDateMilliseconds = dataMAX.getDate() - 19;

        editTextDataNasc.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int ano = cal.get(Calendar.YEAR);
            int mes = cal.get(Calendar.MONTH);
            int dia = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    WalletActivity.this,
                    R.style.DialogTheme,
                    mDateSetListener,
                    ano, mes, dia);

            dialog.getDatePicker().setMinDate(minDateMilliseconds);
            dialog.getDatePicker().setMaxDate(maxDateMilliseconds);
            dialog.show();
        });

        mDateSetListener = (datePicker, ano, mes, dia) -> {
            mes = mes + 1;



            date = ano + "-" + mes + "-" + dia;
            editTextDataNasc.setText(date);
        };
    }

    private void mostrarTelaCriarWallet(){

        if (walletContaLayout.getVisibility() == View.GONE){
            walletContaLayout.setVisibility(View.VISIBLE);

            cardView_hint.setVisibility(View.GONE);

        }

        btnCriarWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarCampos()){
                    verifConecxaoCriarWallet();
                }
            }
        });
    }


    private boolean verificarCampos() {

        bi = editTextBI.getText().toString().trim();
        dataNasc = editTextDataNasc.getText().toString().trim();


        if (bi.isEmpty()){
            editTextBI.requestFocus();
            editTextBI.setError("Preencha o campo");


            return false;
        }

        if (!bi.matches("(\\d){9}[a-zA-Z][a-zA-Z](\\d){3}")){
            editTextBI.requestFocus();
            editTextBI.setError("Verifica o campo.");

            return false;
        }

        if (dataNasc.isEmpty()){
            editTextDataNasc.requestFocus();
            editTextDataNasc.setError("Preencha o campo");

            return false;
        }


        return true;

    }

    private void verifConecxaoSaldoWallet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                mostrarMensagem(WalletActivity.this,R.string.txtMsg);
            } else {
                saldoContaWalletApi();
            }
        }

    }

    private void saldoContaWalletApi() {

        progressDialog.setMessage("Consultar a conta...");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Wallet>> call = apiInterface.getSaldoWallet();
        call.enqueue(new Callback<List<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {

                //response.body()==null
                if (response.isSuccessful()) {

                    if (response.body()!=null){

                        wallet = response.body().get(0);

                        carregarMeuPerfilOffline(Common.mCurrentUser);

                        progressDialog.dismiss();
                    }




                } else {
                    progressDialog.dismiss();
                    mostrarTelaCriarWallet();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>>call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(WalletActivity.this,TAG)){
                    mostrarMensagem(WalletActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(WalletActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(WalletActivity.this,R.string.txtProblemaMsg);
                }
                Log.i(TAG,"onFailure" + t.getMessage());

                try {
                    Snackbar
                            .make(raiz, t.getMessage(), 4000)
                            .setActionTextColor(Color.MAGENTA)
                            .show();
                } catch (Exception e) {
                    Log.d(TAG, String.valueOf(e.getMessage()));
                }
            }
        });
    }

    private void verifConecxaoCriarWallet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                mostrarMensagem(WalletActivity.this,R.string.txtMsg);
            } else {
                criarContaWalletApi();
            }
        }

    }

    private void criarContaWalletApi() {

        progressDialog.setMessage("Criar conta...");
        progressDialog.show();

        WalletRequest walletRequest = new WalletRequest();
        walletRequest.numeroBi = bi;
        walletRequest.dataNasc = dataNasc;

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.criarContaWallet(walletRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                //response.body()==null
                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    walletContaLayout.setVisibility(View.GONE);
                    verifConecxaoSaldoWallet();


                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(WalletActivity.this,TAG)){
                    mostrarMensagem(WalletActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(WalletActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(WalletActivity.this,R.string.txtProblemaMsg);
                }
                Log.i(TAG,"onFailure" + t.getMessage());

                try {
                    Snackbar
                            .make(raiz, t.getMessage(), 4000)
                            .setActionTextColor(Color.MAGENTA)
                            .show();
                } catch (Exception e) {
                    Log.d(TAG, String.valueOf(e.getMessage()));
                }
            }
        });
    }


    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {
        try {

            String contaID = UUID.randomUUID().toString();

            Picasso.with(this).load(usuarioPerfil.imagem).placeholder(R.drawable.ic_camera).into(imageView);

            txtNomeCompleto.setText(usuarioPerfil.nomeCompleto);

            txtTelefone.setText(usuarioPerfil.contactoMovel);
            txtTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);
            txtEmail.setText(usuarioPerfil.email);


            txtContaNumber.setText(wallet.number);
            txtSaldo.setText(wallet.amount+" Kzs");

            cardView_hint.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
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
