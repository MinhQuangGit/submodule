package vn.com.payment.util;

import vn.com.payment.exception.BusinessErrorCode;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;

//@log24j
public class ErrorCode {
    public static final BusinessErrorCode CONFIG_NOT_FOUND =
            new BusinessErrorCode(4003, "config not found", 404);
    public static final BusinessErrorCode INTERNAL_SERVER_ERROR =
            new BusinessErrorCode(5001, "internal server error", 500);
    public static final BusinessErrorCode INVALID_PARAMETERS =
            new BusinessErrorCode(4000, "invalid parameters", 400);
    public static final BusinessErrorCode UNAUTHORIZED =
            new BusinessErrorCode(4001, "You need to login to to access this resource", 401);
    public static final BusinessErrorCode FORBIDDEN =
            new BusinessErrorCode(4002, "You don't have permission to to access this resource", 403);

    static {
        var codes = new HashSet<Integer>();
        var duplications = Arrays.stream(ErrorCode.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType().equals(BusinessErrorCode.class))
                .map(f -> {
                    try {
                        return ((BusinessErrorCode) f.get(null)).getCode();
                    } catch (IllegalAccessException e) {
//                        log.error("Can't load error code into map", e);
                        throw new RuntimeException(e);
                    }
                })
                .filter(c -> !codes.add(c))
                .toList();
        if (!duplications.isEmpty()) {
            throw new RuntimeException("Found error code duplication: " + duplications);
        }
    }
}