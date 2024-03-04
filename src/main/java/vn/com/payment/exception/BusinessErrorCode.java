package vn.com.payment.exception;

import lombok.Value;

@Value
public class BusinessErrorCode {
    int code;
    String message;
    int httpStatus;
}
