package com.common.number;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * DES 对称加密算法 ， 52位秘钥
 *
 * @date:2020/2/24 13:19
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DESUtil {

    private static final String KEY_ALGORITHM = "DES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param key     加密密钥
     * @return 返回Base64转码后的加密数据
     */
    private static byte[] encrypt(byte[] content, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
        return cipher.doFinal(content);//通过Base64转码返回
    }

    /**
     * AES 解密操作
     *
     * @param result
     * @param key
     * @return
     */
    private static byte[] decrypt(byte[] result, SecretKeySpec key) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(result);
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) throws NoSuchAlgorithmException {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        //AES 要求密钥长度为 128
        kg.init(56, new SecureRandom(key.getBytes()));
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
    }

}