package com.banmayun.sdk;

import com.banmayun.sdk.core.ErrorResponse;

public class ClientException extends Exception {

    private static final long serialVersionUID = 1L;

    private ErrorResponse errorResponse = null;

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
