package com.dong.dcoin;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
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
        this.id = Util.applySha256(Util.getStringFromKey(to) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey == to;
    }
}
