package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ErrorResponse {

    @JsonProperty("status")
    public Integer status = null;

    @JsonProperty("code")
    public Integer code = null;

    @JsonProperty("message")
    public String message = null;
}
