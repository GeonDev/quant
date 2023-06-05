package com.quant.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException{
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(Throwable e) {
        super(e);
    }

    public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }

    public ForbiddenException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
