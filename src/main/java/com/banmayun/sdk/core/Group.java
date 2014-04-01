package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Group {

    public enum GroupType {
        PRIVATE, PROTECTED, PUBLIC, SYSTEM_PUBLIC
    }

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("root_id")
    public String rootId = null;

    @JsonProperty("name")
    public String name = null;

    @JsonProperty("source")
    public String source = null;

    @JsonProperty("type")
    public EnumElement type = null;

    @JsonProperty("intro")
    public String intro = null;

    @JsonProperty("tags")
    public String tags = null;

    @JsonProperty("announce")
    public String announce = null;

    @JsonProperty("is_visible")
    public Boolean isVisible = null;

    @JsonProperty("is_activated")
    public Boolean isActivated = null;

    @JsonProperty("is_blocked")
    public Boolean isBlocked = null;

    @JsonProperty("user_count")
    public Integer userCount = null;

    @JsonProperty("created_at")
    public Time createdAt = null;

    @JsonProperty("created_by")
    public User createdBy = null;

    @JsonProperty("relation")
    public Relation relation = null;

    @JsonProperty("root")
    public Root root = null;
}
