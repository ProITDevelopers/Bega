package com.proitdevelopers.bega.api;

import com.proitdevelopers.bega.model.Estabelecimento;
import com.proitdevelopers.bega.model.FaceBookLoginRequest;
import com.proitdevelopers.bega.model.Factura;
import com.proitdevelopers.bega.model.LoginRequest;
import com.proitdevelopers.bega.model.Order;
import com.proitdevelopers.bega.model.Produtos;
import com.proitdevelopers.bega.model.RegisterRequest;
import com.proitdevelopers.bega.model.ReporSenha;
import com.proitdevelopers.bega.model.UsuarioAuth;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.proitdevelopers.bega.model.Wallet;
import com.proitdevelopers.bega.model.WalletRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

//    @Headers("Content-type: application/json")
    @POST("/authenticate2")
    Call<UsuarioAuth> autenticarCliente(@Body LoginRequest loginRequest);

    @POST("/Facebook")
    Call<UsuarioAuth> autenticarFaceBook(@Body FaceBookLoginRequest faceBookLoginRequest);


    @POST("/cadastrarcliente")
    Call<Void> registrarCliente(@Body RegisterRequest registerRequest);



    @Multipart
    @POST("/cadastrarcliente")
    Call<Void> registrarUsuario(
            @Part("PrimeiroNome") RequestBody primeiroNome,
            @Part("UltimoNome") RequestBody ultimoNome,
            @Part("UserName") RequestBody usuario,
            @Part("Email") RequestBody email,
            @Part("Password") RequestBody password,
            @Part("ContactoMovel") RequestBody contactoMovel,
            @Part("Sexo") RequestBody sexo,
            @Part("Provincia") RequestBody provincia,
            @Part("Municipio") RequestBody municipio,
            @Part("Bairro") RequestBody bairro,
            @Part("Rua") RequestBody rua,
            @Part("NCasa") RequestBody nCasa,
            @Part MultipartBody.Part imagem
    );



    @POST("/WalletAbrirContaCliente")
    Call<ResponseBody> criarContaWallet(@Body WalletRequest walletRequest);

    @GET("/WalletConsultarSaldo")
    Call<List<Wallet>> getSaldoWallet();


    @PUT("/SolicitarCodigoRecuperacao/{id}")
    Call<Void> enviarTelefone(@Path("id") int numero_telefone);


    @POST("/ReporSenha/{numero}")
    Call<String> enviarConfirCodigo(
                    @Path("numero") String numero_telefone,
                    @Body ReporSenha reporSenha);


    @Multipart
    @POST("/ConfirmacaoPagamentoWallet/{codigoconfirmacao},{codoperacao}")
    Call<List<String>> enviarConfirCodigoPagamento(
            @Part("codigoconfirmacao") RequestBody codigoconfirmacao,
            @Part("codoperacao") RequestBody codoperacao);







    @GET("/PerfilCliente")
    Call<List<UsuarioPerfil>> getMeuPerfil();

    @GET("/PerfilCliente")
    Call<List<UsuarioPerfil>> getPerfilLogin(@Header("Authorization") String bearerToken);

//    Call<UsuarioPerfil> getMeuPerfil(@Header("Cookie") String sessionIdAndToken);

    @PUT("/UpdateDadosPessoaisCliente")
    Call<Void> actualizarPerfil(@Body UsuarioPerfil usuarioPerfil);

    @Multipart
    @POST("/AlterarFotoPerfilCliente")
    Call<ResponseBody> actualizarFotoPerfil(@Part MultipartBody.Part imagem);


    @POST("/FacturaWallet")
    Call<List<String>> facturaWallet(@Body Order order);

    @POST("/FacturaTpa")
    Call<ResponseBody> facturaTPA(@Body Order order);

    @GET("/FacturasActualCliente")
    Call<List<Factura>> getTodasFacturas();


    @GET("/ListarEstabA24h")
    Call<List<Estabelecimento>> getAltasHorasEstabelecimentos();

    @GET("/ListagemEstabelecimentoA")
    Call<List<Estabelecimento>> getAllEstabelecimentos();

    @GET("/ListarEstabPorTipo/{IdTipoEstabelecimento}")
    Call<List<Estabelecimento>> getEstabelecimentosPorTipo(@Path("IdTipoEstabelecimento") int idTipoEstabelecimento);



    @GET("/ListarProdutosEstab/{idE}")
    Call<List<Produtos>> getAllProdutosDoEstabelecimento(@Path("idE") int idEstabelecimento);

    @GET("/ListarProdutos")
    Call<List<Produtos>> getAllProdutos();


}
