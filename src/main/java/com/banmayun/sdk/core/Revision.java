package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Revision {

    @JsonProperty("version")
    public Long version = null;

    @JsonProperty("md5")
    public String md5 = null;

    @JsonProperty("size")
    public Size size = null;

    @JsonProperty("modified_at")
    public Time modifiedAt = null;

    @JsonProperty("modified_by")
    public User modifiedBy = null;

    @JsonProperty("client_modified_at")
    public Time clientModifiedAt = null;
}
