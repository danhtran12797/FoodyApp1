package com.danhtran12797.thd.foodyapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountLove {

    @SerializedName("idProduct")
    @Expose
    private String idProduct;
    @SerializedName("idUser")
    @Expose
    private String idUser;

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

}