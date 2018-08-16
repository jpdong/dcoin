package com.dong.dcoin;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

    public String hash;
    public String preHash;
    public String root;
    public long timeStamp;
    public int nonce;

    public List<Transaction> transactions = new ArrayList<>();

    public Block(String preHash) {
        this.preHash = preHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return Util.applySha256(preHash + Long.toString(timeStamp) + Integer.toString(nonce) + root);
    }

    public void mineBlock(int difficulty) {
        this.root = Util.getRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        int temp = nonce;
        while (!target.equals(hash.substring(0, difficulty))) {
            nonce = temp;
            hash = calculateHash();
            temp++;
        }
        System.out.println("Block Mined!" + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        if (!"0".equals(preHash)) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction process failed.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("addTransaction success.");
        return true;
    }
}
