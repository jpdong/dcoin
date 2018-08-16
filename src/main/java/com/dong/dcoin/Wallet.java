package com.dong.dcoin;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public Map<String, TransactionOutput> unspentOutputMap = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec genParameterSpec = new ECGenParameterSpec("prime192v1");
            generator.initialize(genParameterSpec, random);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        KeyPair keyPair = generator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> entry : DChain.UTXOs.entrySet()) {
            TransactionOutput output = entry.getValue();
            if (output.isMine(publicKey)) {
                unspentOutputMap.put(output.id, output);
                total += output.value;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey to, float value) {
        if (getBalance() < value) {
            System.out.println("No Enough funds  to create transaction.");
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> entry : unspentOutputMap.entrySet()) {
            TransactionOutput output = entry.getValue();
            total += output.value;
            inputs.add(new TransactionInput(output.id));
            if (total > value) {
                break;
            }
        }
        Transaction newTransaction = new Transaction(publicKey, to, value, inputs);
        newTransaction.generateSignature(privateKey);
        for (TransactionInput input : inputs) {
            unspentOutputMap.remove(input.transactionOutputId);
        }
        return newTransaction;
    }
}
