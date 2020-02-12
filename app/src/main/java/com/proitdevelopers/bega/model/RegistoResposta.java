package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegistoResposta {

    @SerializedName("PrimeiroNome")
    public List<String>PrimeiroNome;

    @SerializedName("UltimoNome")
    public List<String>UltimoNome;
}
