package com.dong.dcoin;

import java.security.PublicKey;

public class TransactionOutput {

    public String id;
    public PublicKey to;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(PublicKey to, float value, String parentTransactionId) {
        this.to = to;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey == to;
    }
}
