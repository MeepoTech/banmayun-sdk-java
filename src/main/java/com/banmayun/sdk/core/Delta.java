package com.banmayun.sdk.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Delta {

    @JsonProperty("cursor_id")
    public String cursorId = null;

    @JsonProperty("reset")
    public Boolean reset = null;

    @JsonProperty("entries")
    public List<Entry> entries = null;

    @JsonInclude(Include.NON_NULL)
    public static class Entry {
        @JsonProperty("path")
        public String path = null;

        @JsonProperty("meta")
        public Meta meta = null;
    }
}
