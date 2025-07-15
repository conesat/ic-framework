package cn.icframework.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES 加密工具类，提供字符串和字节数组的 AES 加解密方法。
 * <p>
 * 支持密钥长度限制和不限制两种方式。所有方法均为静态方法。
 * </p>
 *
 * @author hzl
 */
@Slf4j
public class AESUtils {
    /**
     * 默认构造方法
     */
    public AESUtils() {}
    /**
     * 字符编码
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 算法名称
     */
    private static final String ALGORITHM = "AES";//加密算法
    /**
     * 算法/工作模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//算法/工作模式/填充方式

    /**
     * 使用 AES 算法加密明文字符串。
     * @param plaintext 明文字符串
     * @param secureKey 16位长度的密钥
     * @return 加密后并经过 Base64 编码的字符串
     * @throws Exception 加密异常
     */
    public static String encrypt(String plaintext, String secureKey) throws Exception{
        SecretKeySpec sks = getSecretKeySpec(secureKey);
        Cipher encryptCipher = getCipher(Cipher.ENCRYPT_MODE, sks);
        byte[] result = encryptCipher.doFinal(plaintext.getBytes(ENCODING));
        return  Base64.encodeBase64String(result);
    }

    /**
     * 使用 AES 算法加密字节数组。
     * @param bytes 原始字节数组
     * @param secureKey 16位长度的密钥
     * @return 加密后并经过 Base64 编码的字符串
     * @throws Exception 加密异常
     */
    public static String encryptBytes(byte[] bytes, String secureKey) throws Exception{
        SecretKeySpec sks = getSecretKeySpec(secureKey);
        Cipher encryptCipher = getCipher(Cipher.ENCRYPT_MODE, sks);
        byte[] result = encryptCipher.doFinal(bytes);
        return  Base64.encodeBase64String(result);
    }

    /**
     * 使用 AES 算法解密密文字符串。
     * @param ciphertext 经过 Base64 编码的密文字符串
     * @param secureKey 16位长度的密钥
     * @return 解密后的明文字符串
     * @throws Exception 解密异常
     */
    public static String decrypt(String ciphertext, String secureKey) throws Exception {
        SecretKeySpec sks = getSecretKeySpec(secureKey);
        Cipher decryptCiphe = getCipher(Cipher.DECRYPT_MODE, sks);//initDecryptCipher(secureKey);
        byte[] result =  decryptCiphe.doFinal(Base64.decodeBase64(ciphertext));
        return new String(result, ENCODING);
    }

    /**
     * 获取 Cipher 实例并初始化。
     * @param cipherMode 加密或解密模式
     * @param sks 密钥规格
     * @return 初始化后的 Cipher 对象
     * @throws Exception 初始化异常
     */
    private static Cipher getCipher(int cipherMode, SecretKeySpec sks) throws Exception{
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(cipherMode, sks);
        return cipher;
    }

    /**
     * 根据 16 位密钥字符串生成 SecretKeySpec。
     * @param secureKey 16位密钥字符串
     * @return SecretKeySpec 对象
     * @throws Exception 密钥异常
     */
    private static SecretKeySpec getSecretKeySpec(String secureKey) throws Exception{
        if(secureKey == null || secureKey.trim().isEmpty() || secureKey.length() != 16){
            throw new Exception("密钥不能为空或密钥长度不对");
        }
        byte[] raw = secureKey.getBytes(ENCODING);
        return new SecretKeySpec(raw, ALGORITHM);
    }

    /**
     * 使用 AES 算法加密明文字符串（密钥长度不限制）。
     * @param plaintext 明文字符串
     * @param secureKey 密钥字符串
     * @return 加密后并经过 Hex 编码的字符串
     * @throws Exception 加密异常
     */
    public static String encryptNotLimit(String plaintext, String secureKey) throws Exception{
        SecretKeySpec sks = getSecretKeySpecByRandomSeed(secureKey);
        Cipher encryptCipher = getCipher(Cipher.ENCRYPT_MODE, sks);
        byte[] result = encryptCipher.doFinal(plaintext.getBytes(ENCODING));
        return Hex.encodeHexString(result);
    }

    /**
     * 使用 AES 算法解密密文字符串（密钥长度不限制）。
     * @param ciphertext 经过 Hex 编码的密文字符串
     * @param secureKey 密钥字符串
     * @return 解密后的明文字符串
     * @throws Exception 解密异常
     */
    public static String decryptNotLimit(String ciphertext, String secureKey) throws Exception {
        SecretKeySpec sks = getSecretKeySpecByRandomSeed(secureKey);
        Cipher decryptCiphe = getCipher(Cipher.DECRYPT_MODE, sks);
        byte[] result =  decryptCiphe.doFinal(Hex.decodeHex(ciphertext.toCharArray()));
        return new String(result, ENCODING);
    }

    /**
     * 根据密钥字符串和随机种子生成 SecretKeySpec。
     * @param secureKey 密钥字符串
     * @return SecretKeySpec 对象，生成失败时返回 null
     */
    private static SecretKeySpec getSecretKeySpecByRandomSeed(String secureKey){
        SecretKeySpec sks = null;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            //安全随机数生成器
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");//使用默认的SHA1PRNG算法
            secureRandom.setSeed(secureKey.getBytes(ENCODING));
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] secretKeyEncoded = secretKey.getEncoded();
            sks = new SecretKeySpec(secretKeyEncoded, ALGORITHM);
        } catch (Exception e) {
            log.error("",e);
        }
        return sks;
    }

}
