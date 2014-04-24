package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class EnumElement extends Dumpable {

    public String name = null;
    public String displayValue = null;

    public EnumElement() {
    }

    public EnumElement(String name, String displayValue) {
        this.name = name;
        this.displayValue = displayValue;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("name", this.name);
        out.field("display_value", this.displayValue);
    }

    public static JsonReader<EnumElement> reader = new JsonReader<EnumElement>() {
        @Override
        public EnumElement read(JsonParser parser) throws IOException, JsonReadException {
            String name = null;
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
                    case FM_name:
                        name = JsonReader.STRING_READER.readField(parser, fieldName, name);
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

            return new EnumElement(name, displayValue);
        }
    };

    private static final int FM_name = 0;
    private static final int FM_display_value = 1;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("name", FM_name);
        b.add("display_value", FM_display_value);

        FM = b.build();
    }

    public String toJsonString() {
        JsonBuilder jb = new JsonBuilder();
        if (name != null) {
            jb.addString("name", name);
        }
        if (displayValue != null) {
            jb.addString("display_value", displayValue);
        }
        return jb.makeJsonString();
    }
}
