package com.dong.dcoin;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Util {

    public static String applySha256(String input) {
        byte[] hash = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(input.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            return dsa.sign();
        }  catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] sigData) {
        try {
            Signature signature = Signature.getInstance("ECDSA", "BC");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            return signature.verify(sigData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> preTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            preTreeLayer.add(transaction.transactionId);
        }
        List<String> treeLayer = preTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < preTreeLayer.size(); i++) {
                treeLayer.add(applySha256(preTreeLayer.get(i - 1) + preTreeLayer.get(i)));
            }
            count = treeLayer.size();
            preTreeLayer = treeLayer;
        }
        return treeLayer.size() == 1 ? treeLayer.get(0):"";
    }
}
