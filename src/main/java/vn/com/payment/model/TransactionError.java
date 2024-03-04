package vn.com.payment.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import vn.com.payment.exception.BusinessErrorCode;
import vn.com.payment.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CompiledJson
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TransactionError {
    private String code;
    private String message;
    private List<TransactionError> supperessedErrors;

    public TransactionError(BusinessErrorCode code, String message){
        this.code = Constant.PREFIX_RESPONSE_CODE + code.getCode();
        this.message = message;
    }
    public TransactionError(BusinessErrorCode code){
        this(code,code.getMessage());
    }
    public TransactionError(BusinessErrorCode code , Throwable cause){
        this(code, code.getMessage() + "due: " + cause);
    }
    public TransactionError addSuppressed(TransactionError error){
        if(error == this){
            throw new IllegalArgumentException("Self-suppression not permitted");
        }
        Objects.requireNonNull(error, "Cannot suppress a null error");
        if(supperessedErrors == null){
            supperessedErrors = new ArrayList<>(2);
        }
        supperessedErrors.add(error);
        return this;
    }
}
