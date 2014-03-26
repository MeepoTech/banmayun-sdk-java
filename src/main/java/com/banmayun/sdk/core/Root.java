package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {
    @JsonProperty("id")
    public String id = null;

    @JsonProperty("root_type")
    public EnumElement type = null;

    @JsonProperty("used")
    public Size used = null;

    @JsonProperty("quota")
    public Size quota = null;

    @JsonProperty("default_permission")
    public Permission defaultPermission = null;

    @JsonProperty("file_count")
    public Integer fileCount = null;

}
