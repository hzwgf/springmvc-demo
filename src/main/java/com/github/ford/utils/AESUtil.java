package com.github.ford.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

/**
 * 生成秘钥对，加解密
 *
 * @author wgf
 * @version $$Id: AESUtil, v 0.1 2016年07月25日 下午5:12 wgf Exp $$
 */
public class AESUtil {
    private static final String AES_ALG         = "AES";
    private static final String AES_CBC_PCK_ALG = "AES/CBC/PKCS5Padding";
    private static final byte[] AES_IV          = initIv("AES/CBC/PKCS5Padding");

    public AESUtil() {
    }


    /**
     * 生成aes秘钥
     *
     * @return
     */
    public static byte[] genKey() {
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(AES_ALG);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        kg.init(128);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 生成aes秘钥
     *
     * @return
     */
    public static String genStringKey() {
        byte[] bytes=genKey();
        return Base64.encodeBase64String(bytes);
    }

    public static String encryptContent(String content, String encryptType, String encryptKey,
                                        String charset) throws Exception {
        if ("AES".equals(encryptType)) {
            return aesEncrypt(content, encryptKey, charset);
        } else {
            throw new Exception("当前不支持该算法类型：encrypeType=" + encryptType);
        }
    }

    public static String decryptContent(String content, String encryptType, String encryptKey,
                                        String charset) throws Exception {
        if ("AES".equals(encryptType)) {
            return aesDecrypt(content, encryptKey, charset);
        } else {
            throw new Exception("当前不支持该算法类型：encrypeType=" + encryptType);
        }
    }

    private static String aesEncrypt(String content, String aesKey, String charset)
                                                                                   throws Exception {
        try {
            Cipher e = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(AES_IV);
            e.init(1, new SecretKeySpec(Base64.decodeBase64(aesKey.getBytes()), "AES"), iv);
            byte[] encryptBytes = e.doFinal(content.getBytes(charset));
            return new String(Base64.encodeBase64(encryptBytes));
        } catch (Exception var6) {
            throw new Exception("AES加密失败：Aescontent = " + content + "; charset = " + charset, var6);
        }
    }

    private static String aesDecrypt(String content, String key, String charset) throws Exception {
        try {
            Cipher e = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(initIv("AES/CBC/PKCS5Padding"));
            e.init(2, new SecretKeySpec(Base64.decodeBase64(key.getBytes()), "AES"), iv);
            byte[] cleanBytes = e.doFinal(Base64.decodeBase64(content.getBytes()));
            return new String(cleanBytes, charset);
        } catch (Exception var6) {
            throw new Exception("AES解密失败：Aescontent = " + content + "; charset = " + charset, var6);
        }
    }

    private static byte[] initIv(String fullAlg) {
        byte[] iv;
        int i;
        try {
            Cipher e = Cipher.getInstance(fullAlg);
            int var6 = e.getBlockSize();
            iv = new byte[var6];

            for (i = 0; i < var6; ++i) {
                iv[i] = 0;
            }

            return iv;
        } catch (Exception var5) {
            byte blockSize = 16;
            iv = new byte[blockSize];

            for (i = 0; i < blockSize; ++i) {
                iv[i] = 0;
            }

            return iv;
        }
    }
}
