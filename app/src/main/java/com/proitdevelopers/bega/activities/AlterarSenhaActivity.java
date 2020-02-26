package com.proitdevelopers.bega.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.model.ReporSenha;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.msgAEnviarTelefone;
import static com.proitdevelopers.bega.helper.Common.msgEnviandoCodigo;
import static com.proitdevelopers.bega.helper.Common.msgErro;
import static com.proitdevelopers.bega.helper.Common.msgErroSTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefone;
import static com.proitdevelopers.bega.helper.Common.msgReenviarNumTelef;
import static com.proitdevelopers.bega.helper.Common.msgSupporte;
import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.esconderTeclado;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class AlterarSenhaActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "AlterarSenhaActivity";

    private AppCompatEditText dialog_editTelefone_telefone;
    Button dialog_btn_cancelar_enviar_telefone,dialog_btn_telefone_redif_enviar_telefone;

    private String telefoneRedif_senha;
    ProgressDialog progressDialog;


    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogSenhaEnviarTelefoneCodReset;
    private TextView tv_telefone;
    private String telefoneReceberDeNovo;
    private String codigoConfTelef,novaSenha;
    private ShowHidePasswordEditText pinCodigoConfirmacaoTelef,editNovaSenha;

    //Esqueceu a senha? ----EnviarCodRedifinicao Telefone
    private Dialog dialogConfirmTelefoneSuccesso;
    TextView txtConfirmSucesso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);

        initViews();




    }

    private void initViews(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        dialog_editTelefone_telefone = findViewById(R.id.dialog_editTelefone_telefone);
        dialog_btn_cancelar_enviar_telefone = findViewById(R.id.dialog_btn_cancelar_enviar_telefone);
        dialog_btn_telefone_redif_enviar_telefone = findViewById(R.id.dialog_btn_telefone_redif_enviar_telefone);

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_btn_cancelar_enviar_telefone:
                esconderTeclado(AlterarSenhaActivity.this);
                cancelarEnvioTelefone();
                break;

            case R.id.dialog_btn_telefone_redif_enviar_telefone:
                esconderTeclado(AlterarSenhaActivity.this);
                enviarTelefonelRedif();
                break;

            case R.id.receberDeNovoTelefone:
                if (!TextUtils.isEmpty(telefoneReceberDeNovo)) {
                    enviarTelefoneRedifDeNovo();
                } else {
                    esconderTeclado(AlterarSenhaActivity.this);
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtTentarmaistarde);
                }
                break;

            case R.id.btn_enviar_cod_resetTelef:
                if (verificarCampoTelef()){
                    esconderTeclado(AlterarSenhaActivity.this);
                    enviarCodRedifinicaoTelef();
                }
                break;

            case R.id.linearBtnFecharTelef:
                esconderTeclado(AlterarSenhaActivity.this);
                dialogSenhaEnviarTelefoneCodReset.cancel();
                limparPinView(pinCodigoConfirmacaoTelef,editNovaSenha);
                break;

            case R.id.dialog_btn_telefone_sucesso:
                esconderTeclado(AlterarSenhaActivity.this);
                dialogConfirmTelefoneSuccesso.cancel();
                cancelarEnvioTelefone();
                break;
        }
    }

    private void cancelarEnvioTelefone() {
        dialog_editTelefone_telefone.setText(null);
        dialog_editTelefone_telefone.setError(null);
        finish();
    }

    private void enviarTelefonelRedif() {
        if (verificarTelefone()) {
            mandarTelefoneResetSenha(telefoneRedif_senha);
        }
    }

    private boolean verificarTelefone() {

        if (dialog_editTelefone_telefone.getText() != null) {
            telefoneRedif_senha = dialog_editTelefone_telefone.getText().toString().trim();
            if (!telefoneRedif_senha.matches("9[1-9][0-9]\\d{6}")) {
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

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> enviarTelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
        progressDialog.setMessage(msgAEnviarTelefone);
        progressDialog.show();
        enviarTelefoneReset.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {

                    progressDialog.cancel();
                    dialog_editTelefone_telefone.setText(null);

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
                if (!conexaoInternetTrafego(AlterarSenhaActivity.this,TAG)){
                    mostrarMensagem(AlterarSenhaActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(AlterarSenhaActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(AlterarSenhaActivity.this,R.string.txtProblemaMsg);
                }
                Log.i(TAG,"onFailure" + t.getMessage());
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
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtCodigoenviado);
                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    Toast.makeText(AlterarSenhaActivity.this, errorResponce.getError(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!conexaoInternetTrafego(AlterarSenhaActivity.this,TAG)) {
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtMsg);
                } else if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtTimeout);
                } else {
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtProblemaMsg);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });

    }

    private boolean verificarCampoTelef() {
        codigoConfTelef = pinCodigoConfirmacaoTelef.getText().toString();
        novaSenha = editNovaSenha.getText().toString();

        if (codigoConfTelef.isEmpty()) {
            mostrarMensagem(AlterarSenhaActivity.this, R.string.txtMsgCondConf);
            pinCodigoConfirmacaoTelef.requestFocus();
            pinCodigoConfirmacaoTelef.setError(msgErro);
            return false;
        }
        if (codigoConfTelef.length() <2) {
            mostrarMensagem(AlterarSenhaActivity.this, R.string.txtMsgCondConfCod);
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
                    mostrarMensagem(AlterarSenhaActivity.this, "errorResponce.getError()");
                    progressDialog.cancel();
                } else {

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
                if (!conexaoInternetTrafego(AlterarSenhaActivity.this,TAG)) {
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtMsg);
                } else if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtTimeout);
                } else {
                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtProblemaMsg);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });



    }

    private void limparPinView(ShowHidePasswordEditText pinCodigoConfirmacaoTelef, ShowHidePasswordEditText editNovaSenha) {
        pinCodigoConfirmacaoTelef.setText(null);
        editNovaSenha.setText(null);
    }
}
