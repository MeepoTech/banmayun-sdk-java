package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GroupType extends Dumpable {

    public String name;
    public String displayValue;

    public GroupType(String name, String displayValue) {
        this.name = name;
        this.displayValue = displayValue;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("name", name);
        out.field("display_value", displayValue);
    }

    public static JsonReader<GroupType> Reader = new JsonReader<GroupType>() {

        @Override
        public GroupType read(JsonParser parser) throws IOException, JsonReadException {

            String name = null;
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
                    case FM_name:
                        name = JsonReader.StringReader.readField(parser, fieldName, name);
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
            return new GroupType(name, displayValue);
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
}