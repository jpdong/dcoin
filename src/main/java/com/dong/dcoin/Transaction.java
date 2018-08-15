package com.dong.dcoin;

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

    public Transaction(PublicKey from, PublicKey to, float value) {
        this.from = from;
        this.to = to;
        this.value = value;
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
            input.UTXO = DChain.UTXOs.get(input.transactionOutputId);
        }
        if (getInputValues() < DChain.minimumTransaction) {
            System.out.println("Transaction Inputs too small:" + getInputValues());
            return false;
        }
        float leftOver = getInputValues() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.to, value, transactionId));
        outputs.add(new TransactionOutput(this.from, leftOver, transactionId));
        for (TransactionOutput output : outputs) {
            DChain.UTXOs.put(output.id, output);
        }
        for (TransactionInput input : inputs) {
            if (input.UTXO == null) {
                continue;
            }
            DChain.UTXOs.remove((input.UTXO.id));
        }
        return true;
    }

    public float getInputValues() {
        float total = 0;
        for (TransactionInput input : inputs) {
            if (input.UTXO == null) {
                continue;
            }
            total += input.UTXO.value;
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
}
