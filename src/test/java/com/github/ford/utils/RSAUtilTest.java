/**
 * qccr.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.github.ford.utils;

import com.sun.tools.javac.util.Pair;
import org.junit.Test;

/**
 * @author wgf
 * @version $$Id: RSAUtilTest, v 0.1 2016年07月25日 下午5:12 wgf Exp $$
 */
public class RSAUtilTest {


    public static void main(String[] args) throws Exception {
        Pair<String,String> pair=RSAUtil.genStringKeyPair();
        System.out.println("public key:"+pair.fst+"\nprivate key:"+pair.snd);
        System.out.println("aes key:"+AESUtil.genKey());

    }
}
