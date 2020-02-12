package com.proitdevelopers.bega.Testes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.activities.LoginActivity;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFacebookActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView txtNomeCompleto,txtEmail;
    private Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_facebook);

        if (getIntent()!=null){
            Common.mCurrentUser = (UsuarioPerfil) getIntent().getSerializableExtra("mCurrentUser");
        }

        imageView = findViewById(R.id.profile_pic);
        txtNomeCompleto = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(PerfilFacebookActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        carregarMeuPerfilOffline(Common.mCurrentUser);
    }

    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {
        try {


            Picasso.with(this).load(usuarioPerfil.imagem).placeholder(R.drawable.ic_camera).into(imageView);


            txtNomeCompleto.setText(usuarioPerfil.primeiroNome+" "+usuarioPerfil.ultimoNome);
            txtEmail.setText(usuarioPerfil.email);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PerfilFacebookActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();


    }
}
