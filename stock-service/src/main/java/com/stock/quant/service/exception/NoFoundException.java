package com.stock.quant.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFoundException extends BaseException{
    public NoFoundException() {
        super();
    }

    public NoFoundException(Throwable e) {
        super(e);
    }

    public NoFoundException(String errorMessage) {
        super(errorMessage);
    }

    public NoFoundException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
