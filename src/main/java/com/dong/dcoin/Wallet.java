package com.dong.dcoin;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

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
        float totoal = 0;
        for (Map.Entry<String, TransactionOutput> entry : DChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = entry.getValue();
            if (UTXO.isMine(publicKey)) {
                DChain.UTXOs.put(UTXO.id, UTXO);
            }
        }
        return 0;
    }
}
