package com.wang.authcenter.security.extension;

import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

/**
 * @Auther: wAnG
 * @Date: 2022/4/7 18:46
 * @Description:
 */

public class KeyUtil {

    public static void main(String[] args) {
        getUserInfo("mutVdAaGQWyG875GpqpQ45bz4ey/7CWma4YUbIwXtLvt+mfaXG5p8+GQ42IGZ6/KYkTJ25nuylCNGQp5zJb3mjCEBmb3bCBsk7LmNcd7/JALochLNV/IMcDb+N4Z8yDRXS9WAr347hnDHGGohKo8HCcuYpf4CSQAa5yTwu8IfDgzJbEPKNrsUOVgurfVApv8uxQ53BVRn0tu2EQ9QSI7auDwOCTmZqLqZs5TDqXMHose8cAenLzEP0x5VHYZbmQgjm8GxDQ+O3vIAxj5yk+h+0405aic35jRC6KZ6/YPBTpW5U8DMJklpeu+IjBHTJKf52zEDTKEp+NZPUonmUL1vMiikSjorosqs2jrgrEgkIZe3bsdY7OtPoqAe4Lv9zv3rLixfpg9eRD2H9wQMXjh4w==",
                "y/Q2b2Hg8XkikN4OuJodrw=="
                ,"ke51mufBTJIRe5fAduI2mg==");
    }

    public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        byte[] ivByte = Base64.decode(iv);
            try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, StandardCharsets.UTF_8);
                return null;
            }
        } catch (NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidParameterSpecException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
