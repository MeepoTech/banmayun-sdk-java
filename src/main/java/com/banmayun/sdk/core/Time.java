package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Time {

    @JsonProperty("rfc1123")
    public String rfc1123 = null;

    @JsonProperty("display_value")
    public String displayValue = null;
}
