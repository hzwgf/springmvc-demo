package com.github.ford.utils;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.crypto.Cipher;

/**
 *
 * 生成秘钥对，加解密
 *
 * @author wgf
 * @version $$Id: RSAUtil, v 0.1 2016年07月25日 下午5:12 wgf Exp $$
 */
public class RSAUtil {

    private static final String RSA="RSA";
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    public RSAUtil() {
    }

    /**
     *
     * 生成rsa字符串秘钥对
     *
     * @return
     * @throws Exception
     */
    public static Pair<String,String> genStringKeyPair() throws Exception {
        KeyPair keyPair = genKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
        String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
        return new Pair(publicKeyString,privateKeyString);
    }

    /**
     * 生成rsa秘钥对
     *
     * @return
     * @throws Exception
     */
    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = null;
        keyPairGen = KeyPairGenerator.getInstance(RSA);
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }

    public static String rsaSign(String content, String privateKey, String charset, String signType)
                                                                                                    throws Exception {
        if ("RSA".equals(signType)) {
            return rsaSign(content, privateKey, charset);
        } else if ("RSA2".equals(signType)) {
            return rsa256Sign(content, privateKey, charset);
        } else {
            throw new Exception("Sign Type is Not Support : signType=" + signType);
        }
    }

    public static String rsa256Sign(String content, String privateKey, String charset)
                                                                                      throws Exception {
        try {
            PrivateKey e = getPrivateKeyFromPKCS8("RSA",
                new ByteArrayInputStream(privateKey.getBytes()));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(e);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (Exception var6) {
            throw new Exception("RSAcontent = " + content + "; charset = " + charset, var6);
        }
    }

    public static String rsaSign(String content, String privateKey, String charset)
                                                                                   throws Exception {
        try {
            PrivateKey e = getPrivateKeyFromPKCS8("RSA",
                new ByteArrayInputStream(privateKey.getBytes()));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(e);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (InvalidKeySpecException var6) {
            throw new Exception("RSA私钥格式不正确，请检查是否正确配置了PKCS8格式的私钥", var6);
        } catch (Exception var7) {
            throw new Exception("RSAcontent = " + content + "; charset = " + charset, var7);
        }
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins)
                                                                                      throws Exception {
        if (ins != null && !StringUtils.isEmpty(algorithm)) {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            byte[] encodedKey = StreamUtil.readText(ins).getBytes();
            encodedKey = Base64.decodeBase64(encodedKey);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } else {
            return null;
        }
    }

    public static String getSignCheckContentV1(Map<String, String> params) {
        if (params == null) {
            return null;
        } else {
            params.remove("sign");
            params.remove("sign_type");
            StringBuffer content = new StringBuffer();
            ArrayList keys = new ArrayList(params.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size(); ++i) {
                String key = (String) keys.get(i);
                String value = (String) params.get(key);
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }

            return content.toString();
        }
    }

    public static String getSignCheckContentV2(Map<String, String> params) {
        if (params == null) {
            return null;
        } else {
            params.remove("sign");
            StringBuffer content = new StringBuffer();
            ArrayList keys = new ArrayList(params.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size(); ++i) {
                String key = (String) keys.get(i);
                String value = (String) params.get(key);
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }

            return content.toString();
        }
    }

    public static boolean rsaCheckV1(Map<String, String> params, String publicKey, String charset)
                                                                                                  throws Exception {
        String sign = (String) params.get("sign");
        String content = getSignCheckContentV1(params);
        return rsaCheckContent(content, sign, publicKey, charset);
    }

    public static boolean rsaCheckV2(Map<String, String> params, String publicKey, String charset)
                                                                                                  throws Exception {
        String sign = (String) params.get("sign");
        String content = getSignCheckContentV2(params);
        return rsaCheckContent(content, sign, publicKey, charset);
    }

    public static boolean rsaCheck(String content, String sign, String publicKey, String charset,
                                   String signType) throws Exception {
        if ("RSA".equals(signType)) {
            return rsaCheckContent(content, sign, publicKey, charset);
        } else if ("RSA2".equals(signType)) {
            return rsa256CheckContent(content, sign, publicKey, charset);
        } else {
            throw new Exception("Sign Type is Not Support : signType=" + signType);
        }
    }

    public static boolean rsa256CheckContent(String content, String sign, String publicKey,
                                             String charset) throws Exception {
        try {
            PublicKey e = getPublicKeyFromX509("RSA",
                new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(e);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception var6) {
            throw new Exception("RSAcontent = " + content + ",sign=" + sign + ",charset = "
                                + charset, var6);
        }
    }

    public static boolean rsaCheckContent(String content, String sign, String publicKey,
                                          String charset) throws Exception {
        try {
            PublicKey e = getPublicKeyFromX509("RSA",
                new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(e);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception var6) {
            throw new Exception("RSAcontent = " + content + ",sign=" + sign + ",charset = "
                                + charset, var6);
        }
    }

    public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins)
                                                                                   throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    public static String checkSignAndDecrypt(Map<String, String> params, String alipayPublicKey,
                                             String cusPrivateKey, boolean isCheckSign,
                                             boolean isDecrypt) throws Exception {
        String charset = (String) params.get("charset");
        String bizContent = (String) params.get("biz_content");
        if (isCheckSign && !rsaCheckV2(params, alipayPublicKey, charset)) {
            throw new Exception("rsaCheck failure:rsaParams=" + params);
        } else {
            return isDecrypt ? rsaDecrypt(bizContent, cusPrivateKey, charset) : bizContent;
        }
    }

    public static String encryptAndSign(String bizContent, String alipayPublicKey,
                                        String cusPrivateKey, String charset, boolean isEncrypt,
                                        boolean isSign) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(charset)) {
            charset = "GBK";
        }

        sb.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>");
        String sign;
        if (isEncrypt) {
            sb.append("<alipay>");
            sign = rsaEncrypt(bizContent, alipayPublicKey, charset);
            sb.append("<response>" + sign + "</response>");
            sb.append("<encryption_type>RSA</encryption_type>");
            if (isSign) {
                String sign1 = rsaSign(sign, cusPrivateKey, charset);
                sb.append("<sign>" + sign1 + "</sign>");
                sb.append("<sign_type>RSA</sign_type>");
            }

            sb.append("</alipay>");
        } else if (isSign) {
            sb.append("<alipay>");
            sb.append("<response>" + bizContent + "</response>");
            sign = rsaSign(bizContent, cusPrivateKey, charset);
            sb.append("<sign>" + sign + "</sign>");
            sb.append("<sign_type>RSA</sign_type>");
            sb.append("</alipay>");
        } else {
            sb.append(bizContent);
        }

        return sb.toString();
    }

    public static String rsaEncrypt(String content, String publicKey, String charset)
                                                                                     throws Exception {
        try {
            PublicKey e = getPublicKeyFromX509("RSA",
                new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(1, e);
            byte[] data = StringUtils.isEmpty(charset) ? content.getBytes() : content
                .getBytes(charset);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;

            for (int i = 0; inputLen - offSet > 0; offSet = i * 117) {
                byte[] cache;
                if (inputLen - offSet > 117) {
                    cache = cipher.doFinal(data, offSet, 117);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }

                out.write(cache, 0, cache.length);
                ++i;
            }

            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();
            return StringUtils.isEmpty(charset) ? new String(encryptedData) : new String(
                encryptedData, charset);
        } catch (Exception var12) {
            throw new Exception("EncryptContent = " + content + ",charset = " + charset, var12);
        }
    }

    public static String rsaDecrypt(String content, String privateKey, String charset)
                                                                                      throws Exception {
        try {
            PrivateKey e = getPrivateKeyFromPKCS8("RSA",
                new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, e);
            byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64.decodeBase64(content
                .getBytes()) : Base64.decodeBase64(content.getBytes(charset));
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;

            for (int i = 0; inputLen - offSet > 0; offSet = i * 128) {
                byte[] cache;
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(encryptedData, offSet, 128);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }

                out.write(cache, 0, cache.length);
                ++i;
            }

            byte[] decryptedData = out.toByteArray();
            out.close();
            return StringUtils.isEmpty(charset) ? new String(decryptedData) : new String(
                decryptedData, charset);
        } catch (Exception var12) {
            throw new Exception("EncodeContent = " + content + ",charset = " + charset, var12);
        }
    }
}
