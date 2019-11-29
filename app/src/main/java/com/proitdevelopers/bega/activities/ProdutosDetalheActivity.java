package com.proitdevelopers.bega.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.ProdutosAdapter;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.Produtos;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProdutosDetalheActivity extends AppCompatActivity implements ProdutosAdapter.ProductsAdapterListener {

    String nomeEstabelecimento;
    int produtoId, position;


    ImageView productImg;
    TextView txtName;
    ImageView imgShare,imgFavorite;
    TextView txtPrice,txtDescricao;
    ImageView ic_remove,ic_add;
    TextView product_count;
    Button btn_addCart,btn_comprar;


    View raiz;

    private Realm realm;
    private Produtos produtos;
    private ProdutosAdapter.ProductsAdapterListener listener;
    private CartItemProdutos cartItem;
    private RealmResults<CartItemProdutos> cartItems;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent()!=null){
//            produto = (Produtos) getIntent().getSerializableExtra("produto");
            nomeEstabelecimento = getIntent().getStringExtra("nomeEstabelecimento");
            produtoId = getIntent().getIntExtra("produtoId",0);
            position = getIntent().getIntExtra("position",0);
        }
        setContentView(R.layout.activity_produtos_detalhe);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(nomeEstabelecimento);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        listener = this;

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        produtos = realm.where(Produtos.class).equalTo("idProduto", produtoId).findFirst();
        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();

        checkIfCartAsItems();

        Picasso.with(this).load(produtos.getImagemProduto()).placeholder(R.drawable.hamburger_placeholder).into(productImg);

        txtName.setText(produtos.getDescricaoProdutoC());
        txtDescricao.setText(produtos.getDescricaoProduto());

        String preco = String.valueOf(produtos.getPrecoUnid());
        txtPrice.setText(getString(R.string.price_with_currency, Float.parseFloat(preco)));

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProdutosDetalheActivity.this, "Partilhando link", Toast.LENGTH_SHORT).show();
            }
        });

        int favoriteblack = R.drawable.ic_favorite_black_24dp;
        int favoriteorange = R.drawable.ic_favorite_orange_24dp;
        imgFavorite.setImageResource(favoriteblack);
        imgFavorite.setTag(favoriteblack);
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView imageView = (ImageView) view;
                assert(R.id.imgFavorite == imageView.getId());

                // See here
                Integer integer = (Integer) imageView.getTag();
                integer = integer == null ? 0 : integer;

                switch(integer) {
                    case R.drawable.ic_favorite_black_24dp:
                        imageView.setImageResource(favoriteorange);
                        imageView.setTag(favoriteorange);
                        AppDatabase.addItemToFavourite(produtos);
                        Snackbar.make(view, produtos.getDescricaoProdutoC()+" adicionado aos Favoritos!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    case R.drawable.ic_favorite_orange_24dp:
                    default:
                        imageView.setImageResource(favoriteblack);
                        imageView.setTag(favoriteblack);

                        AppDatabase.removeFavouriteItem(produtos);
                        Snackbar.make(view, produtos.getDescricaoProdutoC()+" removido dos Favoritos!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                }

//                imgFavorite.setImageResource(favorite);


            }
        });


        ic_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onProductAddedCart(position, produtos);

            }
        });


        ic_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProductRemovedFromCart(position, produtos);
            }
        });

        btn_addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, produtos.getDescricaoProdutoC()+" adicionado ao Carrinho!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                listener.onProductAddedCart(position, produtos);
            }
        });

        btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProdutosDetalheActivity.this, ShoppingCartActivity.class));
            }
        });





    }

    private void initViews() {
        raiz = findViewById(android.R.id.content);
        productImg = findViewById(R.id.productImg);
        txtName = findViewById(R.id.txtName);
        imgShare = findViewById(R.id.imgShare);
        imgFavorite = findViewById(R.id.imgFavorite);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescricao = findViewById(R.id.txtDescricao);
        ic_remove = findViewById(R.id.ic_remove);
        ic_add = findViewById(R.id.ic_add);
        product_count = findViewById(R.id.product_count);
        btn_addCart = findViewById(R.id.btn_addCart);
        btn_comprar = findViewById(R.id.btn_comprar);

    }

    private void checkIfCartAsItems(){
        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();
        if (cartItem != null) {
            product_count.setText(String.valueOf(cartItem.quantity));
            ic_add.setVisibility(View.VISIBLE);
            ic_remove.setVisibility(View.VISIBLE);
            product_count.setVisibility(View.VISIBLE);
            btn_addCart.setEnabled(false);

        } else {
            product_count.setText(String.valueOf(0));
            ic_add.setVisibility(View.VISIBLE);
            ic_remove.setVisibility(View.GONE);
            product_count.setVisibility(View.GONE);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_navigation_bottom; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_produtos, menu);

//        MenuItem item = menu.findItem(R.id.menu_cart);
//        MenuItemCompat.setActionView(item, R.layout.cart_items_notification);
//        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
//
//        TextView txtCartItems = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
//        txtCartItems.setText("12");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.menu_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
        }


        return super.onOptionsItemSelected(item);


    }



    @Override
    protected void onResume() {
        super.onResume();
        checkIfCartAsItems();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();



        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(product);
        cartItem = cartItems.where().equalTo("produtos.idProduto", product.getIdProduto()).findFirst();
        if (cartItem != null) {
            product_count.setText(String.valueOf(cartItem.quantity));
            ic_remove.setVisibility(View.VISIBLE);
            product_count.setVisibility(View.VISIBLE);
            btn_addCart.setEnabled(false);

        }



    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        cartItem = cartItems.where().equalTo("produtos.idProduto", product.getIdProduto()).findFirst();

        if (cartItem != null) {
            product_count.setText(String.valueOf(cartItem.quantity));

        }else {
            product_count.setText(String.valueOf(0));
            ic_remove.setVisibility(View.GONE);
            product_count.setVisibility(View.GONE);
            btn_addCart.setEnabled(true);

        }
    }
}
