package com.dong.dcoin;

import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DChainTest {

    public DChain chain;

    @Before
    public void setup() {
        chain = new DChain();
        chain.add("hello world!");
        chain.add("i am the second block.");
        chain.add("i am the third block.");
        chain.toString();
    }

    @Test
    public void isChainValid() {
        System.out.println("isChainValid " + chain.isChainValid());
        chain.blockChain.add(new Block("ddddd", "wow"));
        System.out.println("isChainValid " + chain.isChainValid());
    }
}