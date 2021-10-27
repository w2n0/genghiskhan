package cn.w2n0.genghiskhan.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Aes对称加密
 *
 * @author 无量
 * date 2021-01-12
 */
public class AesUtil {

    private static final String IVSTR = "QBEZGSWDDGJ5addu";

    /**
     * 对称加密
     *
     * @param encodeRules 密钥
     * @param content     明文
     * @return 密钥
     */
    public static String aesEncode(String encodeRules, String content) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(encodeRules.getBytes());
            keygen.init(128, secureRandom);
            SecretKey originalKey = keygen.generateKey();
            byte[] raw = originalKey.getEncoded();
            SecretKey key = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IVSTR.getBytes("UTF-8")));
            byte[] byteEncode = content.getBytes("utf-8");
            byte[] byteAeS = cipher.doFinal(byteEncode);

            String b64str = new sun.misc.BASE64Encoder().encode(byteAeS);
            String aesEncode = new String(b64str);
            return aesEncode;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对称解密
     *
     * @param encodeRules 密钥
     * @param content     密文
     * @return 明文
     */
    public static String aesDecode(String encodeRules, String content) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(encodeRules.getBytes());
            keygen.init(128, secureRandom);
            SecretKey originalKey = keygen.generateKey();
            byte[] raw = originalKey.getEncoded();
            SecretKey key = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IVSTR.getBytes("UTF-8")));
            byte[] byteContent = new sun.misc.BASE64Decoder().decodeBuffer(content);
            byte[] byteDecode = cipher.doFinal(byteContent);
            String aecDecode = new String(byteDecode, "utf-8");
            return aecDecode;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
