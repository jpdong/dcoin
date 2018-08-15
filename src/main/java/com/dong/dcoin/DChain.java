package com.dong.dcoin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DChain {

    public static List<Block> blockChain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 7;
    public static Wallet walletA;
    public static Wallet walletB;
    public static float minimumTransaction = 0;

    public DChain() {
        blockChain.add(new Block("0", "hello world!"));
    }

    public void add(String data) {
        blockChain.add(new Block(blockChain.get(blockChain.size() - 1).hash,data));
    }

    public static void main(String[] args) {
        /*long startTime = System.currentTimeMillis();
        Block first = new Block("0", "hello world!");
        System.out.println("hash origin:" + first.hash);
        first.mineBlock(difficulty);
        blockChain.add(first);
        System.out.println("cost " + (System.currentTimeMillis() - startTime)/1000);
        *//*blockChain.add(new Block(blockChain.get(blockChain.size() - 1).hash,"i am the second block."));
        blockChain.add(new Block(blockChain.get(blockChain.size() - 1).hash,"i am the third block."));*//*
        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainJson);*/

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        walletA = new Wallet();
        walletB = new Wallet();
        System.out.println(Util.getStringFromKey(walletA.privateKey));
        System.out.println(Util.getStringFromKey(walletA.publicKey));
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5);
        transaction.generateSignature(walletA.privateKey);
        System.out.println("is signature verified:" + transaction.verifySignature());
    }

    public boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTargetHead = new String(new char[difficulty]).replace('\0', '0');
        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println(String.format("the %d block hash wrong",i));
                return false;
            }
            if (!currentBlock.preHash.equals(previousBlock.hash)) {
                System.out.println(String.format("the %d and %d block hash wrong", i - 1, i));
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
    }
}
