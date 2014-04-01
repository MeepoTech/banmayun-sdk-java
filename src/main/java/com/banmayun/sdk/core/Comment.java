package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Comment {

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("root_id")
    public String rootId = null;

    @JsonProperty("meta_id")
    public String metaId = null;

    @JsonProperty("contents")
    public String contents = null;

    @JsonProperty("created_at")
    public Time createdAt = null;

    @JsonProperty("created_by")
    public User createdBy = null;
}
