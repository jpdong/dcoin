package com.dong.dcoin;

import org.junit.Test;

import static org.junit.Assert.*;

public class BlockTest {

    @Test
    public void testBlock() {
        Block genesisBlock = new Block("0","hello world");
        System.out.println(genesisBlock.hash);
        System.out.println(genesisBlock.hash.length());
    }

}