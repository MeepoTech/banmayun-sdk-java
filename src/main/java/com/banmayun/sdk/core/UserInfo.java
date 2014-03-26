package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(Include.NON_NULL)
public class UserInfo {

    @JsonUnwrapped
    public User user = null;

    @JsonProperty("quota_info")
    public QuotaInfo quotaInfo = null;
}
