package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class User extends Dumpable {

    public String id = null;
    public String rootId = null;
    public String name = null;
    public String email = null;
    public String source = null;
    public String displayName = null;
    public EnumElement role = null;
    public Integer groupsCanOwn = null;
    public Boolean isActivated = null;
    public Boolean isBlocked = null;
    public Integer groupCount = null;
    public Time createdAt = null;
    public Relation relation = null;
    public Root root = null;

    public User() {
    }

    public User(String id, String rootId, String name, String email, String source, String displayName,
            EnumElement role, Integer groupsCanOwn, Boolean isActivated, Boolean isBlocked, Integer groupCount,
            Time createdAt, Relation relation, Root root) {
        this.id = id;
        this.rootId = rootId;
        this.name = name;
        this.email = email;
        this.source = source;
        this.displayName = displayName;
        this.role = role;
        this.groupsCanOwn = groupsCanOwn;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
        this.groupCount = groupCount;
        this.createdAt = createdAt;
        this.relation = relation;
        this.root = root;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("id", this.id);
        out.field("root_id", this.rootId);
        out.field("name", this.name);
        out.field("email", this.email);
        out.field("source", this.source);
        out.field("display_name", this.displayName);
        out.field("role", this.role);
        out.field("groups_can_own", this.groupsCanOwn);
        out.field("is_activated", this.isActivated);
        out.field("is_blocked", this.isBlocked);
        out.field("group_count", this.groupCount);
        out.field("created_at", this.createdAt);
        out.field("relation", this.relation);
        out.field("root", this.root);
    }

    public static JsonReader<User> reader = new JsonReader<User>() {
        @Override
        public User read(JsonParser parser) throws IOException, JsonReadException {
            String id = null;
            String rootId = null;
            String name = null;
            String email = null;
            String source = null;
            String displayName = null;
            EnumElement role = null;
            Integer groupsCanOwn = null;
            Boolean isActivated = null;
            Boolean isBlocked = null;
            Integer groupCount = -1;
            Time createdAt = null;
            Relation relation = null;
            Root root = null;

            JsonReader.expectObjectStart(parser);
            while (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                try {
                    Integer fi = FM.get(fieldName);
                    switch (fi) {
                    case -1:
                        JsonReader.skipValue(parser);
                        break;
                    case FM_id:
                        id = JsonReader.STRING_READER.readField(parser, fieldName, id);
                        break;
                    case FM_root_id:
                        rootId = JsonReader.STRING_READER.readField(parser, fieldName, rootId);
                        break;
                    case FM_name:
                        name = JsonReader.STRING_READER.readField(parser, fieldName, name);
                        break;
                    case FM_email:
                        email = JsonReader.STRING_READER.readField(parser, fieldName, email);
                        break;
                    case FM_source:
                        source = JsonReader.STRING_READER.readField(parser, fieldName, source);
                        break;
                    case FM_display_name:
                        displayName = JsonReader.STRING_READER.readField(parser, fieldName, displayName);
                        break;
                    case FM_role:
                        role = EnumElement.reader.readField(parser, fieldName, role);
                        break;
                    case FM_groups_can_own:
                        groupsCanOwn = (int) JsonReader.readUnsignedLong(parser);
                        break;
                    case FM_is_activated:
                        isActivated = JsonReader.readBoolean(parser);
                        break;
                    case FM_is_blocked:
                        isBlocked = JsonReader.readBoolean(parser);
                        break;
                    case FM_group_count:
                        groupCount = (int) JsonReader.readUnsignedLong(parser);
                        break;
                    case FM_created_at:
                        createdAt = Time.reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_relation:
                        relation = Relation.reader.readField(parser, fieldName, relation);
                        break;
                    case FM_root:
                        root = Root.reader.readField(parser, fieldName, root);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new User(id, rootId, name, email, source, displayName, role, groupsCanOwn, isActivated, isBlocked,
                    groupCount, createdAt, relation, root);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_root_id = 1;
    private static final int FM_name = 2;
    private static final int FM_email = 3;
    private static final int FM_source = 4;
    private static final int FM_display_name = 5;
    private static final int FM_role = 6;
    private static final int FM_groups_can_own = 7;
    private static final int FM_is_activated = 8;
    private static final int FM_is_blocked = 9;
    private static final int FM_group_count = 10;
    private static final int FM_created_at = 11;
    private static final int FM_relation = 12;
    private static final int FM_root = 13;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("root_id", FM_root_id);
        b.add("name", FM_name);
        b.add("email", FM_email);
        b.add("source", FM_source);
        b.add("display_name", FM_display_name);
        b.add("role", FM_role);
        b.add("groups_can_own", FM_groups_can_own);
        b.add("is_activated", FM_is_activated);
        b.add("is_blocked", FM_is_blocked);
        b.add("group_count", FM_group_count);
        b.add("created_at", FM_created_at);
        b.add("relation", FM_relation);
        b.add("root", FM_root);

        FM = b.build();
    }

    public String toJsonString() {
        JsonBuilder jb = new JsonBuilder();
        if (this.id != null) {
            jb.addString("id", this.id);
        }
        if (this.rootId != null) {
            jb.addString("root_id", this.rootId);
        }
        if (this.name != null) {
            jb.addString("name", this.name);
        }
        if (this.email != null) {
            jb.addString("email", this.email);
        }
        if (this.source != null) {
            jb.addString("source", this.source);
        }
        if (this.displayName != null) {
            jb.addString("display_name", this.displayName);
        }
        if (this.role != null) {
            jb.addString("role", this.role.toJsonString());
        }
        if (this.groupsCanOwn != null) {
            jb.addInt("groups_can_own", this.groupsCanOwn);
        }
        if (this.isActivated != null) {
            jb.addBoolean("is_activated", this.isActivated);
        }
        if (this.isBlocked != null) {
            jb.addBoolean("is_blocked", this.isBlocked);
        }
        if (this.groupCount != null) {
            jb.addInt("group_count", this.groupCount);
        }
        if (this.createdAt != null) {
            jb.addString("created_at", this.createdAt.toJsonString());
        }
        if (this.relation != null) {
            jb.addString("relation", this.relation.toJsonString());
        }
        if (this.root != null) {
            jb.addString("root", this.root.toJsonString());
        }
        return jb.makeJsonString();
    }
}
