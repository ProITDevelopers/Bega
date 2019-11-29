package com.proitdevelopers.bega.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.fragmentos.CategoriaFragment;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.helper.NotificationHelper;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;


public class MenuActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private String TAG = "MenuActivity";



    private UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
    private CircleImageView image_User_avatar;
    private TextView txtName;
    private TextView txtEmail;

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogTerminarSessao;



    NotificationHelper notificationHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        notificationHelper = new NotificationHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bega");
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View navigationHeaderView = navigationView.getHeaderView(0);


        image_User_avatar = (CircleImageView) navigationHeaderView.findViewById(R.id.image_User_avatar);
        txtName = (TextView) navigationHeaderView.findViewById(R.id.txtUserName);
        txtEmail = (TextView) navigationHeaderView.findViewById(R.id.txtUserEmail);



        dialogTerminarSessao = new Dialog(this);
        dialogTerminarSessao.setContentView(R.layout.layout_terminar_sessao);
        Button dialog_btn_cancelar_sessao = dialogTerminarSessao.findViewById(R.id.dialog_btn_cancelar_sessao);
        Button dialog_btn_terminar_sessao = dialogTerminarSessao.findViewById(R.id.dialog_btn_terminar_sessao);
        dialog_btn_cancelar_sessao.setOnClickListener(this);
        dialog_btn_terminar_sessao.setOnClickListener(this);







        //carregar dados do Usuario
        Common.mCurrentUser = AppPref.getInstance().getUser();
        carregarMeuPerfilOffline(Common.mCurrentUser);



        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new CategoriaFragment());
        transaction.commit();

    }

    private void verificaoPerfil() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                carregarMeuPerfilOffline(Common.mCurrentUser);
                Toast.makeText(this, "Network offline", Toast.LENGTH_SHORT).show();
            } else {
                carregarMeuPerfil();
            }
        }
    }

    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {

        try {

            Picasso.with(this).load(usuarioPerfil.imagem).placeholder(R.drawable.ic_camera).into(image_User_avatar);
            txtName.setText(usuarioPerfil.primeiroNome + " "+ Common.mCurrentUser.ultimoNome);
            txtEmail.setText(usuarioPerfil.email);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void carregarMeuPerfil() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>>  usuarioCall = apiInterface.getMeuPerfil();
        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);
                        Common.mCurrentUser = usuarioPerfil;


                        carregarMeuPerfilOffline(Common.mCurrentUser);

                        AppPref.getInstance().saveUser(Common.mCurrentUser);

                    }

                } else {
                    notificationHelper.createNotification("A sessão expirou!","Inicie outra vez a sessão!");
                    mensagemTokenExpirado();
                }





            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {

                if (!conexaoInternetTrafego(MenuActivity.this,TAG)){
                    mostrarMensagem(MenuActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(MenuActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(MenuActivity.this,R.string.txtProblemaMsg);
                }


            }
        });
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

                logOut();

            }
        });




        dialog.show();
    }




    private void logOut() {
        AppPref.getInstance().clearAppPrefs();
        AppDatabase.clearData();
        Intent intent = new Intent(MenuActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        verificaoPerfil();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu_home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new CategoriaFragment());
            transaction.commit();
        }

        else if (id == R.id.nav_menu_perfil) {

           Intent intent = new Intent(this,PerfilActivity.class);
           startActivity(intent);
        }

        else if (id == R.id.nav_menu_wallet) {

            Intent intent = new Intent(this,WalletActivity.class);
            startActivity(intent);
        }

//        else if (id == R.id.nav_menu_favoritos) {
//
//            Intent intent = new Intent(this,FavoritosActivity.class);
//            startActivity(intent);
//        }

        else if (id == R.id.nav_menu_share) {
            MetodosUsados.shareTheApp(this);

        }


        else if (id == R.id.nav_menu_settings) {
            Toast.makeText(this, "nav_menu_settings", Toast.LENGTH_SHORT).show();
        }

        else if (id == R.id.nav_menu_logout) {
            dialogTerminarSessao.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.menu_cart) {
            Intent intent = new Intent(MenuActivity.this, ShoppingCartActivity.class);
            startActivity(intent);
            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Definições", Toast.LENGTH_SHORT).show();

            return true;
        }



        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        //            invalidateOptionsMenu();
////        MenuItem item =  menu.findItem(R.id.menu_switch_layout);
////        loadIcon(item);
//
//        return super.onPrepareOptionsMenu(menu);
//    }







}