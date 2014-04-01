package com.banmayun.sdk.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Meta {

    @JsonProperty("id")
    public String id = null;

    @JsonProperty("root_id")
    public String rootId = null;

    @JsonProperty("path")
    public String path = null;

    @JsonProperty("name")
    public String name = null;

    @JsonProperty("is_dir")
    public Boolean isDir = null;

    @JsonProperty("md5")
    public String md5 = null;

    @JsonProperty("size")
    public Size size = null;

    @JsonProperty("version")
    public Long version = null;

    @JsonProperty("thumb_exists")
    public Boolean thumbExists = null;

    @JsonProperty("icon")
    public String icon = null;

    @JsonProperty("comment_count")
    public Integer commentCount = null;

    @JsonProperty("share_count")
    public Integer shareCount = null;

    @JsonProperty("created_at")
    public Time createdAt = null;

    @JsonProperty("created_by")
    public User createdBy = null;

    @JsonProperty("modified_at")
    public Time modifiedAt = null;

    @JsonProperty("modified_by")
    public User modifiedBy = null;

    @JsonProperty("client_modified_at")
    public Time clientModifiedAt = null;

    @JsonProperty("insertable")
    public Boolean insertable = null;

    @JsonProperty("readable")
    public Boolean readable = null;

    @JsonProperty("writable")
    public Boolean writable = null;

    @JsonProperty("deletable")
    public Boolean deletable = null;

    @JsonProperty("permission")
    public Permission permission = null;

    @JsonProperty("contents")
    public List<Meta> contents = null;
}
