package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.danielpgbrasil.orderprocessing.domain.shared.AbstractNotFoundException;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.AppErrorResponse;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<AppErrorResponse> handlePublicException(
            IllegalStateException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.badRequest().body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AppErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.badRequest().body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(AbstractNotFoundException.class)
    public ResponseEntity<AppErrorResponse> handleAbstractNotFoundException(
            AbstractNotFoundException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppErrorResponse> handleException(Exception ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.internalServerError().body(new AppErrorResponse("Erro inesperado no servidor."));
    }

    private void logError(Exception e) {
        LOGGER.error(logMessage(e), e);
    }

    private String logMessage(Exception e) {
        var message = e.getMessage();
        var className = e.getClass().getSimpleName();
        return StringUtils.isBlank(message) ? className : className + ": " + message;
    }
}