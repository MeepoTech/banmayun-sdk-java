package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ChunkedUpload {

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("offset")
    public Long offset = null;

    @JsonProperty("size")
    public Size size = null;

    @JsonProperty("expires_at")
    public Time expiresAt = null;

    @JsonProperty("created_at")
    public Time createdAt = null;
}
