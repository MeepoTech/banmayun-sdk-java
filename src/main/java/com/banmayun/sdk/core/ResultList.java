package com.banmayun.sdk.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ResultList<T> {

    @JsonProperty("total")
    public Integer total = null;

    @JsonProperty("offset")
    public Integer offset = null;

    @JsonProperty("entries")
    public List<T> entries = null;
}
