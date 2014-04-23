package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Time extends Dumpable {

    public Long millis = null;
    public String displayValue = null;

    public Time() {
    }

    public Time(long millis, String displayValue) {
        this.millis = millis;
        this.displayValue = displayValue;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("millis", this.millis);
        out.field("display_value", this.displayValue);
    }

    public static JsonReader<Time> reader = new JsonReader<Time>() {
        @Override
        public Time read(JsonParser parser) throws IOException, JsonReadException {
            Long millis = null;
            String displayValue = null;

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
                    case FM_millis:
                        millis = JsonReader.readUnsignedLong(parser);
                        break;
                    case FM_display_value:
                        displayValue = JsonReader.STRING_READER.readField(parser, fieldName, displayValue);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Time(millis, displayValue);
        }
    };

    private static final int FM_millis = 0;
    private static final int FM_display_value = 1;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("millis", FM_millis);
        b.add("display_value", FM_display_value);

        FM = b.build();
    }
}
