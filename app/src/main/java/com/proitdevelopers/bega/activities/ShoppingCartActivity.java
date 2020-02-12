package com.proitdevelopers.bega.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.proitdevelopers.bega.utilsClasses.CartInfoBar;
import com.proitdevelopers.bega.utilsClasses.RecyclerSectionItemDecoration;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Utils;
import com.proitdevelopers.bega.adapters.CartProdutosAdapter;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.model.CartItemProdutos;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


import static com.proitdevelopers.bega.helper.Common.msgErroSTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefone;
import static com.proitdevelopers.bega.helper.MetodosUsados.esconderTeclado;

public class ShoppingCartActivity extends AppCompatActivity
        implements View.OnClickListener, CartProdutosAdapter.CartProductsAdapterListener{



    CartInfoBar cartInfoBar;
    RecyclerView recyclerView;
    Button btnCheckout;

    private Realm realm;
    private CartProdutosAdapter mAdapter;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartItemRealmChangeListener;

    private Dialog caixa_dialogo_tipo_pagamento;


    public static final String TAG = "PlacePickerActivity";
    private static final int LOC_REQ_CODE = 1;
    private static final int PLACE_PICKER_REQ_CODE = 2;



    private Place place;
    String latitude,longitude,endereco,telefone,tipoPagamento;

    private Dialog caixa_dialogo_fornecer_numero;
    AppCompatEditText dialog_editTelefone_telefone,dialog_editEndereco;
    Button dialog_btn_meu_telefone;
    Button dialog_btn_cancelar_enviar_telefone;
    Button dialog_btn_enviar_telefone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Carrinho");
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_view);
        btnCheckout = findViewById(R.id.btn_checkout);

        cartInfoBar  = findViewById(R.id.cart_info_bar);


        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        cartItemRealmChangeListener = cartItems -> {
            mAdapter.setData(cartItems);
            setTotalPrice();

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
                toggleCartBar(true);

            } else {
                toggleCartBar(false);
            }
        };

        cartItems.addChangeListener(cartItemRealmChangeListener);
        init();



        caixa_dialogo_tipo_pagamento = new Dialog(this);
        caixa_dialogo_tipo_pagamento.setContentView(R.layout.layout_tipo_pagamento);
        caixa_dialogo_tipo_pagamento.setCancelable(false);

        //Botão em caixa_dialogo_tipo_pagamento
        Button btnWallet = caixa_dialogo_tipo_pagamento.findViewById(R.id.btnWallet);
        Button btnMulticaixa = caixa_dialogo_tipo_pagamento.findViewById(R.id.btnMulticaixa);
