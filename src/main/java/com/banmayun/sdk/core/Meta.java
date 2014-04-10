package com.banmayun.sdk.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Meta extends Dumpable {

    public String id;
    public String rootId;
    public String name;
    public String path;
    public String md5;
    public Size size;
    public long version;
    public String icon;
    public boolean isDir;
    public boolean thumbExists;
    public boolean insertable;
    public boolean readable;
    public boolean writable;
    public boolean deletable;
    public int commentCount;
    public int shareCount;
    public Time createdAt;
    public User createdBy;
    public Time modifiedAt;
    public User modifiedBy;
    public Time clientModifiedAt;
    public Permission permission;
    public Meta[] contents;

    public Meta(String id, String rootId, String name, String path, String md5, Size size, long version, String icon,
            boolean isDir, boolean thumbExists, boolean insertable, boolean readable, boolean writable,
            boolean deletable, int commentCount, int shareCount, Time createdAt, User createdBy, Time modifiedAt,
            User modifiedBy, Time clientModifiedAt, Permission permission, Meta[] contents) {
        this.id = id;
        this.rootId = rootId;
        this.name = name;
        this.path = path;
        this.md5 = md5;
        this.size = size;
        this.version = version;
        this.icon = icon;
        this.isDir = isDir;
        this.thumbExists = thumbExists;
        this.insertable = insertable;
        this.readable = readable;
        this.writable = writable;
        this.deletable = deletable;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.clientModifiedAt = clientModifiedAt;
        this.permission = permission;
        this.contents = contents;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("id", id);
        out.field("root_id", rootId);
        out.field("name", name);
        out.field("path", path);
        out.field("md5", md5);
        out.field("size", size);
        out.field("version", version);
        out.field("icon", icon);
        out.field("is_dir", isDir);
        out.field("thumb_exists", thumbExists);
        out.field("insertable", insertable);
        out.field("readable", readable);
        out.field("writable", writable);
        out.field("deletable", deletable);
        out.field("comment_count", commentCount);
        out.field("share_count", shareCount);
        out.field("created_at", createdAt);
        out.field("created_by", createdBy);
        out.field("modified_at", modifiedAt);
        out.field("modified_by", modifiedBy);
        out.field("client_modified_at", clientModifiedAt);
        out.field("permission", permission);
        // out.field("contents", contents);
        // TODO: not ok
    }
    public static JsonReader<Meta> Reader = new JsonReader<Meta>() {

        @Override
        public Meta read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            String rootId = null;
            String name = null;
            String path = null;
            String md5 = null;
            Size size = null;
            long version = -1;
            String icon = null;
            boolean isDir = false;
            boolean thumbExists = false;
            boolean insertable = false;
            boolean readable = false;
            boolean writable = false;
            boolean deletable = false;
            int commentCount = -1;
            int shareCount = -1;
            Time createdAt = null;
            User createdBy = null;
            Time modifiedAt = null;
            User modifiedBy = null;
            Time clientModifiedAt = null;
            Permission permission = null;
            Meta[] contents = null;

            JsonLocation top = JsonReader.expectObjectStart(parser);
            while (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                try {
                    int fi = FM.get(fieldName);
                    switch (fi) {
                    case -1:
                        JsonReader.skipValue(parser);
                        break;
                    case FM_id:
                        id = JsonReader.StringReader.readField(parser, fieldName, id);
                        break;
                    case FM_root_id:
                        rootId = JsonReader.StringReader.readField(parser, fieldName, rootId);
                        break;
                    case FM_name:
                        name = JsonReader.StringReader.readField(parser, fieldName, name);
                        break;
                    case FM_path:
                        path = JsonReader.StringReader.readField(parser, fieldName, path);
                        break;
                    case FM_md5:
                        md5 = JsonReader.StringReader.readField(parser, fieldName, md5);
                        break;
                    case FM_size:
                        size = Size.Reader.readField(parser, fieldName, size);
                        break;
                    case FM_version:
                        version = JsonReader.readUnsignedLongField(parser, fieldName, version);
                        break;
                    case FM_icon:
                        icon = JsonReader.StringReader.readField(parser, fieldName, icon);
                        break;
                    case FM_is_dir:
                        isDir = JsonReader.readBoolean(parser);
                        break;
                    case FM_thumb_exists:
                        thumbExists = JsonReader.readBoolean(parser);
                        break;
                    case FM_insertable:
                        insertable = JsonReader.readBoolean(parser);
                        break;
                    case FM_readable:
                        readable = JsonReader.readBoolean(parser);
                        break;
                    case FM_writable:
                        writable = JsonReader.readBoolean(parser);
                        break;
                    case FM_deletable:
                        deletable = JsonReader.readBoolean(parser);
                        break;
                    case FM_comment_count:
                        commentCount = (int) JsonReader.readUnsignedLongField(parser, fieldName, commentCount);
                        break;
                    case FM_share_count:
                        shareCount = (int) JsonReader.readUnsignedLongField(parser, fieldName, shareCount);
                        break;
                    case FM_created_at:
                        createdAt = Time.Reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_created_by:
                        createdBy = User.Reader.readField(parser, fieldName, createdBy);
                        break;
                    case FM_modified_at:
                        modifiedAt = Time.Reader.readField(parser, fieldName, modifiedAt);
                        break;
                    case FM_modified_by:
                        modifiedBy = User.Reader.readField(parser, fieldName, modifiedBy);
                        break;
                    case FM_client_modified_at:
                        clientModifiedAt = Time.Reader.readField(parser, fieldName, clientModifiedAt);
                        break;
                    case FM_permission:
                        permission = Permission.Reader.readField(parser, fieldName, permission);
                        break;
                    case FM_contents:
                        JsonReader.expectArrayStart(parser);
                        ArrayList<Meta> metaList = new ArrayList<>();
                        while (!JsonReader.isArrayEnd(parser)) {
                            Meta meta;
                            meta = Meta.Reader.read(parser);
                            metaList.add(meta);
                        }
                        parser.nextToken();
                        if (metaList.size() > 0) {
                            contents = new Meta[metaList.size()];
                            for (int i = 0; i < metaList.size(); i++) {
                                contents[i] = metaList.get(i);
                            }
                        } else {
                            contents = null;
                        }
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            // TODO: add some checks?
            return new Meta(id, rootId, name, path, md5, size, version, icon, isDir, thumbExists, insertable, readable,
                    writable, deletable, commentCount, shareCount, createdAt, createdBy, modifiedAt, modifiedBy,
                    clientModifiedAt, permission, contents);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_root_id = 1;
    private static final int FM_name = 2;
    private static final int FM_path = 3;
    private static final int FM_md5 = 4;
    private static final int FM_size = 5;
    private static final int FM_version = 6;
    private static final int FM_icon = 7;
    private static final int FM_is_dir = 8;
    private static final int FM_thumb_exists = 9;
    private static final int FM_insertable = 10;
    private static final int FM_readable = 11;
    private static final int FM_writable = 12;
    private static final int FM_deletable = 13;
    private static final int FM_comment_count = 14;
    private static final int FM_share_count = 15;
    private static final int FM_created_at = 16;
    private static final int FM_created_by = 17;
    private static final int FM_modified_at = 18;
    private static final int FM_modified_by = 19;
    private static final int FM_client_modified_at = 20;
    private static final int FM_permission = 21;
    private static final int FM_contents = 22;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("root_id", FM_root_id);
        b.add("name", FM_name);
        b.add("path", FM_path);
        b.add("md5", FM_md5);
        b.add("size", FM_size);
        b.add("version", FM_version);
        b.add("icon", FM_icon);
        b.add("is_dir", FM_is_dir);
        b.add("thumb_exists", FM_thumb_exists);
        b.add("insertable", FM_insertable);
        b.add("readable", FM_readable);
        b.add("writable", FM_writable);
        b.add("deletable", FM_deletable);
        b.add("comment_count", FM_comment_count);
        b.add("share_count", FM_share_count);
        b.add("created_at", FM_created_at);
        b.add("created_by", FM_created_by);
        b.add("modified_at", FM_modified_at);
        b.add("modified_by", FM_modified_by);
        b.add("client_modified_at", FM_client_modified_at);
        b.add("permission", FM_permission);
        b.add("contents", FM_contents);

        FM = b.build();
    }
}