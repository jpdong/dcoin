package com.dong.dcoin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DChain {

    public static List<Block> blockChain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;
    public static float minimumTransaction = 0.1f;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinBase = new Wallet();
        genesisTransaction = new Transaction(coinBase.publicKey, walletA.publicKey, 100, null);
        genesisTransaction.generateSignature(coinBase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.to, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis Block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.hash);
        System.out.println("wallet a balance is : " + walletA.getBalance());
        System.out.println("wallet a send 40 to wallet b...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40));
        addBlock(block1);
        System.out.println("wallet a balance is : " + walletA.getBalance());
        System.out.println("wallet b balance is : " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("wallet a send 1000 to wallet b...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000));
        addBlock(block2);
        System.out.println("wallet a balance is : " + walletA.getBalance());
        System.out.println("wallet b balance is : " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("wallet b send 20 to wallet a...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 50));
        block3.addTransaction(walletA.sendFunds(walletB.publicKey, 50));
        block3.addTransaction(walletA.sendFunds(walletB.publicKey, 30));
        addBlock(block3);
        System.out.println("wallet a balance is : " + walletA.getBalance());
        System.out.println("wallet b balance is : " + walletB.getBalance());

        printChain(blockChain);
        System.out.println("is chain valid:" + isChainValid());
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTargetHead = new String(new char[difficulty]).replace('\0', '0');
        Map<String, TransactionOutput> tempUTXO = new HashMap<>();
        tempUTXO.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println(String.format("the %d block hash wrong",i));
                System.out.println(String.format("the %d block hash wrong:store hash:%s",i,currentBlock.hash));
                System.out.println(String.format("the %d block hash wrong:calculate hash:%s",i,currentBlock.calculateHash()));
                return false;
            }
            if (!currentBlock.preHash.equals(previousBlock.hash)) {
                System.out.println(String.format("the %d and %d block hash wrong", i - 1, i));
                return false;
            }
            if (!hashTargetHead.equals(currentBlock.hash.substring(0, difficulty))) {
                System.out.println(String.format("the %d block hasn't been mined"));
                return false;
            }

            TransactionOutput tempOutput;
            for (int j = 0; j < currentBlock.transactions.size(); j++) {
                Transaction currentTX = currentBlock.transactions.get(j);
                if (!currentTX.verifySignature()) {
                    System.out.println(String.format("the %d block %d transaction's signature is invalid.",i,j));
                    return false;
                }
                if (currentTX.getInputValues() != currentTX.getOutputValues()) {
                    System.out.println(String.format("the %d block %d transaction's inputs are not equal to outputs.",i,j));
                    return false;
                }
                for (TransactionInput input : currentTX.inputs) {
                    tempOutput = tempUTXO.get(input.transactionOutputId);
                    if (tempOutput == null) {
                        System.out.println(String.format("the %d block %d transaction's input(%s) is missing.",i,j,input.transactionOutputId));
                        return false;
                    }
                    if (input.unspentTransactionOutput.value != tempOutput.value) {
                        System.out.println(String.format("the %d block %d transaction's input(%s) value is invalid.",i,j,input.transactionOutputId));
                        return false;
                    }
                    tempUTXO.remove(input.transactionOutputId);
                }
                for (TransactionOutput output : currentTX.outputs) {
                    tempUTXO.put(output.id, output);
                }
                if (currentTX.outputs.get(0).to != currentTX.to) {
                    System.out.println(String.format("the %d block %d transaction's receiver is invalid.",i,j));
                    return false;
                }
                if (currentTX.outputs.get(1).to != currentTX.from) {
                    System.out.println(String.format("the %d block %d transaction's change receiver is invalid.",i,j));
                    return false;
                }
            }
        }
        return true;
    }

    public static void addBlock(Block block) {
        block.mineBlock(difficulty);
        blockChain.add(block);
        //printChain(blockChain);
    }

    public static void printChain(List<Block> blockChain) {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Transaction.class, new Transaction.TransactionTypeAdapter());
        System.out.println(builder.create().toJson(blockChain));
    }
}
