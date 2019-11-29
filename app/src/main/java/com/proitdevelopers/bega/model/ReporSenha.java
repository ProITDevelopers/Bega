package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReporSenha implements Serializable {

    @SerializedName("codigoRecuperacao")
    public String codigoRecuperacao;

    @SerializedName("novaPassword")
    public String novaPassword;

}
