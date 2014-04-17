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

public class Revision extends Dumpable {

    public long version;
    public String md5;
    public Size size;
    public Time modifiedAt;
    public User modifiedBy;
    public Time clientModifiedAt;

    Revision(long version, String md5, Size size, Time modifiedAt, User modifiedBy, Time clientModifiedAt) {
        this.version = version;
        this.md5 = md5;
        this.size = size;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.clientModifiedAt = clientModifiedAt;

    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("version", version);
        out.field("md5", md5);
        out.field("size", size);
        out.field("modified_at", modifiedAt);
        out.field("modified_by", modifiedBy);
        out.field("client_modified_at", clientModifiedAt);
    }

    public static JsonReader<Revision> Reader = new JsonReader<Revision>() {

        @Override
        public Revision read(JsonParser parser) throws IOException, JsonReadException {

            long version = -1;
            String md5 = null;
            Size size = null;
            Time modifiedAt = null;
            User modifiedBy = null;
            Time clientModifiedAt = null;

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
                    case FM_version:
                        version = JsonReader.readUnsignedLongField(parser, fieldName, version);
                        break;
                    case FM_md5:
                        md5 = JsonReader.StringReader.readField(parser, fieldName, md5);
                        break;
                    case FM_size:
                        size = Size.Reader.readField(parser, fieldName, size);
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
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            // TODO: add some checks?
            return new Revision(version, md5, size, modifiedAt, modifiedBy, clientModifiedAt);
        }
    };

    private static final int FM_version = 0;
    private static final int FM_md5 = 1;
    private static final int FM_size = 2;
    private static final int FM_modified_at = 3;
    private static final int FM_modified_by = 4;
    private static final int FM_client_modified_at = 5;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("version", FM_version);
        b.add("md5", FM_md5);
        b.add("size", FM_size);
        b.add("modified_at", FM_modified_at);
        b.add("modified_by", FM_modified_by);
        b.add("client_modified_at", FM_client_modified_at);

        FM = b.build();
    }

    public void print() {
        System.out.println(this.md5 + " " + this.version + " " + this.size.bytes);

    }
}