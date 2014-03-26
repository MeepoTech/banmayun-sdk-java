package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Trash {

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("root_id")
    public String rootId = null;

    @JsonProperty("file_id")
    public String fileId = null;

    @JsonProperty("created_at")
    public Time createdAt = null;

    @JsonProperty("created_by")
    public User createdBy = null;

    @JsonProperty("meta")
    public Meta meta = null;
}
