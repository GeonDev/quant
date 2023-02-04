package com.stock.quant.service.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public BaseException() {
        super();
    }

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(Throwable e) {
        super(e);
    }

    public BaseException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public abstract HttpStatus getHttpStatus();


}