package com.wang.fastfoodapi.Exception;

/**
 * @Auther: wAnG
 * @Date: 2022/1/5 16:13
 * @Description:
 */

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

