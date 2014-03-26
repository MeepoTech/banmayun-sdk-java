package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Size {

    @JsonProperty("bytes")
    public Long bytes = null;

    @JsonProperty("display_value")
    public String displayValue = null;
}
