package com.banmayun.sdk.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Permission {

    @JsonProperty("insertable_to_owner")
    public Boolean insertableToOwner = null;

    @JsonProperty("readable_to_owner")
    public Boolean readableToOwner = null;

    @JsonProperty("writable_to_owner")
    public Boolean writableToOwner = null;

    @JsonProperty("deletable_to_owner")
    public Boolean deletableToOwner = null;

    @JsonProperty("insertable_to_others")
    public Boolean insertableToOthers = null;

    @JsonProperty("readable_to_others")
    public Boolean readableToOthers = null;

    @JsonProperty("writable_to_others")
    public Boolean writableToOthers = null;

    @JsonProperty("deletable_to_others")
    public Boolean deletableToOthers = null;
}
