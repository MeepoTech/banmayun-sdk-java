package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class QuotaInfo {

    @JsonProperty("used")
    public Size used = null;

    @JsonProperty("quota")
    public Size quota = null;
}
