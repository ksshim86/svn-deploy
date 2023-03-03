package com.ks.sd.sys.advice;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.ErrorDetails;
import com.ks.sd.errors.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ErrorDetails> handleSvnException(BusinessException ex, WebRequest request) {
        log.error("SVNException", ex);

        return new ResponseEntity<>(
            new ErrorDetails(
                ex.getMessage(),
                request.getDescription(false)),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<ErrorDetails> handleSqlException(SQLException ex, WebRequest request) {
        log.error("SQLException", ex);

        return new ResponseEntity<>(
            new ErrorDetails(
                ErrorCode.SVR_SQL_ERROR.getMessage(),
                request.getDescription(false)),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException ex, WebRequest request) {
        log.error("HttpMessageNotReadableException", ex);

        return new ResponseEntity<>(
            new ErrorDetails(
                ErrorCode.HTTP_REQUEST_FAILED.getMessage(),
                request.getDescription(false)),
            HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDetails> handleAllException(Exception ex, WebRequest request) {
        log.error("Exception", ex);

        return new ResponseEntity<>(
            new ErrorDetails(
                ErrorCode.SVR_CMM_ERROR.getMessage(),
                request.getDescription(false)),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
