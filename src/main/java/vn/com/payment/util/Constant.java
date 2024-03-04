package vn.com.payment.util;

public class Constant {
    public static final String PREFIX_RESPONSE_CODE;
    public static final String SYSTEM_USER = "system";

    static {
        PREFIX_RESPONSE_CODE = System.getProperty("payment.response.prefix-code", "PMH-");
    }

    private Constant() {
        throw new UnsupportedOperationException();
    }
}
