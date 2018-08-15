package com.dong.dcoin;

import java.util.Date;

public class Block {

    public String hash;
    public String preHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String preHash, String data) {
        this.preHash = preHash;
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return Util.applySha256(preHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!target.equals(hash.substring(0, difficulty))) {
            hash = calculateHash();
            nonce++;
        }
        System.out.println("Block Mined!" + hash);
    }
}
