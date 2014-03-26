package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GroupInfo {

    @JsonUnwrapped
    public Group group = null;

    @JsonProperty("quota_info")
    public QuotaInfo quotaInfo = null;
}
