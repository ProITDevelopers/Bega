package com.proitdevelopers.bega.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.adapters.ProdutosAdapter;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.Produtos;
import com.proitdevelopers.bega.utilsClasses.Converter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class ProdutosActivity extends AppCompatActivity implements ProdutosAdapter.ProductsAdapterListener {
    private String TAG = "ProdutosActivity";

    @SerializedName("idEstabelecimento")
    public int idEstabelecimento;

    List<Produtos> produtosList = new ArrayList<>();
    private RealmResults<Produtos> produtosListRealm;


    private RecyclerView recyclerView;
    private ProdutosAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;
    ProgressDialog progressDialog;

    @SerializedName("estabelecimento")
    private String nomeEstabelecimento;

    String imagemCapa;
    ImageView header;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;

    Toolbar toolbar;

    Drawable toolbarBK;
    int cart_count = 0;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Common.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        if (getIntent()!=null){
            idEstabelecimento = getIntent().getIntExtra("idEstabelecimento",0);
            imagemCapa = getIntent().getStringExtra("imagemCapa");
            nomeEstabelecimento = getIntent().getStringExtra("nomeEstabelecimento");
        }
        setContentView(R.layout.activity_produtos);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        errorLayout = (RelativeLayout) findViewById(R.id.erroLayout);
        btnTentarDeNovo = (TextView) findViewById(R.id.btn);
        btnTentarDeNovo.setText("Tentar de Novo");


        toolbarBK = getResources().getDrawable( R.drawable.toolbar_bk );

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(nomeEstabelecimento);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initCollapsingToolbar();
        header = findViewById(R.id.header);
        Picasso.with(this).load(imagemCapa).placeholder(R.drawable.shop_placeholder).into(header);



        recyclerView = (RecyclerView) findViewById(R.id.recyclewProdutos);
        gridLayoutManager = new GridLayoutManager(this, 1);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        verifConecxaoProdutos();

        cartRealmChangeListener = cartItems -> {
//            Timber.d("Cart items changed! " + this.cartItems.size());
            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
//                toggleCartBar(true);
            } else {
//                toggleCartBar(false);
                cart_count = 0;
                invalidateOptionsMenu();
            }

            if (itemAdapter!=null)
                itemAdapter.setCartItems(cartItems);

            invalidateOptionsMenu();
        };

    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                appBarLayout.setExpanded(false);
            } else {
                appBarLayout.setExpanded(true);
            }
        }

//        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    collapsingToolbar.setTitle(nomeEstabelecimento);
                    toolbar.setBackground(toolbarBK);
                    isShow = true;
                } else if (isShow) {
                    toolbar.setBackground(null);
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }



    private void verifConecxaoProdutos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                mostarMsnErro();
            } else {
                carregarProductsList();
//                renderProducts();
            }
        }
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
                verifConecxaoProdutos();
            }
        });
    }

    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {
        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
//        cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
//        cart_count = itemCount;
        cart_count = cartItems.size();



    }

    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(this,product);
        if (cartItems != null) {
            itemAdapter.updateItem(index, cartItems);

        }
    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        if (cartItems != null) {
            itemAdapter.updateItem(index, cartItems);

        }

    }

    private void carregarProductsList() {
        progressDialog.setMessage("Carregando...");
        progressDialog.show();
//        ApiInterface apiInterface = ApiClient.apiClient().create(ApiInterface.class);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Produtos>> rv = apiInterface.getAllProdutosDoEstabelecimento(idEstabelecimento);
        rv.enqueue(new Callback<List<Produtos>>() {
            @Override
            public void onResponse(@NonNull Call<List<Produtos>> call, @NonNull Response<List<Produtos>> response) {

                if (!response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(ProdutosActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                } else {

                    if (response.body()!=null){

                        produtosList = response.body();

                        setProdutosAdapters(produtosList);
                        AppDatabase.saveProducts(produtosList);

                    } else {
                        Toast.makeText(ProdutosActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Produtos>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(ProdutosActivity.this,TAG)){
                    mostrarMensagem(ProdutosActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(ProdutosActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(ProdutosActivity.this,R.string.txtProblemaMsg);
                }
            }
        });
    }

    private void setProdutosAdapters(List<Produtos> produtosList) {

        if (produtosList.size()>0){

            itemAdapter = new ProdutosAdapter(this, produtosList,this, gridLayoutManager);
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(gridLayoutManager);

            itemAdapter.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Produtos produto = produtosList.get(position);
//                    Toast.makeText(ProdutosActivity.this, "Item: "+produto.getDescricaoProdutoC(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProdutosActivity.this,ProdutosDetalheActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("nomeEstabelecimento",nomeEstabelecimento);
                    intent.putExtra("produtoId",produto.getIdProduto());
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            });
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_navigation_bottom; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_produtos, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_cart);
        menuItem.setIcon(Converter.convertLayoutToImage(ProdutosActivity.this,cart_count,R.drawable.ic_shopping_cart_white_24dp));
//        MenuItem menuItem2 = menu.findItem(R.id.notification_action);
//        menuItem2.setIcon(Converter.convertLayoutToImage(MainActivity.this,2,R.drawable.ic_notifications_white_24dp));
//

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.menu_cart) {

            Intent intent = new Intent(ProdutosActivity.this,ShoppingCartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

//        if (id == R.id.action_favoritos) {
//            startActivity(new Intent(this, FavoritosActivity.class));
//        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cartItems != null) {
//             cartItems.removeChangeListener(cartRealmChangeListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }
        progressDialog.cancel();
    }


}
