package com.dong.dcoin;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionId;
    public PublicKey from;
    public PublicKey to;
    public float value;

    public byte[] signature;

    private static int sequence = 0;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return Util.applySha256(Util.getStringFromKey(from) + Util.getStringFromKey(to) + Float.toString(value) + sequence);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = Util.getStringFromKey(from) + Util.getStringFromKey(to) + Float.toString(value);
        this.signature = Util.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = Util.getStringFromKey(from) + Util.getStringFromKey(to) + Float.toString(value);
        return Util.verifyECDSASig(from, data, signature);
    }

    public boolean processTransaction() {
        if (verifySignature() == false) {
            System.out.println("Transaction Signature failed to verify");
            return false;
        }
        for (TransactionInput input : inputs) {
            input.unspentTransactionOutput = DChain.UTXOs.get(input.transactionOutputId);
        }
        if (getInputValues() < DChain.minimumTransaction) {
            System.out.println("Transaction Inputs too small:" + getInputValues());
            return false;
        }
        float leftOver = getInputValues() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.to, value, transactionId));
        System.out.println(String.format("processTransaction:id(%s):to(%s):value(%f)",transactionId,Util.getStringFromKey(to),value));
        outputs.add(new TransactionOutput(this.from, leftOver, transactionId));
        System.out.println(String.format("processTransaction:id(%s):to(%s):value(%f)",transactionId,Util.getStringFromKey(from),leftOver));
        for (TransactionOutput output : outputs) {
            DChain.UTXOs.put(output.id, output);
        }
        for (TransactionInput input : inputs) {
            if (input.unspentTransactionOutput == null) {
                continue;
            }
            DChain.UTXOs.remove((input.unspentTransactionOutput.id));
        }
        return true;
    }

    public float getInputValues() {
        float total = 0;
        for (TransactionInput input : inputs) {
            if (input.unspentTransactionOutput == null) {
                continue;
            }
            total += input.unspentTransactionOutput.value;
        }
        return total;
    }

    public float getOutputValues() {
        float total = 0;
        for (TransactionOutput output : outputs) {
            total += output.value;
        }
        return total;
    }

    public static class TransactionTypeAdapter extends TypeAdapter<Transaction> {
        @Override
        public void write(JsonWriter out, Transaction transaction) throws IOException {
            out.beginObject();
            out.name("transactionId").value(transaction.transactionId);
            out.name("from").value(Util.getStringFromKey(transaction.from));
            out.name("to").value(Util.getStringFromKey(transaction.to));
            out.name("value").value(Float.toString(transaction.value));
            out.endObject();
        }

        @Override
        public Transaction read(JsonReader in) throws IOException {
            return null;
        }
    }

}
