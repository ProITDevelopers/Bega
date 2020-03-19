package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

public class WalletRequest {

    @SerializedName("numeroBi")
    public String numeroBi;

    @SerializedName("dataNasc")
    public String dataNasc;

    @SerializedName("identityType")
    public String identityType;
}
