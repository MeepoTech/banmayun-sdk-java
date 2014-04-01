package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Root {

    public enum RootType {
        USER, GROUP;
    }

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("type")
    public String type = null;

    @JsonProperty("used")
    public Size used = null;

    @JsonProperty("quota")
    public Size quota = null;

    @JsonProperty("default_permission")
    public Permission defaultPermission = null;

    @JsonProperty("file_count")
    public Integer fileCount = null;

    @JsonProperty("byte_count")
    public Long byteCount = null;
}
