package com.pblgllgs.emailnotificationmicroservice.error;
/*
 *
 * @author pblgl
 * Created on 01-07-2024
 *
 */

public class RetryableException extends RuntimeException {

    public RetryableException(String message) {
        super(message);
    }


    public RetryableException(Throwable cause) {
        super(cause);
    }
}
