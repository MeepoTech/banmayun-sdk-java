package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Group extends Dumpable {

    public String id = null;
    public String rootId = null;
    public String name = null;
    public String source = null;
    public EnumElement type = null;
    public String intro = null;
    public String tags = null;
    public String annouce = null;
    public Boolean isVisible = null;
    public Boolean isActivated = null;
    public Boolean isBlocked = null;
    public Integer userCount = null;
    public Time createdAt = null;
    public User createdBy = null;
    public Relation relation = null;
    public Root root = null;

    public Group() {
    }

    public Group(String id, String rootId, String name, String source, EnumElement type, String intro, String tags,
            String annouce, Boolean isVisible, Boolean isActivated, Boolean isBlocked, Integer userCount, Time createdAt,
            User createdBy, Relation relation, Root root) {
        this.id = id;
        this.rootId = rootId;
        this.name = name;
        this.source = source;
        this.type = type;
        this.intro = intro;
        this.tags = tags;
        this.annouce = annouce;
        this.isVisible = isVisible;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
        this.userCount = userCount;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.relation = relation;
        this.root = root;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("id", this.id);
        out.field("root_id", this.rootId);
        out.field("name", this.name);
        out.field("source", this.source);
        out.field("type", this.type);
        out.field("intro", this.intro);
        out.field("tags", this.tags);
        out.field("announce", this.annouce);
        out.field("is_visible", this.isVisible);
        out.field("is_activated", this.isActivated);
        out.field("is_blocked", this.isBlocked);
        out.field("user_count", this.userCount);
        out.field("created_at", this.createdAt);
        out.field("created_by", this.createdBy);
        out.field("relation", this.relation);
        out.field("root", this.root);
    }

    public static JsonReader<Group> reader = new JsonReader<Group>() {
        @Override
        public Group read(JsonParser parser) throws IOException, JsonReadException {
            String id = null;
            String rootId = null;
            String name = null;
            String source = null;
            EnumElement type = null;
            String intro = null;
            String tags = null;
            String annouce = null;
            Boolean isVisible = null;
            Boolean isActivated = null;
            Boolean isBlocked = null;
            Integer userCount = null;
            Time createdAt = null;
            User createdBy = null;
            Relation relation = null;
            Root root = null;

            JsonReader.expectObjectStart(parser);
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
                        id = JsonReader.STRING_READER.readField(parser, fieldName, id);
                        break;
                    case FM_root_id:
                        rootId = JsonReader.STRING_READER.readField(parser, fieldName, rootId);
                        break;
                    case FM_name:
                        name = JsonReader.STRING_READER.readField(parser, fieldName, name);
                        break;
                    case FM_source:
                        source = JsonReader.STRING_READER.readField(parser, fieldName, source);
                        break;
                    case FM_type:
                        type = EnumElement.reader.readField(parser, fieldName, type);
                        break;
                    case FM_intro:
                        intro = JsonReader.STRING_READER.readField(parser, fieldName, intro);
                        break;
                    case FM_tags:
                        tags = JsonReader.STRING_READER.readField(parser, fieldName, tags);
                        break;
                    case FM_announce:
                        annouce = JsonReader.STRING_READER.readField(parser, fieldName, annouce);
                        break;
                    case FM_is_visible:
                        isVisible = JsonReader.readBoolean(parser);
                        break;
                    case FM_is_activated:
                        isActivated = JsonReader.readBoolean(parser);
                        break;
                    case FM_is_blocked:
                        isBlocked = JsonReader.readBoolean(parser);
                        break;
                    case FM_user_count:
                        userCount = (int) JsonReader.readUnsignedLong(parser);
                        break;
                    case FM_created_at:
                        createdAt = Time.reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_created_by:
                        createdBy = User.reader.readField(parser, fieldName, createdBy);
                        break;
                    case FM_relation:
                        relation = Relation.reader.readField(parser, fieldName, relation);
                        break;
                    case FM_root:
                        root = Root.reader.readField(parser, fieldName, root);
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Group(id, rootId, name, source, type, intro, tags, annouce, isVisible, isActivated, isBlocked,
                    userCount, createdAt, createdBy, relation, root);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_root_id = 1;
    private static final int FM_name = 2;
    private static final int FM_source = 3;
    private static final int FM_type = 4;
    private static final int FM_intro = 5;
    private static final int FM_tags = 6;
    private static final int FM_announce = 7;
    private static final int FM_is_visible = 8;
    private static final int FM_is_activated = 9;
    private static final int FM_is_blocked = 10;
    private static final int FM_user_count = 11;
    private static final int FM_created_at = 12;
    private static final int FM_created_by = 13;
    private static final int FM_relation = 14;
    private static final int FM_root = 15;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("root_id", FM_root_id);
        b.add("name", FM_name);
        b.add("source", FM_source);
        b.add("type", FM_type);
        b.add("intro", FM_intro);
        b.add("tags", FM_tags);
        b.add("announce", FM_announce);
        b.add("is_visible", FM_is_visible);
        b.add("is_activated", FM_is_activated);
        b.add("is_blocked", FM_is_blocked);
        b.add("user_count", FM_user_count);
        b.add("created_at", FM_created_at);
        b.add("created_by", FM_created_by);
        b.add("relation", FM_relation);
        b.add("root", FM_root);

        FM = b.build();
    }
    
    public String objectToJsonString() {
        JsonBuilder jb = new JsonBuilder();
        if (name != null) {
            jb.addString("name", name);
        }
        if (type != null) {
            jb.addEnumElement("type", type);
        }
        if (isVisible != null) {
            jb.addBoolean("is_visible", isVisible);
        }
        if (annouce != null) {
            jb.addString("announce", annouce);
        }
        if (intro != null) {
            jb.addString("intro", intro);
        }
        if (tags != null) {
            jb.addString("tags", tags);
        }
        if (source != null) {
            jb.addString("source", source);
        }
        return jb.makeJsonString();
    }
}
