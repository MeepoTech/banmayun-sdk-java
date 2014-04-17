package com.banmayun.sdk.core;

import java.io.IOException;

import javax.jws.soap.SOAPBinding.Use;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class User extends Dumpable {

    public String id;
    public String rootId;
    public String name;
    public String email;
    public String source;
    public String displayName;
    public UserRole role;
    public int groupsCanOwn;
    public boolean isActivated;
    public boolean isBlocked;
    public int groupCount;
    public Time createdAt;
    public Relation relation;
    public Root root;

    public User() {

    }

    public User(String id, String rootId, String name, String email, String source, String displayName, UserRole role,
            int groupsCanOwn, boolean isActivated, boolean isBlocked, int groupCount, Time createdAt,
            Relation relation, Root root) {
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
        // TODO Auto-generated method stub
        out.field("id", id);
        out.field("root_id", rootId);
        out.field("name", name);
        out.field("email", email);
        out.field("source", source);
        out.field("display_name", displayName);
        out.field("role", role);
        out.field("groups_can_own", groupsCanOwn);
        out.field("is_activated", isActivated);
        out.field("is_blocked", isBlocked);
        out.field("group_count", groupCount);
        out.field("created_at", createdAt);
        out.field("relation", relation);
        out.field("root", root);
    }

    public static JsonReader<User> Reader = new JsonReader<User>() {

        @Override
        public User read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            String rootId = null;
            String name = null;
            String email = null;
            String source = null;
            String displayName = null;
            UserRole role = null;
            int groupsCanOwn = -1;
            boolean isActivated = false;
            boolean isBlocked = false;
            int groupCount = -1;
            Time createdAt = null;
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
                    case FM_email:
                        email = JsonReader.StringReader.readField(parser, fieldName, email);
                        break;
                    case FM_source:
                        source = JsonReader.StringReader.readField(parser, fieldName, source);
                        break;
                    case FM_display_name:
                        displayName = JsonReader.StringReader.readField(parser, fieldName, displayName);
                        break;
                    case FM_role:
                        role = UserRole.Reader.readField(parser, fieldName, role);
                        break;
                    case FM_groups_can_own:
                        groupsCanOwn = (int) JsonReader.readUnsignedLongField(parser, fieldName, groupsCanOwn);
                        break;
                    case FM_is_activated:
                        isActivated = JsonReader.readBoolean(parser);
                        break;
                    case FM_is_blocked:
                        isBlocked = JsonReader.readBoolean(parser);
                        break;
                    case FM_group_count:
                        groupCount = (int) JsonReader.readUnsignedLongField(parser, fieldName, groupCount);
                        break;
                    case FM_created_at:
                        createdAt = Time.Reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_relation:
                        relation = Relation.Reader.readField(parser, fieldName, relation);
                        break;
                    case FM_root:
                        root = Root.Reader.readField(parser, fieldName, root);
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

    public void print() {
        System.out.println(this.displayName + " " + this.email + " " + this.groupCount + " " + this.groupsCanOwn + " "
                + this.id + " " + this.name + " " + this.rootId + " " + this.source);
    }
}