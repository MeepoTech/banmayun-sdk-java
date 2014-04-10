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

    public String rfc1123;
    public String displayValue;

    public Time(String rfc1123, String displayValue) {
        this.rfc1123 = rfc1123;
        this.displayValue = displayValue;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("rfc1123", rfc1123);
        out.field("display_value", displayValue);
    }

    public static JsonReader<Time> Reader = new JsonReader<Time>() {

        @Override
        public Time read(JsonParser parser) throws IOException, JsonReadException {
            
            String rfc1123 = null;
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
                    case FM_rfc1123:
                        rfc1123 = JsonReader.StringReader.readField(parser, fieldName, rfc1123);
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
            return new Time(rfc1123, displayValue);
        }
    };

    private static final int FM_rfc1123 = 0;
    private static final int FM_display_value = 1;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("rfc1123", FM_rfc1123);
        b.add("display_value", FM_display_value);

        FM = b.build();
    }
}