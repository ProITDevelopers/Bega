package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UsuarioAuth implements Serializable {

    @SerializedName("expiracao")
    public String expiracao;

    @SerializedName("tokenuser")
    public String tokenuser;

    @SerializedName("role")
    public String role;



    //ADAAAAOOOOOOO
    @SerializedName("dataExpiracao")
    public String dataExpiracao;

    @SerializedName("token_acesso")
    public String token_acesso;




}
