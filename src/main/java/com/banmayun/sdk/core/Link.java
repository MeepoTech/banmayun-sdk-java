package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Link extends Dumpable {

    public enum LinkDevice {
        PC_WINDOWS, PC_MACOSX, PC_LINUX, PHONE_IOS, PHONE_ANDROID, PAD_IOS, PAD_ANDROID, WEB, UNKNOWN
    }
    
    public static String getLinkDeviceStr(LinkDevice linkDevice) {
        String device = null;
        switch (linkDevice) {
        case PC_WINDOWS:
            device = "pc_windows"; 
            break;
        case PC_MACOSX:
            device = "pc_macosx";
            break;
        case PC_LINUX:
            device = "pc_linux";
            break;
        case PHONE_IOS:
            device = "phone_ios";
            break;
        case PHONE_ANDROID:
            device = "phone_android";
            break;
        case PAD_IOS:
            device = "pad_ios";
            break;
        case PAD_ANDROID:
            device = "pad_android";
        case WEB:
            device = "web";
            break;
        case UNKNOWN:
            device = "unknown";
            break;
        default:
            device = "unknown";
            break;
        }
        return device;
    }

    public String id;
    public String name;
    public String device;
    public String token;
    public Time expiresAt;
    public Time createdAt;
    public boolean isCurrent;

    public Link(String id, String name, String device, String token, Time expiresAt, Time createdAt, boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.device = device;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.isCurrent = isCurrent;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("id", id);
        out.field("name", name);
        out.field("device", device);
        out.field("token", token);
        out.field("expires_at", expiresAt);
        out.field("created_at", createdAt);
        out.field("is_current", isCurrent);
    }

    public static JsonReader<Link> Reader = new JsonReader<Link>() {

        @Override
        public Link read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            String name = null;
            String device = null;
            String token = null;
            Time expiresAt = null;
            Time createdAt = null;
            boolean isCurrent = false;

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
                    case FM_name:
                        name = JsonReader.StringReader.readField(parser, fieldName, name);
                        break;
                    case FM_device:
                        device = JsonReader.StringReader.readField(parser, fieldName, device);
                        break;
                    case FM_token:
                        token = JsonReader.StringReader.readField(parser, fieldName, token);
                        break;
                    case FM_expires_at:
                        expiresAt = Time.Reader.readField(parser, fieldName, expiresAt);
                        break;
                    case FM_created_at:
                        createdAt = Time.Reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_is_current:
                        isCurrent = JsonReader.readBoolean(parser);
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
            return new Link(id, name, device, token, expiresAt, createdAt, isCurrent);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_name = 1;
    private static final int FM_device = 2;
    private static final int FM_token = 3;
    private static final int FM_expires_at = 4;
    private static final int FM_created_at = 5;
    private static final int FM_is_current = 6;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("name", FM_name);
        b.add("device", FM_device);
        b.add("token", FM_token);
        b.add("expires_at", FM_expires_at);
        b.add("created_at", FM_created_at);
        b.add("is_current", FM_is_current);

        FM = b.build();
    }
}