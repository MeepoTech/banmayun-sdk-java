package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Time extends Dumpable {

    public long millis;
    public String displayValue;

    public Time(long millis, String displayValue) {
        this.millis = millis;
        this.displayValue = displayValue;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("millis", millis);
        out.field("display_value", displayValue);
    }

    public static JsonReader<Time> Reader = new JsonReader<Time>() {

        @Override
        public Time read(JsonParser parser) throws IOException, JsonReadException {

            long millis = -1;
            String displayValue = null;

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
                    case FM_millis:
                        millis = JsonReader.readUnsignedLongField(parser, fieldName, millis);
                        break;
                    case FM_display_value:
                        displayValue = JsonReader.StringReader.readField(parser, fieldName, displayValue);
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
            return new Time(millis, displayValue);
        }
    };

    private static final int FM_millis = 0;
    private static final int FM_display_value = 1;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("rfc1123", FM_millis);
        b.add("display_value", FM_display_value);

        FM = b.build();
    }

    public void print() {
        System.out.println(this.millis + " " + this.displayValue);
    }
}