package com.proitdevelopers.bega.activities;

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
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Utils;
import com.proitdevelopers.bega.adapters.FavoritosItemAdapter;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.model.FavoritosItem;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FavoritosActivity extends AppCompatActivity implements FavoritosItemAdapter.FavoritosItemListener{

    RecyclerView recyclerView;


    private Realm realm;
    private FavoritosItemAdapter mAdapter;
    private RealmResults<FavoritosItem> favoritosItems;
    private RealmChangeListener<RealmResults<FavoritosItem>> favoritosItemsRealmChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favoritos");
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        realm = Realm.getDefaultInstance();
        favoritosItems = realm.where(FavoritosItem.class).findAllAsync();

        favoritosItemsRealmChangeListener = favoritosItems -> {
            mAdapter.setData(favoritosItems);
            showQuantity();
        };

        favoritosItems.addChangeListener(favoritosItemsRealmChangeListener);
        init();
    }

    private void init() {
        mAdapter = new FavoritosItemAdapter(this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        showQuantity();
    }

    private void showQuantity() {
        if (favoritosItems != null) {
            int quantity = Utils.getFavoriteQuantity(favoritosItems);
            if (quantity > 0) {
                Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show();
            } else {
                // if the price is zero, dismiss the dialog
//                dismiss();

                Toast.makeText(this, "Sem items nos favoritos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (favoritosItems != null) {
            favoritosItems.removeChangeListener(favoritosItemsRealmChangeListener);
        }

        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void onFavoritoItemRemoved(int index, FavoritosItem favoritosItem) {
        AppDatabase.removeFavouriteItem(favoritosItem);
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