//        Button btnCancelar_dialog = caixa_dialogo_tipo_pagamento.findViewById(R.id.btnCancelar_dialog);
        ImageView btnCancelar_dialog = caixa_dialogo_tipo_pagamento.findViewById(R.id.btnCancelar_dialog);

        if (caixa_dialogo_tipo_pagamento.getWindow()!=null)
            caixa_dialogo_tipo_pagamento.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnWallet.setOnClickListener(this);
        btnMulticaixa.setOnClickListener(this);
        btnCancelar_dialog.setOnClickListener(this);


        caixa_dialogo_fornecer_numero = new Dialog(this);
        caixa_dialogo_fornecer_numero.setContentView(R.layout.layout_fornecer_numero_telefone);
        caixa_dialogo_fornecer_numero.setCancelable(false);

        //Botão em caixa_dialogo_telefone
        dialog_editTelefone_telefone = caixa_dialogo_fornecer_numero.findViewById(R.id.dialog_editTelefone_telefone);
        dialog_editEndereco = caixa_dialogo_fornecer_numero.findViewById(R.id.dialog_editEndereco);
        dialog_btn_meu_telefone = caixa_dialogo_fornecer_numero.findViewById(R.id.dialog_btn_meu_telefone);
        dialog_btn_cancelar_enviar_telefone = caixa_dialogo_fornecer_numero.findViewById(R.id.dialog_btn_cancelar_enviar_telefone);
        dialog_btn_enviar_telefone = caixa_dialogo_fornecer_numero.findViewById(R.id.dialog_btn_enviar_telefone);

        dialog_btn_meu_telefone.setOnClickListener(this);
        dialog_btn_cancelar_enviar_telefone.setOnClickListener(this);
        dialog_btn_enviar_telefone.setOnClickListener(this);

        if (caixa_dialogo_fornecer_numero.getWindow()!=null)
            caixa_dialogo_fornecer_numero.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caixa_dialogo_tipo_pagamento.show();


            }
        });
    }

    private void init() {
        mAdapter = new CartProdutosAdapter(this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setTotalPrice();

//        if (cartItems!=null){
//            RecyclerSectionItemDecoration sectionItemDecoration =
//                    new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.header),
//                            true,
//                            getSectionCallback(cartItems));
//            recyclerView.addItemDecoration(sectionItemDecoration);
//        }



    }




    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(RealmResults<CartItemProdutos> cartItems) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {


                return position == 0 ||

                        !cartItems.get(position).produtos.getEstabelecimento().equals(cartItems.get(position - 1).produtos.getEstabelecimento());
            }

            @Override
            public String getSectionHeader(int position) {

                return cartItems.get(position).produtos.getEstabelecimento();
            }
        };
    }

    private void setTotalPrice() {
        if (cartItems != null) {
            float price = Utils.getCartPrice(cartItems);
            if (price > 0) {
//                btnCheckout.setText(getString(R.string.btn_checkout, getString(R.string.price_with_currency, price)));
                btnCheckout.setText(getString(R.string.btn_checkout));
            } else {
                // if the price is zero, dismiss the dialog
//                dismiss();
                btnCheckout.setVisibility(View.INVISIBLE);
                btnCheckout.setText(getString(R.string.btn_checkout));
                Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {
        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
        cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
    }

    private void toggleCartBar(boolean show) {
        if (show)
            cartInfoBar.setVisibility(View.VISIBLE);
        else
            cartInfoBar.setVisibility(View.GONE);
    }
    

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnWallet:
                caixa_dialogo_tipo_pagamento.dismiss();
                tipoPagamento = "Wallet";
                getCurrentPlaceItems();
                break;
            case R.id.btnMulticaixa:
                caixa_dialogo_tipo_pagamento.dismiss();
                tipoPagamento = "Multicaixa";
                getCurrentPlaceItems();
                break;
            case R.id.btnCancelar_dialog:
                caixa_dialogo_tipo_pagamento.dismiss();
                break;

            case R.id.dialog_btn_meu_telefone:
                telefone  = AppPref.getInstance().getUser().contactoMovel;
                dialog_editTelefone_telefone.setText(null);
                dialog_editTelefone_telefone.setError(null);
                dialog_editTelefone_telefone.setText(telefone);
                break;

            case R.id.dialog_btn_cancelar_enviar_telefone:

                esconderTeclado(ShoppingCartActivity.this);
                cancelarEnvioTelefone();

                break;
            case R.id.dialog_btn_enviar_telefone:

                esconderTeclado(ShoppingCartActivity.this);
                abrirPagamentoActivity();
                break;

        }
    }

    private boolean verificarCamposTelefone() {

        telefone = dialog_editTelefone_telefone.getText().toString().trim();
        endereco = dialog_editEndereco.getText().toString().trim();

        if (telefone.isEmpty()){
            dialog_editTelefone_telefone.setError(msgErroSTelefone);
            return false;
        }

        if (!telefone.matches("9[1-9][0-9]\\d{6}")){
            dialog_editTelefone_telefone.setError(msgErroTelefone);
            return false;
        }

        if (endereco.isEmpty()) {
            dialog_editEndereco.requestFocus();
            dialog_editEndereco.setError("Preencha o campo.");
            return false;
        }

        dialog_editTelefone_telefone.onEditorAction(EditorInfo.IME_ACTION_DONE);
        dialog_editEndereco.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private void cancelarEnvioTelefone() {
        dialog_editTelefone_telefone.setText(null);
        dialog_editTelefone_telefone.setError(null);

        dialog_editEndereco.setText(null);
        dialog_editEndereco.setError(null);

        caixa_dialogo_fornecer_numero.dismiss();
    }


    private void abrirPagamentoActivity() {
        if (verificarCamposTelefone()){
            caixa_dialogo_fornecer_numero.dismiss();
            Intent intent = new Intent(this,PagamentoActivity.class);
            intent.putExtra("tipoPagamento",tipoPagamento);
            intent.putExtra("numero",telefone);
            intent.putExtra("endereco",endereco);
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.removeChangeListener(cartItemRealmChangeListener);
        }

        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void onCartItemRemoved(int index, CartItemProdutos cartItem) {
        AppDatabase.removeCartItem(cartItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void getCurrentPlaceItems() {
        if (isLocationAccessPermitted()) {
            showPlacePicker();
        } else {
            requestLocationAccessPermission();
        }
    }


    private void showPlacePicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private boolean isLocationAccessPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOC_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                showPlacePicker();
            } else {
                Toast.makeText(this, "This app needs Location permission to continue!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //    ------------- resultado do place picker----------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                showPlacePicker();
            }
        }else if(requestCode == PLACE_PICKER_REQ_CODE){
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);

                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                endereco = String.format("%s", place.getAddress());

                mostrarConfirmarNumeros(endereco);
            }
        }
    }

    private void mostrarConfirmarNumeros(String endereco) {
        caixa_dialogo_fornecer_numero.show();
        dialog_editEndereco.setText(endereco);
    }
}
