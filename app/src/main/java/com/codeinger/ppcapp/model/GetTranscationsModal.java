package com.codeinger.ppcapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTranscationsModal {

    @SerializedName("transactionType")
    @Expose
    private String transactionType;
    @SerializedName("transactionDate")
    @Expose
    private String transactionDate;
    @SerializedName("transactionTime")
    @Expose
    private String transactionTime;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("title")
    @Expose
    private String title;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GetTranscationsModal(String transactionType, Integer amount, String title
            ,String transactionTime,String transactionDate) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.title = title;
        this.transactionTime = transactionTime;
        this.transactionDate = transactionDate;
    }
}