package com.pblgllgs.emailnotificationmicroservice.error;
/*
 *
 * @author pblgl
 * Created on 01-07-2024
 *
 */

public class NotRetryableException extends RuntimeException {

    public NotRetryableException(String message) {
        super(message);
    }

    public NotRetryableException(Throwable cause) {
        super(cause);
    }
}
