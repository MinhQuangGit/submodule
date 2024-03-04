package vn.com.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import vn.com.payment.exception.BusinessErrorCode;
import vn.com.payment.exception.BusinessException;
import vn.com.payment.exception.FieldViolation;
import vn.com.payment.exception.ValidateException;
import vn.com.payment.model.response.Response;
import vn.com.payment.util.ErrorCode;

import java.io.IOException;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(BusinessException.class)
    protected void handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, e.getErrorCode(), request, response);
    }

    @ExceptionHandler(Exception.class)
    protected void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.INTERNAL_SERVER_ERROR, request, response);
    }

    @ExceptionHandler(BindException.class)
    protected void handleBindException(BindException e, HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        var fieldViolations = e.getBindingResult().getAllErrors().stream()
                .map(error -> new FieldViolation(((FieldError) error).getField(), error.getDefaultMessage()))
                .toList();

        handleInvalidParams(e, fieldViolations, request, response);
    }

    @ExceptionHandler(ValidateException.class)
    protected void handleValidateException(ValidateException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleInvalidParams(e, e.getFieldViolations(), request, response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected void handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.INVALID_PARAMETERS, request, response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected void handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var fieldViolations = List.of(new FieldViolation(e.getParameterName(), e.getMessage()));
        handleInvalidParams(e, fieldViolations, request, response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected void handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var fieldViolations = List.of(new FieldViolation(e.getName(), e.getMessage()));
        handleInvalidParams(e, fieldViolations, request, response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.FORBIDDEN, request, response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.UNAUTHORIZED, request, response);
    }

    private void handle(Exception e, BusinessErrorCode errorCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var errorResponse = Response.ofFailed(errorCode, e.getMessage());
        writeResponse(response, errorCode.getHttpStatus(), errorResponse);
    }

    private void handleInvalidParams(Exception e, List<FieldViolation> fieldViolations, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_PARAMETERS, e.getMessage(), fieldViolations);
        writeResponse(response, ErrorCode.INVALID_PARAMETERS.getHttpStatus(), errorResponse);
    }

    private void writeResponse(HttpServletResponse servletResponse, int httpStatus, Response<?> errorResponse) throws IOException {
        servletResponse.setStatus(httpStatus);
        servletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        byte[] body = objectMapper.writeValueAsBytes(errorResponse);
        servletResponse.setContentLength(body.length);
        servletResponse.getOutputStream().write(body);
    }
}
