package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class User {
    @JsonProperty("id")
    public String id = null;

    @JsonProperty("root_id")
    public String rootId = null;

    @JsonProperty("name")
    public String name = null;

    @JsonProperty("email")
    public String email = null;

    @JsonProperty("source")
    public String source = null;

    @JsonProperty("display_name")
    public String displayName = null;

    @JsonProperty("role")
    public EnumElement role = null;

    @JsonProperty("groups_can_own")
    public Integer groupsCanOwn = null;

    @JsonProperty("is_activated")
    public Boolean isActivated = null;

    @JsonProperty("is_blocked")
    public Boolean isBlocked = null;

    @JsonProperty("group_count")
    public Integer groupCount = null;

    @JsonProperty("created_at")
    public Time createdAt = null;

    @JsonProperty("relation")
    public Relation relation = null;

    @JsonProperty("root")
    public Root root = null;
}
