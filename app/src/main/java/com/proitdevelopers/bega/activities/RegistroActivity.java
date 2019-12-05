package com.proitdevelopers.bega.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.model.RegisterRequest;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.msgErro;
import static com.proitdevelopers.bega.helper.Common.msgErroEspaco;
import static com.proitdevelopers.bega.helper.Common.msgErroLetras;
import static com.proitdevelopers.bega.helper.Common.msgErroSTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroSenha;
import static com.proitdevelopers.bega.helper.Common.msgErroSenhaDiferente;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefone;
import static com.proitdevelopers.bega.helper.Common.msgQuasePronto;
import static com.proitdevelopers.bega.helper.MetodosUsados.esconderTeclado;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;
import static com.proitdevelopers.bega.helper.MetodosUsados.removeAcentos;
import static com.proitdevelopers.bega.helper.MetodosUsados.validarEmail;


public class RegistroActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int TIRAR_FOTO_CAMARA = 1, ESCOLHER_FOTO_GALERIA = 1995, PERMISSAO_FOTO = 3;

    private Uri selectedImage;
    private String postPath;

    private Dialog caixa_dialogo_foto;

    private String TAG = "RegistroActivity";

    private CircleImageView imageView;
    private AppCompatEditText editNomeUtilizador, editPrimeiroNome,editUltimoNome,editEmail,editTelefone;
    private AppCompatEditText editMunicipio,editBairro,editRua,editNCasa;
    private ShowHidePasswordEditText editPass,editConfirmPass;
    private Spinner editCidadeSpiner;
    private RadioGroup radioGroup;
    private RadioButton radioBtnFem,radioBtnMasc;
    private Button btnRegistro,btnLogin;
    private ProgressDialog progressDialog;
    private View raiz;

    private String nomeUtilizador,primeiroNome,sobreNome,email,telefone,senha,senhaConf;
    private String valorGeneroItem,valorCidadeItem;
    private String municipio,bairro,rua,nCasa;

    private Dialog dialogConfirmTelefoneSuccesso;
    TextView txtConfirmSucesso;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Common.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_registro);
        initViews();

    }

    private void initViews() {

        imageView = findViewById(R.id.img);

        editNomeUtilizador = findViewById(R.id.editNomeUtilizador);
        editPrimeiroNome = findViewById(R.id.editPrimeiroNome);
        editUltimoNome = findViewById(R.id.editUltimoNome);
        editEmail = findViewById(R.id.editEmail);

        editPass = findViewById(R.id.editPass);
        editConfirmPass = findViewById(R.id.editConfirmPass);

        editTelefone = findViewById(R.id.editTelefone);


        radioGroup = findViewById(R.id.radioGroup);
        radioBtnFem = findViewById(R.id.radioBtnFem);
        radioBtnMasc = findViewById(R.id.radioBtnMasc);

        editCidadeSpiner = findViewById(R.id.editCidadeSpiner);



        ArrayAdapter<CharSequence> adapterCidade = ArrayAdapter.createFromResource(this,
                R.array.cidade, android.R.layout.simple_spinner_item);
        adapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCidadeSpiner.setAdapter(adapterCidade);
        editCidadeSpiner.setOnItemSelectedListener(this);


        editMunicipio = findViewById(R.id.editMunicipio);
        editBairro = findViewById(R.id.editBairro);
        editRua = findViewById(R.id.editRua);
        editNCasa = findViewById(R.id.editNCasa);



        btnRegistro = findViewById(R.id.btnRegistro);
        btnLogin = findViewById(R.id.btnLogin);

        raiz = findViewById(android.R.id.content);

        progressDialog = new ProgressDialog(RegistroActivity.this);
        progressDialog.setCancelable(false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermissaoFotoCameraGaleria();

            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarCampo()){
                    verifConecxao();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        caixa_dialogo_foto = new Dialog(this);
        caixa_dialogo_foto.setContentView(R.layout.caixa_de_dialogo_foto);
        caixa_dialogo_foto.setCancelable(false);

        //Botão em caixa de dialogo foto
        Button btnCamara = caixa_dialogo_foto.findViewById(R.id.btnCamara);
        Button btnGaleria = caixa_dialogo_foto.findViewById(R.id.btnGaleria);
        Button btnCancelar_dialog = caixa_dialogo_foto.findViewById(R.id.btnCancelar_dialog);

        btnCamara.setOnClickListener(this);
        btnGaleria.setOnClickListener(this);
        btnCancelar_dialog.setOnClickListener(this);


        dialogConfirmTelefoneSuccesso = new Dialog(this);
        dialogConfirmTelefoneSuccesso.setContentView(R.layout.layout_confirmacao_sucesso);
        txtConfirmSucesso = dialogConfirmTelefoneSuccesso.findViewById(R.id.txtConfirmSucesso);
        Button dialog_btn_telefone_sucesso = dialogConfirmTelefoneSuccesso.findViewById(R.id.dialog_btn_telefone_sucesso);
        dialog_btn_telefone_sucesso.setOnClickListener(this);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void verificarPermissaoFotoCameraGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSAO_FOTO);
        }

        if (ContextCompat.checkSelfPermission(RegistroActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(RegistroActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(view.getContext(),"Precisa aceitar as permissões para escolher uma foto de perfil",Toast.LENGTH_SHORT).show();
        } else {
            caixa_dialogo_foto.show();
        }
    }




    private boolean verificarCampo() {

        nomeUtilizador = editNomeUtilizador.getText().toString().trim();
        primeiroNome = editPrimeiroNome.getText().toString().trim();
        sobreNome = editUltimoNome.getText().toString().trim();
        email = editEmail.getText().toString().trim();
        telefone = editTelefone.getText().toString().trim();
        senha = editPass.getText().toString().trim();
        senhaConf = editConfirmPass.getText().toString().trim();

        municipio = editMunicipio.getText().toString().trim();
        bairro = editBairro.getText().toString().trim();
        rua = editRua.getText().toString().trim();
        nCasa = editNCasa.getText().toString().trim();

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioBtnFem:
                valorGeneroItem = radioBtnFem.getText().toString().trim();
                break;
            case R.id.radioBtnMasc:
                valorGeneroItem= radioBtnMasc.getText().toString().trim();
                break;

        }

        try {
            nomeUtilizador = removeAcentos(nomeUtilizador);
            primeiroNome = removeAcentos(primeiroNome);
            sobreNome = removeAcentos(sobreNome);
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

        if (primeiroNome.isEmpty()){
            editPrimeiroNome.setError(msgErro);
            return false;
        }

        if (!primeiroNome.matches("^[a-zA-Z\\s]+$")){
            editPrimeiroNome.setError(msgErroLetras);
            return false;
        }

        if (sobreNome.isEmpty()){
            editUltimoNome.setError(msgErro);
            return false;
        }

        if (!sobreNome.matches("^[a-zA-Z\\s]+$")){
            editUltimoNome.setError(msgErroLetras);
            return false;
        }

        if (email.isEmpty()){
            editEmail.setError(msgErro);
            return false;
        }

        if (!validarEmail(email)){
            editEmail.setError(msgErro);
            return false;
        }


        if (telefone.isEmpty()){
            editTelefone.setError(msgErroSTelefone);
            return false;
        }

        if (!telefone.matches("9[1-9][1-9]\\d{6}")){
            editTelefone.setError(msgErroTelefone);
            return false;
        }



        if (senha.isEmpty()){
            editPass.setError(msgErro);
            return false;
        }

        if (senha.length()<5){
            editPass.setError(msgErroSenha);
            return false;
        }

        if (senhaConf.length()<5){
            editConfirmPass.setError(msgErroSenha);
            return false;
        }

        if (!senha.equals(senhaConf)){
            editConfirmPass.setError(msgErroSenhaDiferente);
            return false;
        }


        if (municipio.isEmpty()){
            editMunicipio.setError(msgErro);
            return false;
        }

//        if (!municipio.matches("^[a-zA-Z\\s]+$")){
//            editMunicipio.setError(msgErroLetras);
//            return false;
//        }

        if (bairro.isEmpty()){
            editBairro.setError(msgErro);
            return false;
        }

//        if (!bairro.matches("^[a-zA-Z\\s]+$")){
//            editBairro.setError(msgErroLetras);
//            return false;
//        }

        if (rua.isEmpty()){
            editRua.setError(msgErro);
            return false;
        }

        if (nCasa.isEmpty()){
            editNCasa.setError(msgErro);
            return false;
        }

        if (nCasa.length()==1){
            nCasa = "0"+nCasa;
        }

        if (selectedImage == null) {
            Toast.makeText(this, "Adicione a foto", Toast.LENGTH_SHORT).show();
            return false;
        }



        return true;
    }

    private void registrarUsuario(){

        progressDialog.setMessage(msgQuasePronto);
        progressDialog.show();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.usuario = nomeUtilizador;
        registerRequest.primeiroNome = primeiroNome;
        registerRequest.ultimoNome = sobreNome;
        registerRequest.email = email;
        registerRequest.contactoMovel = telefone;
        registerRequest.password = senha;
        registerRequest.provincia = valorCidadeItem;
        registerRequest.municipio = municipio;
        registerRequest.bairro = bairro;
        registerRequest.rua = rua;
        registerRequest.nCasa = nCasa;
        registerRequest.sexo = valorGeneroItem;
        File file = new File(postPath);


        RequestBody primeiroNome = RequestBody.create(MultipartBody.FORM,registerRequest.primeiroNome);
        RequestBody ultimoNome = RequestBody.create(MultipartBody.FORM, registerRequest.ultimoNome);
        RequestBody usuario = RequestBody.create(MultipartBody.FORM,registerRequest.usuario);
        RequestBody email = RequestBody.create(MultipartBody.FORM, registerRequest.email);
        RequestBody password = RequestBody.create(MultipartBody.FORM, registerRequest.password);
        RequestBody contactoMovel = RequestBody.create(MultipartBody.FORM, registerRequest.contactoMovel);
        RequestBody sexo = RequestBody.create(MultipartBody.FORM, registerRequest.sexo);
        RequestBody provincia = RequestBody.create(MultipartBody.FORM, registerRequest.provincia);
        RequestBody municipio = RequestBody.create(MultipartBody.FORM, registerRequest.municipio);
        RequestBody bairro = RequestBody.create(MultipartBody.FORM, registerRequest.bairro);
        RequestBody rua = RequestBody.create(MultipartBody.FORM, registerRequest.rua);
        RequestBody nCasa = RequestBody.create(MultipartBody.FORM, registerRequest.nCasa);


        RequestBody filepart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)),file);

        MultipartBody.Part file1 = MultipartBody.Part.createFormData("imagem",file.getName(),filepart);



