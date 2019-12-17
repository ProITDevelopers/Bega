package com.proitdevelopers.bega.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.msgErro;
import static com.proitdevelopers.bega.helper.Common.msgErroLetras;
import static com.proitdevelopers.bega.helper.Common.msgErroSTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefoneIguais;
import static com.proitdevelopers.bega.helper.Common.msgQuasePronto;

import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;
import static com.proitdevelopers.bega.helper.MetodosUsados.removeAcentos;

public class PerfilActivity extends AppCompatActivity {

    private String TAG = "PerfilActivity";
    private UsuarioPerfil usuarioPerfil;

    private CircleImageView imageView;
    private TextView txtNomeCompleto,txtNomeUtilizador,txtTelefone,txtTelefoneAlternativo;
    private TextView txtEmail,txtSexo,txtProvincia,txtEndereco;


    private View raiz;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando...");
        progressDialog.setCancelable(false);


        //carregar dados do Usuario
        Common.mCurrentUser = AppPref.getInstance().getUser();
        carregarMeuPerfilOffline(Common.mCurrentUser);


    }

    private void initViews() {

        imageView = findViewById(R.id.img);

        txtNomeCompleto = findViewById(R.id.txtNomeCompleto);
        txtNomeUtilizador = findViewById(R.id.txtNomeUtilizador);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtTelefoneAlternativo = findViewById(R.id.txtTelefoneAlternativo);

        txtEmail = findViewById(R.id.txtEmail);
        txtSexo = findViewById(R.id.txtSexo);
        txtProvincia = findViewById(R.id.txtProvincia);

        txtEndereco = findViewById(R.id.txtEndereco);

        raiz = findViewById(android.R.id.content);


    }

    private void verificaoPerfil() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){

                carregarMeuPerfilOffline(Common.mCurrentUser);
            } else {
                carregarMeuPerfil();
            }
        }
    }

    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {
        try {


            Picasso.with(this).load(usuarioPerfil.imagem).placeholder(R.drawable.ic_camera).into(imageView);


            txtNomeCompleto.setText(usuarioPerfil.nomeCompleto);
            txtNomeUtilizador.setText(usuarioPerfil.userName);

            txtTelefone.setText(usuarioPerfil.contactoMovel);
            txtTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);
            txtEmail.setText(usuarioPerfil.email);

            txtSexo.setText(usuarioPerfil.sexo);
            txtProvincia.setText(usuarioPerfil.provincia);




            txtEndereco.setText("Município de "+
                    usuarioPerfil.municipio +", "+
                    "Bairro "+usuarioPerfil.bairro+", "+
                    "Rua "+usuarioPerfil.rua+", "+
                    "Casa "+usuarioPerfil.nCasa);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        verificaoPerfil();
    }

    private void carregarMeuPerfil() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>> usuarioCall = apiInterface.getMeuPerfil();
        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);

                        Common.mCurrentUser = usuarioPerfil;

                        AppPref.getInstance().saveUser(Common.mCurrentUser);

                        carregarMeuPerfilOffline(Common.mCurrentUser);



                    }


                } else {

                    mensagemTokenExpirado();

                }
                progressDialog.dismiss();






            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {

                progressDialog.dismiss();
                if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(PerfilActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(PerfilActivity.this,R.string.txtProblemaMsg);
                }


            }
        });
    }



    private void mensagemTokenExpirado() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("A sessão expirou!");
        dialog.setMessage("Inicie outra vez a sessão!");

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

        AppDatabase.clearData();
        AppPref.getInstance().clearAppPrefs();
        Intent intent = new Intent(PerfilActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_navigation_bottom; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_perfil, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.menu_perfil_edit) {

            Intent intent = new Intent(this, EditarPerfilActivity.class);
            intent.putExtra("mCurrentUser",Common.mCurrentUser);
            startActivity(intent);
        }

        if (id == R.id.action_refresh) {
            verificaoPerfil();
        }


        return super.onOptionsItemSelected(item);


    }


}
