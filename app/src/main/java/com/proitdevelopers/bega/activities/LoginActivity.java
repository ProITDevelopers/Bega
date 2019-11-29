package com.proitdevelopers.bega.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.model.LoginRequest;
import com.proitdevelopers.bega.model.ReporSenha;
import com.proitdevelopers.bega.model.UsuarioAuth;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.bearerApi;

import static com.proitdevelopers.bega.helper.Common.msgAEnviarTelefone;
import static com.proitdevelopers.bega.helper.Common.msgEnviandoCodigo;
import static com.proitdevelopers.bega.helper.Common.msgErro;
import static com.proitdevelopers.bega.helper.Common.msgErroEspaco;
import static com.proitdevelopers.bega.helper.Common.msgErroLetras;
import static com.proitdevelopers.bega.helper.Common.msgErroSEmailTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroSTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefone;
import static com.proitdevelopers.bega.helper.Common.msgReenviarNumTelef;
import static com.proitdevelopers.bega.helper.Common.msgSupporte;
import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.esconderTeclado;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;
import static com.proitdevelopers.bega.helper.MetodosUsados.removeAcentos;
import static com.proitdevelopers.bega.helper.MetodosUsados.validarEmail;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private UsuarioPerfil usuarioPerfil = new UsuarioPerfil();

    private LoginRequest request = new LoginRequest();

    LinearLayout hint_nome_user,hint_email,hint_telefone,hint_email_telefone;
    AppCompatEditText editNomeUtilizador,editTelefone,editEmailTelefone;
    BottomNavigationView bottomNavigationView;

    private String TAG = "LoginActivity";
    AppCompatEditText editEmail;
    ShowHidePasswordEditText editPass;
    FloatingActionButton btnLogin;
    Button btnRegistro,btnForgotPass;
    ProgressDialog progressDialog;
    View raiz;

    //Esqueceu a senha? ----Componentes interface da caixa de dialogo Enviar Numero Telefone
    private Dialog dialogOpcaoSenhaEnviarTelefone;
    private AppCompatEditText dialog_editTelefone_telefone;
    private String telefoneRedif_senha;
//    private Pinview pinCodigoConfirmacaoTelef;

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogSenhaEnviarTelefoneCodReset;
    private TextView tv_telefone;
    private String telefoneReceberDeNovo;
    private String codigoConfTelef,novaSenha;
    private ShowHidePasswordEditText pinCodigoConfirmacaoTelef,editNovaSenha;

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogConfirmTelefoneSuccesso;

    String nomeUtilizador,email,telefone,palavraPass,emailTelefone;




    Menu menu;
    MenuItem menuItem1,menuItem2,menuItem3,menuItem4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(AppPref.getInstance().getAuthToken())) {
            launchHomeScreen();
        }