//        ApiInterface apiInterface = ApiClient.apiClient().create(ApiInterface.class);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<Void> call = apiInterface.registrarCliente(registerRequest);
        Call<Void> call = apiInterface.registrarUsuario(primeiroNome,ultimoNome,
                usuario,email,password,contactoMovel,sexo,
                provincia,municipio,bairro,rua,nCasa,file1);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    txtConfirmSucesso.setText(getString(R.string.msg_conta_criada_sucesso));
                    dialogConfirmTelefoneSuccesso.show();

                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    progressDialog.dismiss();

                    try {
                        Snackbar
                                .make(raiz, errorResponce.getError(), 4000)
                                .setActionTextColor(Color.WHITE)
                                .show();
                    }catch (Exception e){
                        Log.i(TAG,"autenticacaoVerif snakBar" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(RegistroActivity.this,TAG)){
                    mostrarMensagem(RegistroActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(RegistroActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(RegistroActivity.this,R.string.txtProblemaMsg);
                }
                Log.i(TAG,"onFailure" + t.getMessage());

            }
        });

    }

    private void verifConecxao() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                mostrarMensagem(RegistroActivity.this,R.string.txtMsg);
            } else {
                registrarUsuario();
            }
        }

    }



    private void launchHomeScreen() {
        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.editCidadeSpiner) {
            valorCidadeItem = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnCamara:
                caixa_dialogo_foto.dismiss();
                pegarFotoCamara();
                break;
            case R.id.btnGaleria:
                caixa_dialogo_foto.dismiss();
                pegarFotoGaleria();
                break;
            case R.id.btnCancelar_dialog:
                caixa_dialogo_foto.dismiss();
                break;

            case R.id.dialog_btn_telefone_sucesso:
                esconderTeclado(RegistroActivity.this);
                dialogConfirmTelefoneSuccesso.cancel();
                launchHomeScreen();
                break;
        }
    }




    private void pegarFotoGaleria() {
        Intent galeria = new Intent();
        galeria.setAction(Intent.ACTION_GET_CONTENT);
        galeria.setType("image/*");
        startActivityForResult(galeria, ESCOLHER_FOTO_GALERIA);
    }



    private void pegarFotoCamara() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            //Create a file to store the image
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.proitdevelopers.bega.provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(cameraIntent,
                        TIRAR_FOTO_CAMARA);
            }
        }

