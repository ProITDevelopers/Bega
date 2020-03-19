package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginEncomenda implements Serializable {

    @SerializedName("telefone")
    public String telefone;

    @SerializedName("palavraPasse")
    public String palavraPasse;




}
