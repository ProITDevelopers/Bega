package com.proitdevelopers.bega.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.proitdevelopers.bega.Testes.PerfilFacebookActivity;
import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.model.FaceBookLoginRequest;
import com.proitdevelopers.bega.model.LoginRequest;
import com.proitdevelopers.bega.model.ReporSenha;
import com.proitdevelopers.bega.model.UsuarioAuth;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
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

    private String TAG = "LoginActivity";

    private UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
    private LoginRequest request = new LoginRequest();
    private FaceBookLoginRequest faceBookLoginRequest = new FaceBookLoginRequest();




    AppCompatEditText editEmailTelefone;
    ShowHidePasswordEditText editPass;

    Button btnLogin;
    Button btnRegistro,btnForgotPass;
    ProgressDialog progressDialog;
    View raiz;

    //Esqueceu a senha? ----Componentes interface da caixa de dialogo Enviar Numero Telefone
    private Dialog dialogOpcaoSenhaEnviarTelefone;
    private AppCompatEditText dialog_editTelefone_telefone;
    private String telefoneRedif_senha;

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogSenhaEnviarTelefoneCodReset;
    private TextView tv_telefone;
    private String telefoneReceberDeNovo;
    private String codigoConfTelef,novaSenha;
    private ShowHidePasswordEditText pinCodigoConfirmacaoTelef,editNovaSenha;

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogConfirmTelefoneSuccesso;
    TextView txtConfirmSucesso;

    String palavraPass,emailTelefone;

    LoginButton loginButton;
    Button fb,logoutBtn;
    CallbackManager callbackManager;
    AccessToken accessToken;
    String facebookToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(AppPref.getInstance().getAuthToken())) {
            launchHomeScreen();
        }
        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_login);


        initViews();

    }


    private void initViews() {

        raiz = findViewById(android.R.id.content);

        editEmailTelefone = findViewById(R.id.editEmailTelefone);
        editPass = findViewById(R.id.editPass);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnForgotPass = findViewById(R.id.btnForgotPass);

        loginButton = findViewById(R.id.loginBtn);
        fb = findViewById(R.id.fb);
        logoutBtn = findViewById(R.id.logoutBtn);

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

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOpcaoSenhaEnviarTelefone.show();

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

        if (dialogOpcaoSenhaEnviarTelefone.getWindow()!=null)
            dialogOpcaoSenhaEnviarTelefone.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

        if (dialogSenhaEnviarTelefoneCodReset.getWindow()!=null)
            dialogSenhaEnviarTelefoneCodReset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //-------------------------------------------------------------

        dialogConfirmTelefoneSuccesso = new Dialog(this);
        dialogConfirmTelefoneSuccesso.setContentView(R.layout.layout_confirmacao_sucesso);
        txtConfirmSucesso = dialogConfirmTelefoneSuccesso.findViewById(R.id.txtConfirmSucesso);
        Button dialog_btn_telefone_sucesso = dialogConfirmTelefoneSuccesso.findViewById(R.id.dialog_btn_telefone_sucesso);
        dialog_btn_telefone_sucesso.setOnClickListener(this);

        if (dialogConfirmTelefoneSuccesso.getWindow()!=null)
            dialogConfirmTelefoneSuccesso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        fb.setEnabled(true);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                loginButton.performClick();
                fb.setEnabled(false);
                fb.setBackground(ContextCompat.getDrawable(LoginActivity.this,R.drawable.facebook_f6_login_grey));
            }
        });


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();

                fb.setEnabled(false);
                fb.setBackground(ContextCompat.getDrawable(LoginActivity.this,R.drawable.facebook_f6_login_grey));

                if (!MetodosUsados.isConnected(10000)) {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LoginActivity.this, "Check Net", Toast.LENGTH_SHORT).show();
                    fb.setEnabled(true);
                    fb.setBackground(ContextCompat.getDrawable(LoginActivity.this,R.drawable.facebook_f6_login));

                } else{

                    loaduserProfile(accessToken);

                }


            }

            @Override
            public void onCancel() {
                fb.setEnabled(true);
                fb.setBackground(ContextCompat.getDrawable(LoginActivity.this,R.drawable.facebook_f6_login));
                Toast.makeText(LoginActivity.this, "onCancel()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                fb.setEnabled(true);
                fb.setBackground(ContextCompat.getDrawable(LoginActivity.this,R.drawable.facebook_f6_login));
                Toast.makeText(LoginActivity.this, "FacebookException: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logOut();

                fb.setEnabled(true);
                fb.setBackground(ContextCompat.getDrawable(LoginActivity.this,R.drawable.facebook_f6_login));
                Toast.makeText(LoginActivity.this, "Logout", Toast.LENGTH_SHORT).show();



            }
        });


    }


    private void verifConecxao() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                mostrarMensagem(LoginActivity.this,R.string.txtMsg);
            } else {
                verificarCamposEmailTelef();
            }
        }

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
            if (emailTelefone.matches("9[1-9][0-9]\\d{6}")){
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

    private void verificarCamposEmailTelef() {
        if (verificarCamposEmailTelefone()) {

            request.password = palavraPass;
            request.rememberMe = true;

            autenticacaoLogin(request);
        }
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
                progressDialog.setMessage("Validando os dados...");
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioAuth userToken = response.body();



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

    private void carregarMeuPerfilFaceBook(UsuarioPerfil usuarioPerfil) {
        progressDialog.setMessage("Carregando dados...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();
                Common.mCurrentUser = usuarioPerfil;
                Intent intent = new Intent(LoginActivity.this, PerfilFacebookActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("mCurrentUser",Common.mCurrentUser);
                startActivity(intent);
                finish();

            }
        },3000);




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
                    txtConfirmSucesso.setText(getString(R.string.msg_sucesso_senha_alterada));
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


    private void autenticacaoFaceBook(FaceBookLoginRequest faceBookLoginRequest,UsuarioPerfil usuarioPerfil) {

        progressDialog.setMessage("Validando os dados...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                carregarMeuPerfilFaceBook(usuarioPerfil);

            }
        },2000);


//        progressDialog.setMessage("Verificando...");
//        progressDialog.show();

//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<UsuarioAuth> call = apiInterface.autenticarFaceBook(faceBookLoginRequest);
//        call.enqueue(new Callback<UsuarioAuth>() {
//            @Override
//            public void onResponse(@NonNull Call<UsuarioAuth> call, @NonNull Response<UsuarioAuth> response) {
//
//                //response.body()==null
//                progressDialog.setMessage("Validando os dados...");
//                if (response.isSuccessful() && response.body() != null) {
//                    UsuarioAuth userToken = response.body();
//
////                    AppPref.getInstance().saveAuthToken(userToken.tokenuser);
////                    AppPref.getInstance().saveTokenTime(userToken.expiracao);
//
//                    carregarMeuPerfilFaceBook(usuarioPerfil);
//
//
//
//
//                } else {
//                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
//                    progressDialog.dismiss();
//
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<UsuarioAuth> call, @NonNull Throwable t) {
//                progressDialog.dismiss();
//                if (!conexaoInternetTrafego(LoginActivity.this,TAG)){
//                    mostrarMensagem(LoginActivity.this,R.string.txtMsg);
//                }else  if ("timeout".equals(t.getMessage())) {
//                    mostrarMensagem(LoginActivity.this,R.string.txtTimeout);
//                }else {
//                    mostrarMensagem(LoginActivity.this,R.string.txtProblemaMsg);
//                }
//                Log.i(TAG,"onFailure" + t.getMessage());
//
//                try {
//                    Snackbar
//                            .make(raiz, t.getMessage(), 4000)
//                            .setActionTextColor(Color.MAGENTA)
//                            .show();
//                } catch (Exception e) {
//                    Log.d(TAG, String.valueOf(e.getMessage()));
//                }
//            }
//        });

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


    private void loaduserProfile(AccessToken newAccessToken){

        progressDialog.setMessage("Verificando...");
        progressDialog.show();

        facebookToken =newAccessToken.getToken();

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String name = first_name + " "+last_name;

                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";

                    UsuarioPerfil usuarioPerfil = new UsuarioPerfil(first_name,last_name,email,image_url);



                    faceBookLoginRequest.accessToken = facebookToken;

                    autenticacaoFaceBook(faceBookLoginRequest,usuarioPerfil);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void launchHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }










}
