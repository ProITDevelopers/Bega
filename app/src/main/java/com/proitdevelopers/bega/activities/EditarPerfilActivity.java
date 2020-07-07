package com.proitdevelopers.bega.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.api.ErrorResponce;
import com.proitdevelopers.bega.api.ErrorUtils;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.localDB.AppDatabase;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.msgErro;
import static com.proitdevelopers.bega.helper.Common.msgErroLetras;
import static com.proitdevelopers.bega.helper.Common.msgErroLetrasCaracteres;
import static com.proitdevelopers.bega.helper.Common.msgErroSTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefone;
import static com.proitdevelopers.bega.helper.Common.msgErroTelefoneIguais;
import static com.proitdevelopers.bega.helper.Common.msgQuasePronto;
import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;
import static com.proitdevelopers.bega.helper.MetodosUsados.removeAcentos;

public class EditarPerfilActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int TIRAR_FOTO_CAMARA = 1, ESCOLHER_FOTO_GALERIA = 1995, PERMISSAO_FOTO = 3;

    private Uri selectedImage;
    private String postPath;

    private Dialog caixa_dialogo_foto;

    private String TAG = "EditarPerfilActivity";
    private UsuarioPerfil usuarioPerfil = new UsuarioPerfil();

    private CircleImageView imageView;
    private AppCompatEditText editPrimeiroNome,editUltimoNome;
    private RadioGroup radioGroup;
    private RadioButton radioBtnFem,radioBtnMasc;
    private AppCompatEditText editTelefone,editTelefoneAlternativo;
    private Spinner editCidadeSpiner;
    private AppCompatEditText editMunicipio,editBairro,editRua,editNCasa;
    private Button btnSalvar;
    private View raiz;

    private String primeiroNome,sobreNome,telefone,telefoneAlternativo;
    private String valorGeneroItem,valorCidadeItem;
    private String municipio,bairro,rua,nCasa;
    private ProgressDialog progressDialog;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        if (getIntent()!=null){
//            Common.mCurrentUser = (UsuarioPerfil) getIntent().getSerializableExtra("mCurrentUser");
//        }

        initViews();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando...");
        progressDialog.setCancelable(false);



///carregar dados do Usuario
        usuarioPerfil = AppPref.getInstance().getUser();
        carregarMeuPerfilOffline(usuarioPerfil);

    }


    private void initViews() {

        imageView = findViewById(R.id.img);
        imageView.setOnClickListener(this);


        editPrimeiroNome = findViewById(R.id.editPrimeiroNome);
        editUltimoNome = findViewById(R.id.editUltimoNome);


        radioGroup = findViewById(R.id.radioGroup);
        radioBtnFem = findViewById(R.id.radioBtnFem);
        radioBtnMasc = findViewById(R.id.radioBtnMasc);

        editTelefone = findViewById(R.id.editTelefone);
        editTelefoneAlternativo = findViewById(R.id.editTelefoneAlternativo);



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



        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(this);

        raiz = findViewById(android.R.id.content);


        caixa_dialogo_foto = new Dialog(this);
        caixa_dialogo_foto.setContentView(R.layout.caixa_de_dialogo_foto);
        caixa_dialogo_foto.setCancelable(false);

        if (caixa_dialogo_foto.getWindow()!=null)
            caixa_dialogo_foto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Botão em caixa de dialogo foto
        Button btnCamara = caixa_dialogo_foto.findViewById(R.id.btnCamara);
        Button btnGaleria = caixa_dialogo_foto.findViewById(R.id.btnGaleria);
//        Button btnCancelar_dialog = caixa_dialogo_foto.findViewById(R.id.btnCancelar_dialog);
        ImageView btnCancelar_dialog = caixa_dialogo_foto.findViewById(R.id.btnCancelar_dialog);

        btnCamara.setOnClickListener(this);
        btnGaleria.setOnClickListener(this);
        btnCancelar_dialog.setOnClickListener(this);


    }


    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {
        try {


            Picasso.with(this).load(usuarioPerfil.imagem).fit().centerCrop().placeholder(R.drawable.ic_camera).into(imageView);


            editPrimeiroNome.setText(usuarioPerfil.primeiroNome);
            editUltimoNome.setText(usuarioPerfil.ultimoNome);


            if (usuarioPerfil.sexo.equals("F")){
                radioBtnFem.setChecked(true);
            } else {
                radioBtnMasc.setChecked(true);
            }

            editTelefone.setText(usuarioPerfil.contactoMovel);
            editTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);


            valorCidadeItem = usuarioPerfil.provincia;

            for(int i=0; i < editCidadeSpiner.getAdapter().getCount(); i++) {
                if(valorCidadeItem.trim().equals(editCidadeSpiner.getItemAtPosition(i).toString())){
                    editCidadeSpiner.setSelection(i);
                    break;
                }
            }

            editMunicipio.setText(usuarioPerfil.municipio);
            editBairro.setText(usuarioPerfil.bairro);
            editRua.setText(usuarioPerfil.rua);

//            if (usuarioPerfil.nCasa.startsWith("nº"))
//                usuarioPerfil.nCasa = usuarioPerfil.nCasa.replace(("nº"),"");
//
//            if (usuarioPerfil.nCasa.startsWith("0"))
//                usuarioPerfil.nCasa = usuarioPerfil.nCasa.replace(("0"),"");

            editNCasa.setText(usuarioPerfil.nCasa);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    private boolean verificarCampo() {

        primeiroNome = editPrimeiroNome.getText().toString().trim();
        sobreNome = editUltimoNome.getText().toString().trim();

        telefone = editTelefone.getText().toString().trim();
        telefoneAlternativo = editTelefoneAlternativo.getText().toString().trim();

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

            primeiroNome = removeAcentos(primeiroNome);
            sobreNome = removeAcentos(sobreNome);
        }catch (Exception e){
            e.printStackTrace();
        }



        if (primeiroNome.isEmpty()){
            editPrimeiroNome.setError(msgErro);
            return false;
        }

        if (!primeiroNome.matches("^[a-zA-Z\\s]+$")){
            editPrimeiroNome.setError(msgErroLetras);
            return false;
        }

        if (primeiroNome.length()<3){
            editPrimeiroNome.setError(msgErroLetrasCaracteres);
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

        if (sobreNome.length()<3){
            editUltimoNome.setError(msgErroLetrasCaracteres);
            return false;
        }


        if (telefone.isEmpty()){
            editTelefone.setError(msgErroSTelefone);
            return false;
        }

        if (!telefone.matches("9[1-9][0-9]\\d{6}")){
            editTelefone.setError(msgErroTelefone);
            return false;
        }

        if (telefone.equals(telefoneAlternativo)){
            editTelefoneAlternativo.setError(msgErroTelefoneIguais);
            return false;
        }

        if (valorGeneroItem.isEmpty()){
            Toast.makeText(this, "Escolha o género", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (valorCidadeItem.isEmpty()){
            Toast.makeText(this, "Escolha a província", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (municipio.isEmpty()){
            editMunicipio.setError(msgErro);
            return false;
        }

        if (municipio.length()<3){
            editMunicipio.setError(msgErroLetrasCaracteres);
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

        if (bairro.length()<3){
            editBairro.setError(msgErroLetrasCaracteres);
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

//        if (rua.length()<3){
//            editRua.setError(msgErroLetrasCaracteres);
//            return false;
//        }

        if (nCasa.isEmpty()){
            editNCasa.setError(msgErro);
            return false;
        }

//        if (nCasa.length()==1){
//            nCasa = "nº"+nCasa;
//        }



        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img:
                verificarPermissaoFotoCameraGaleria();
                break;

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

            case R.id.btnSalvar:
                if (verificarCampo()){
                    verifConecxaoSalvar();
                }
                break;
        }
    }

    private void verificarPermissaoFotoCameraGaleria() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSAO_FOTO);
        }

        if (ContextCompat.checkSelfPermission(EditarPerfilActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(EditarPerfilActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(view.getContext(),"Precisa aceitar as permissões para escolher uma foto de perfil",Toast.LENGTH_SHORT).show();
        } else {
            caixa_dialogo_foto.show();
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
    }

    private void cortarImagemCrop(Uri imagemUri) {
        CropImage.activity(imagemUri)
                .setActivityTitle("BEGA")
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                .start(this);


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


    private boolean verificaUriFoto() {

        if (selectedImage == null) {
            Toast.makeText(this, "Adicione uma foto", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void salvarFoto() {
        File file = new File(postPath);
        try {
//            RequestBody filepart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), file);
            RequestBody filepart = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part file1 = MultipartBody.Part.createFormData("FotoCapa", file.getName(), filepart);
            if (verificaUriFoto()) {
                progressDialog.setMessage("Salvando a foto...");
                progressDialog.show();
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<ResponseBody> enviarFoto = apiInterface.actualizarFotoPerfil(file1);

                enviarFoto.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();

                            Snackbar
                                    .make(raiz, "Foto atualizada com sucesso!", 3000)
                                    .setActionTextColor(Color.WHITE)
                                    .show();


                        } else {
                            progressDialog.dismiss();
                            ErrorResponce errorResponce = ErrorUtils.parseError(response);
                            mostrarMensagem(EditarPerfilActivity.this, errorResponce.getError());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                        progressDialog.dismiss();
                        if (!conexaoInternetTrafego(EditarPerfilActivity.this,TAG)) {
                            mostrarMensagem(EditarPerfilActivity.this, R.string.txtMsg);
                        } else if ("timeout".equals(t.getMessage())) {
                            mostrarMensagem(EditarPerfilActivity.this, R.string.txtTimeout);
                        } else {
                            mostrarMensagem(EditarPerfilActivity.this, R.string.txtProblemaMsg);
                        }
                        Log.i(TAG, "onFailure" + t.getMessage());
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSAO_FOTO) {

            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                caixa_dialogo_foto.show();
            } else {
                Toast.makeText(this, "This app needs Camera and Storage permissions to continue!", Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ESCOLHER_FOTO_GALERIA && resultCode == RESULT_OK && data != null) {
            selectedImage = CropImage.getPickImageResultUri(EditarPerfilActivity.this, data);
            cortarImagemCrop(selectedImage);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    imageView.setImageBitmap(bitmap);

                    postPath = selectedImage.getPath();

                    salvarFoto();

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

    private void verifConecxaoSalvar() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                mostrarMensagem(EditarPerfilActivity.this,R.string.txtMsg);
            } else {
                actualizarDadosUsuario();
            }
        }
    }

    private void actualizarDadosUsuario() {
        progressDialog.setMessage(msgQuasePronto);
        progressDialog.show();


        usuarioPerfil = new UsuarioPerfil(primeiroNome,sobreNome,telefone,telefoneAlternativo,valorCidadeItem,municipio,bairro,rua,nCasa,valorGeneroItem);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> call = apiInterface.actualizarPerfil(usuarioPerfil);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();

                    Snackbar
                            .make(raiz, "Seus dados foram salvos!", 3000)
                            .setActionTextColor(Color.WHITE)
                            .show();




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
                if (!conexaoInternetTrafego(EditarPerfilActivity.this,TAG)){
                    mostrarMensagem(EditarPerfilActivity.this,R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(EditarPerfilActivity.this,R.string.txtTimeout);
                }else {
                    mostrarMensagem(EditarPerfilActivity.this,R.string.txtProblemaMsg);
                }


            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
        int id = parent.getId();

        if (id == R.id.editCidadeSpiner){
            valorCidadeItem = parent.getItemAtPosition(position).toString();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


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
