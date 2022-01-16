package com.wang.accountcenter.Util;

import org.springframework.lang.NonNull;

import java.io.*;

/**
 * @Auther: wAnG
 * @Date: 2021/12/20 00:52
 * @Description: JDK序列化方法，之前为了序列化lambda。结果序列化不了。总是有地方用的上
 */

public class JDKSerializedUtil {

    public static byte[] serialized(Object o){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(outputStream);
            stream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
                assert stream != null;
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }

    @SuppressWarnings("all")
    public static <T> T parse(@NonNull byte[] bytes, Class<T> clazz) {
        T t = null;
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            t = (T)objectInputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }


}
