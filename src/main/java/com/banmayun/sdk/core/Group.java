package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Group extends Dumpable {

    public String id;
    public String rootId;
    public String name;
    public String source;
    public GroupType type;
    public String intro;
    public String tags;
    public String annouce;
    public boolean isVisible;
    public boolean isActivated;
    public boolean isBlocked;
    public int userCount;
    public Time createdAt;
    public User createdBy;
    public Relation relation;
    public Root root;

    public Group() {
    }

    public Group(String id, String rootId, String name, String source, GroupType type, String intro, String tags,
            String annouce, boolean isVisible, boolean isActivated, boolean isBlocked, int userCount, Time createdAt,
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
        // TODO Auto-generated method stub
        out.field("id", id);
        out.field("root_id", rootId);
        out.field("name", name);
        out.field("source", source);
        out.field("type", type);
        out.field("intro", intro);
        out.field("tags", tags);
        out.field("announce", annouce);
        out.field("is_visible", isVisible);
        out.field("is_activated", isActivated);
        out.field("is_blocked", isBlocked);
        out.field("user_count", userCount);
        out.field("created_at", createdAt);
        out.field("created_by", createdBy);
        out.field("relation", relation);
        out.field("root", root);
    }

    public static JsonReader<Group> Reader = new JsonReader<Group>() {

        @Override
        public Group read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            String rootId = null;
            String name = null;
            String source = null;
            GroupType type = null;
            String intro = null;
            String tags = null;
            String annouce = null;
            boolean isVisible = false;
            boolean isActivated = false;
            boolean isBlocked = false;
            int userCount = -1;
            Time createdAt = null;
            User createdBy = null;
            Relation relation = null;
            Root root = null;

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
                    case FM_source:
                        source = JsonReader.StringReader.readField(parser, fieldName, source);
                        break;
                    case FM_type:
                        type = GroupType.Reader.readField(parser, fieldName, type);
                        break;
                    case FM_intro:
                        intro = JsonReader.StringReader.readField(parser, fieldName, intro);
                        break;
                    case FM_tags:
                        tags = JsonReader.StringReader.readField(parser, fieldName, tags);
                        break;
                    case FM_announce:
                        annouce = JsonReader.StringReader.readField(parser, fieldName, annouce);
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
                        userCount = (int) JsonReader.readUnsignedLongField(parser, fieldName, userCount);
                        break;
                    case FM_created_at:
                        createdAt = Time.Reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_created_by:
                        createdBy = User.Reader.readField(parser, fieldName, createdBy);
                        break;
                    case FM_relation:
                        relation = Relation.Reader.readField(parser, fieldName, relation);
                        break;
                    case FM_root:
                        root = Root.Reader.readField(parser, fieldName, root);
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            // TODO: add some checks?
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

    public void print() {
        System.out.println(this.annouce + " " + this.id + " " + this.intro + " " + this.name + " " + this.rootId + " "
                + this.source + " " + this.tags);
    }
}