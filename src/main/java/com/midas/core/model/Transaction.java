package com.midas.core.model;

public class Transaction {

    private Long senderId;
    private Long recipientId;
    private double amount;

    public Transaction() {
    }

    public Transaction(Long senderId, Long recipientId, double amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{senderId=" + senderId + ", recipientId=" + recipientId + ", amount=" + amount + "}";
    }
}

