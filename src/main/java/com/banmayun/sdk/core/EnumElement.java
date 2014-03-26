package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class EnumElement {
    @JsonProperty("name")
    public String name = null;

    @JsonProperty("description")
    public String description = null;
}