//        Common.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_login);


        initViews();

    }


    private void initViews() {

        hint_nome_user = findViewById(R.id.hint_nome_user);
        hint_email = findViewById(R.id.hint_email);
        hint_telefone = findViewById(R.id.hint_telefone);
        hint_email_telefone = findViewById(R.id.hint_email_telefone);

        editNomeUtilizador = findViewById(R.id.editNomeUtilizador);
        editTelefone = findViewById(R.id.editTelefone);
        editEmailTelefone = findViewById(R.id.editEmailTelefone);

        raiz = findViewById(android.R.id.content);
        editEmail = findViewById(R.id.editEmail);
        editPass = findViewById(R.id.editPass);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnForgotPass = findViewById(R.id.btnForgotPass);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifConecxao();
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);

            }
        });



        dialogOpcaoSenhaEnviarTelefone = new Dialog(this);
        dialogOpcaoSenhaEnviarTelefone.setContentView(R.layout.layout_recuperar_senha_telefone);
        Button dialog_btn_cancelar_enviar_telefone = dialogOpcaoSenhaEnviarTelefone.findViewById(R.id.dialog_btn_cancelar_enviar_telefone);
        dialog_editTelefone_telefone = dialogOpcaoSenhaEnviarTelefone.findViewById(R.id.dialog_editTelefone_telefone);
        Button dialog_btn_telefone_redif_enviar_telefone = dialogOpcaoSenhaEnviarTelefone.findViewById(R.id.dialog_btn_telefone_redif_enviar_telefone);
        dialogOpcaoSenhaEnviarTelefone.setCancelable(false);
        dialog_btn_cancelar_enviar_telefone.setOnClickListener(this);
        dialog_btn_telefone_redif_enviar_telefone.setOnClickListener(this);

        //-------------------------------------------------------------
        //Dialog enviar codigo de confirmacao telefone
        dialogSenhaEnviarTelefoneCodReset = new Dialog(this);
        dialogSenhaEnviarTelefoneCodReset.setContentView(R.layout.layout_enviar_codigo_telefone);
        dialogSenhaEnviarTelefoneCodReset.setCancelable(false);
        tv_telefone = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.tv_telefone);
        pinCodigoConfirmacaoTelef = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.pinCodigoConfirmacaoTelef);
        editNovaSenha = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.editNovaSenha);
        TextView receberDeNovoTelefone = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.receberDeNovoTelefone);
        Button btn_enviar_cod_resetTelef = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.btn_enviar_cod_resetTelef);
        LinearLayout linearBtnFecharTelef = dialogSenhaEnviarTelefoneCodReset.findViewById(R.id.linearBtnFecharTelef);
        receberDeNovoTelefone.setOnClickListener(this);
        btn_enviar_cod_resetTelef.setOnClickListener(this);
        linearBtnFecharTelef.setOnClickListener(this);
        //-------------------------------------------------------------

        dialogConfirmTelefoneSuccesso = new Dialog(this);
        dialogConfirmTelefoneSuccesso.setContentView(R.layout.layout_confirmacao_sucesso);
        Button dialog_btn_telefone_sucesso = dialogConfirmTelefoneSuccesso.findViewById(R.id.dialog_btn_telefone_sucesso);
        dialog_btn_telefone_sucesso.setOnClickListener(this);


        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOpcaoSenhaEnviarTelefone.show();

            }
        });

        /*USER_NAME = menu.getItem(0);
        * USER_EMAIL = menu.getItem(1);
        * USER_PHONE = menu.getItem(2);
        * USER_EMAIL_PHONE = menu.getItem(3);
        * */
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        hint_nome_user.setVisibility(View.GONE);
        hint_email.setVisibility(View.GONE);
        hint_telefone.setVisibility(View.GONE);
        hint_email_telefone.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.ic_user_name:

                        hint_nome_user.setVisibility(View.VISIBLE);
                        hint_email.setVisibility(View.GONE);
                        hint_telefone.setVisibility(View.GONE);
                        hint_email_telefone.setVisibility(View.GONE);

                        menuItem.setChecked(true);

                        break;
                    case R.id.ic_user_email:
                        hint_email.setVisibility(View.VISIBLE);
                        hint_nome_user.setVisibility(View.GONE);
                        hint_telefone.setVisibility(View.GONE);
                        hint_email_telefone.setVisibility(View.GONE);

                        menuItem.setChecked(true);
                        break;
                    case R.id.ic_user_telefone:
                        hint_telefone.setVisibility(View.VISIBLE);
                        hint_nome_user.setVisibility(View.GONE);
                        hint_email.setVisibility(View.GONE);
                        hint_email_telefone.setVisibility(View.GONE);

                        menuItem.setChecked(true);
                        break;

                    case R.id.ic_email_telefone:
                        hint_email_telefone.setVisibility(View.VISIBLE);
                        hint_telefone.setVisibility(View.GONE);
                        hint_nome_user.setVisibility(View.GONE);
                        hint_email.setVisibility(View.GONE);

                        menuItem.setChecked(true);
                        break;

                }


                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dialog_btn_telefone_redif_enviar_telefone:
                esconderTeclado(LoginActivity.this);
                enviarTelefonelRedif();
                break;


            case R.id.dialog_btn_cancelar_enviar_telefone:
                esconderTeclado(LoginActivity.this);
                cancelarEnvioTelefone();
                break;



            case R.id.receberDeNovoTelefone:
                if (!TextUtils.isEmpty(telefoneReceberDeNovo)) {
                    enviarTelefoneRedifDeNovo();
                } else {
                    esconderTeclado(LoginActivity.this);
                    mostrarMensagem(LoginActivity.this, R.string.txtTentarmaistarde);
                }
                break;

            case R.id.btn_enviar_cod_resetTelef:
                if (verificarCampoTelef()){
                    esconderTeclado(LoginActivity.this);
                    enviarCodRedifinicaoTelef();
                }
                break;

            case R.id.linearBtnFecharTelef:
                esconderTeclado(LoginActivity.this);
                dialogSenhaEnviarTelefoneCodReset.cancel();
                limparPinView(pinCodigoConfirmacaoTelef,editNovaSenha);
                break;

            case R.id.dialog_btn_telefone_sucesso:
                esconderTeclado(LoginActivity.this);
                dialogConfirmTelefoneSuccesso.cancel();
                break;

        }
    }

    private void limparPinView(ShowHidePasswordEditText pinCodigoConfirmacaoTelef, ShowHidePasswordEditText editNovaSenha) {
        pinCodigoConfirmacaoTelef.setText(null);
        editNovaSenha.setText(null);
    }

    private boolean verificarCampoTelef() {
        codigoConfTelef = pinCodigoConfirmacaoTelef.getText().toString();
        novaSenha = editNovaSenha.getText().toString();

        if (codigoConfTelef.isEmpty()) {
            mostrarMensagem(LoginActivity.this, R.string.txtMsgCondConf);
            pinCodigoConfirmacaoTelef.requestFocus();
            pinCodigoConfirmacaoTelef.setError(msgErro);
            return false;
        }
        if (codigoConfTelef.length() <2) {
            mostrarMensagem(LoginActivity.this, R.string.txtMsgCondConfCod);
            return false;
        }

        if (novaSenha.isEmpty()) {
            editNovaSenha.requestFocus();
            editNovaSenha.setError("Preencha o campo.");
            return false;
        }


        return true;

    }

    private void enviarCodRedifinicaoTelef() {
        Log.i(TAG, "codConfi" + codigoConfTelef);
        progressDialog.setMessage(msgEnviandoCodigo);
        progressDialog.show();
        String telefone = telefoneReceberDeNovo.trim();

        ReporSenha reporSenha = new ReporSenha();
        reporSenha.codigoRecuperacao = codigoConfTelef;
        reporSenha.novaPassword = novaSenha;

//        ApiInterface apiInterface = ApiClient.apiClient().create(ApiInterface.class);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<String> enviarCod = apiInterface.enviarConfirCodigo(Integer.parseInt(telefone), codigoConfTelef,novaSenha);
        Call<String> enviarCod = apiInterface.enviarConfirCodigo(telefone,reporSenha);
        enviarCod.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (!response.isSuccessful()) {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    mostrarMensagem(LoginActivity.this, "errorResponce.getError()");
                    progressDialog.cancel();
                } else {
//                    if (response.body() != null) {
//                        token = response.body().getToken();
//                        dialogSenhaEnviarTelefoneCodReset.cancel();
//                        dialogSenhaEnviarEmailSenhaNova.show();
//                        limparPinView(pinCodigoConfirmacaoTelef,editNovaSenha);
//                    }

                    String message = response.body();

                    limparPinView(pinCodigoConfirmacaoTelef,editNovaSenha);
                    dialogSenhaEnviarTelefoneCodReset.cancel();
                    progressDialog.dismiss();
                    dialogConfirmTelefoneSuccesso.show();
//                    mostrarDialogoOK(message);
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(LoginActivity.this,TAG)) {
                    mostrarMensagem(LoginActivity.this, R.string.txtMsg);
                } else if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(LoginActivity.this, R.string.txtTimeout);
                } else {
                    mostrarMensagem(LoginActivity.this, R.string.txtProblemaMsg);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });



    }


    private void enviarTelefoneRedifDeNovo() {

        dialog_editTelefone_telefone.setError(null);
        String telefone = telefoneReceberDeNovo.trim();
//        ApiInterface apiInterface = ApiClient.apiClient().create(ApiInterface.class);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> enviarYelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
        progressDialog.setMessage(msgReenviarNumTelef);
        progressDialog.show();
        enviarYelefoneReset.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    mostrarMensagem(LoginActivity.this, R.string.txtCodigoenviado);
                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    Toast.makeText(LoginActivity.this, errorResponce.getError(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(LoginActivity.this,TAG)) {
                    mostrarMensagem(LoginActivity.this, R.string.txtMsg);
                } else if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(LoginActivity.this, R.string.txtTimeout);
                } else {
                    mostrarMensagem(LoginActivity.this, R.string.txtProblemaMsg);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });

    }

    private void cancelarEnvioTelefone() {
        dialog_editTelefone_telefone.setText(null);
        dialog_editTelefone_telefone.setError(null);
        dialogOpcaoSenhaEnviarTelefone.dismiss();
        dialogOpcaoSenhaEnviarTelefone.cancel();
    }

    private void enviarTelefonelRedif() {
        if (verificarTelefone()) {
            mandarTelefoneResetSenha(telefoneRedif_senha);
        }
    }

    private boolean verificarTelefone() {

        if (dialog_editTelefone_telefone.getText() != null) {
            telefoneRedif_senha = dialog_editTelefone_telefone.getText().toString().trim();
            if (!telefoneRedif_senha.matches("9[1-9][1-9]\\d{6}")) {
                dialog_editTelefone_telefone.setError(msgErroTelefone);
                return false;
            }else {
                return true;
            }
        }else {
            dialog_editTelefone_telefone.setError(msgErroSTelefone);
            return false;
        }
    }

    private void mandarTelefoneResetSenha(String telefone) {
        dialog_editTelefone_telefone.setError(null);
        telefoneReceberDeNovo = telefone;
//        ApiInterface apiInterface = ApiClient.apiClient().create(ApiInterface.class);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> enviarTelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
        progressDialog.setMessage(msgAEnviarTelefone);
        progressDialog.show();
        enviarTelefoneReset.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
//                    String idTelef = response.body().getId();
//                    String idTelef = response.body();
                    progressDialog.cancel();
                    dialog_editTelefone_telefone.setText(null);
                    dialogOpcaoSenhaEnviarTelefone.cancel();
                    tv_telefone.setText(msgSupporte.concat(telefone));
                    dialogSenhaEnviarTelefoneCodReset.show();
                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    progressDialog.dismiss();
                    dialog_editTelefone_telefone.setError(errorResponce.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(LoginActivity.this,TAG)){
                    mostrarMensagem(LoginActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(LoginActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(LoginActivity.this,R.string.txtProblemaMsg);
                }
                Log.i(TAG,"onFailure" + t.getMessage());
            }
        });
    }




    private void autenticacaoLogin(LoginRequest request) {

        progressDialog.setMessage("Verificando...");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UsuarioAuth> call = apiInterface.autenticarCliente(request);
        call.enqueue(new Callback<UsuarioAuth>() {
            @Override
            public void onResponse(@NonNull Call<UsuarioAuth> call, @NonNull Response<UsuarioAuth> response) {

                //response.body()==null
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioAuth userToken = response.body();



                    progressDialog.setMessage("Validando os dados...");
                    AppPref.getInstance().saveAuthToken(userToken.tokenuser);
                    AppPref.getInstance().saveTokenTime(userToken.expiracao);
//                    AppPref.getInstance().saveUser(Common.mCurrentUser);

                    carregarMeuPerfil(userToken.tokenuser);

//                    progressDialog.dismiss();
//                    launchHomeScreen();


                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(@NonNull Call<UsuarioAuth> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(LoginActivity.this,TAG)){
                    mostrarMensagem(LoginActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(LoginActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(LoginActivity.this,R.string.txtProblemaMsg);
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


    private void carregarMeuPerfil(String token) {
        progressDialog.setMessage("Carregando dados...");
        String bearerToken = bearerApi.concat(token);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>>  usuarioCall = apiInterface.getPerfilLogin(bearerToken);

        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {


                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);
//
                        Common.mCurrentUser = usuarioPerfil;
//
//

                        AppPref.getInstance().saveUser(Common.mCurrentUser);
                        progressDialog.dismiss();
                        launchHomeScreen();
                    }

                } else {
                    progressDialog.dismiss();
                    AppPref.getInstance().clearAppPrefs();
                }




            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {
                AppPref.getInstance().clearAppPrefs();
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(LoginActivity.this,TAG)){
                    mostrarMensagem(LoginActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(LoginActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(LoginActivity.this,R.string.txtProblemaMsg);
                }

            }
        });
    }





    private boolean verificarCamposUserName() {

        nomeUtilizador = editNomeUtilizador.getText().toString().trim();
        palavraPass = editPass.getText().toString().trim();

        try {
            nomeUtilizador = removeAcentos(nomeUtilizador);
        }catch (Exception e){
            e.printStackTrace();
        }



        if (nomeUtilizador.isEmpty()){
            editNomeUtilizador.setError(msgErro);
            return false;
        }

        if (nomeUtilizador.contains(" ")){
            editNomeUtilizador.setError(msgErroEspaco);
            return false;
        }

        if (!nomeUtilizador.matches("^[a-zA-Z\\s]+$")){
            editNomeUtilizador.setError(msgErroLetras);
            return false;
        }

        if (palavraPass.isEmpty()) {
            editPass.requestFocus();
            editPass.setError("Preencha o campo.");
            return false;
        }

        editNomeUtilizador.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private boolean verificarCampos() {

        email = editEmail.getText().toString().trim();
        palavraPass = editPass.getText().toString().trim();

        if (email.isEmpty()) {
            editEmail.requestFocus();
            editEmail.setError("Preencha o campo.");
            return false;
        }

        if (!validarEmail(email)) {
            editEmail.requestFocus();
            editEmail.setError("Preencha o campo com um email.");
            return false;
        }

        if (palavraPass.isEmpty()) {
            editPass.requestFocus();
            editPass.setError("Preencha o campo.");
            return false;
        }

        editEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private boolean verificarCamposTelefone() {

        telefone = editTelefone.getText().toString().trim();
        palavraPass = editPass.getText().toString().trim();

        if (telefone.isEmpty()){
            editTelefone.setError(msgErroSTelefone);
            return false;
        }

        if (!telefone.matches("9[1-9][1-9]\\d{6}")){
            editTelefone.setError(msgErroTelefone);
            return false;
        }

        if (palavraPass.isEmpty()) {
            editPass.requestFocus();
            editPass.setError("Preencha o campo.");
            return false;
        }

        editTelefone.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private boolean verificarCamposEmailTelefone() {

        emailTelefone = editEmailTelefone.getText().toString().trim();
        palavraPass = editPass.getText().toString().trim();



        if (emailTelefone.isEmpty()){
            editEmailTelefone.setError(msgErro);
            return false;
        }

        if (validarEmail(emailTelefone)) {
            emailTelefone = emailTelefone.toLowerCase();
            request.email = emailTelefone;
        }else {
            if (emailTelefone.matches("9[1-9][1-9]\\d{6}")){
                request.telefone = emailTelefone;
                return true;
            } else {
                editEmailTelefone.requestFocus();
                editEmailTelefone.setError(msgErroSEmailTelefone);
                return false;
            }
        }

        if (palavraPass.isEmpty()) {
            editPass.requestFocus();
            editPass.setError("Preencha o campo.");
            return false;
        }




        editEmailTelefone.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editPass.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void verifConecxao() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                mostrarMensagem(LoginActivity.this,R.string.txtMsg);
            } else {
//                autenticacaoLoginApi();
                checkMenuState();
            }
        }

    }

    private void checkMenuState() {
        menu = bottomNavigationView.getMenu();
        menuItem1 = menu.getItem(0);
        menuItem2 = menu.getItem(1);
        menuItem3 = menu.getItem(2);
        menuItem4 = menu.getItem(3);


        if (menu.getItem(0).isChecked())

        if (menuItem1.isChecked()){

            verificarCamposUtilizador();

//            autenticacaoLoginApiUsername();
        }

        if (menuItem2.isChecked()){
//            autenticacaoLoginApi();

            verificarCamposEmail();
        }

        if (menuItem3.isChecked()){
//            autenticacaoLoginApiTelefone();
            verificarCamposTelef();
        }

        if (menuItem4.isChecked()){
//            autenticacaoLoginApiTelefone();
            verificarCamposEmailTelef();
        }
    }




    private void verificarCamposUtilizador() {
        if (verificarCamposUserName()) {

            request.userName = nomeUtilizador;
            request.password = palavraPass;
            request.rememberMe = true;
            autenticacaoLogin(request);

        }
    }

    private void verificarCamposEmail() {
        if (verificarCampos()) {
            request.email = email;
            request.password = palavraPass;
            request.rememberMe = true;
            autenticacaoLogin(request);
        }
    }

    private void verificarCamposTelef() {
        if (verificarCamposTelefone()) {

            request.telefone = telefone;
            request.password = palavraPass;
            request.rememberMe = true;
            autenticacaoLogin(request);
        }
    }

    private void verificarCamposEmailTelef() {
        if (verificarCamposEmailTelefone()) {

            request.password = palavraPass;
            request.rememberMe = true;

            autenticacaoLogin(request);
        }
    }








}
