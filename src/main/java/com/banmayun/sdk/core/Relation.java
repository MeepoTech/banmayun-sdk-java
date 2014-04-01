package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Relation {

    public enum RelationRole {
        OWNER, ADMIN, MEMBER;
    }

    @JsonProperty("role")
    public EnumElement role = null;

    @JsonProperty("is_activated")
    public Boolean isActivated = null;

    @JsonProperty("is_blocked")
    public Boolean isBlocked = null;

    @JsonProperty("remark")
    public String remark = null;

    @JsonProperty("created_at")
    public Time createdAt = null;
}
