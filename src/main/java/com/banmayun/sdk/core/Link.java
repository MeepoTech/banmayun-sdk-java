package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Link {

    public enum LinkDevice {
        PC_WINDOWS, PC_MACOSX, PC_LINUX, PHONE_IOS, PHONE_ANDROID, PAD_IOS, PAD_ANDROID, WEB, UNKNOWN
    }

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("user_id")
    public String userId = null;

    @JsonProperty("name")
    public String name = null;

    @JsonProperty("device")
    public String device = null;

    @JsonProperty("token")
    public String token = null;

    @JsonProperty("expires_at")
    public Time expiresAt = null;

    @JsonProperty("created_at")
    public Time createdAt = null;

    @JsonProperty("is_current")
    public Boolean isCurrent = null;
}
