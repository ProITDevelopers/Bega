package com.proitdevelopers.bega.api;

import com.proitdevelopers.bega.model.LoginEncomenda;
import com.proitdevelopers.bega.model.UsuarioAuth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterfaceEncomenda {


    @POST("/api/login/dono/encomenda")
    Call<UsuarioAuth> loginADAOCliente(@Body LoginEncomenda loginADAO);

    @GET
    Call<String> getPath(@Url String url);


}