//        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(pictureIntent,
//                    TIRAR_FOTO_CAMARA);
//        }
//        startActivityForResult(pictureIntent, TIRAR_FOTO_CAMARA);
    }

    private void cortarImagemCrop(Uri imagemUri) {
        CropImage.activity(imagemUri)
                .setActivityTitle("BEGA")
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                .start(this);


    }

    public Bitmap reduzirImagem(Bitmap image, int maxSize) {
        int width = 10;
        int height = 10;

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "foto_bega", null);
        return Uri.parse(path);
    }


    String imageFilePath;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ESCOLHER_FOTO_GALERIA && resultCode == RESULT_OK && data != null) {
            selectedImage = CropImage.getPickImageResultUri(RegistroActivity.this, data);
            cortarImagemCrop(selectedImage);
        }

//        if (requestCode == TIRAR_FOTO_CAMARA && resultCode == RESULT_OK && data != null) {
//
//            try {
//                Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
//                Bitmap fotoReduzida = reduzirImagem(bitmap1, 500);
//                Log.i("urirranadka", bitmap1.getWidth() + "algumacoisa");
//
//
//                selectedImage = getImageUri(getApplicationContext(), fotoReduzida);
//
//                cortarImagemCrop(selectedImage);
//
//
//            } catch (Exception e) {
//                Log.i(TAG, "Erro onActivityResult" + e.getMessage());
//            }
//
//        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    imageView.setImageBitmap(bitmap);

                    postPath = selectedImage.getPath();

                } catch (IOException e) {
                    Log.i(TAG, "ERRO CROPIMAGE" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == TIRAR_FOTO_CAMARA && resultCode == RESULT_OK ) {

            selectedImage = Uri.fromFile(photoFile);

            cortarImagemCrop(selectedImage);

        }


    }






}
