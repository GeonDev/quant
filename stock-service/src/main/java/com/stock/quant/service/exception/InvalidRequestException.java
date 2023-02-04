package com.stock.quant.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends BaseException{

    private static final long serialVersionUID = 1L;

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(Throwable e) {
        super(e);
    }

    public InvalidRequestException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidRequestException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
