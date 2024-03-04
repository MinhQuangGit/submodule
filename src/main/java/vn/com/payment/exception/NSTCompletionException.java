package vn.com.payment.exception;


import java.util.concurrent.CompletionException;

public class NSTCompletionException extends CompletionException {
public NSTCompletionException(String message, Throwable cause) {super(message, cause);};
public NSTCompletionException(Throwable cause) {super(cause);};
    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
