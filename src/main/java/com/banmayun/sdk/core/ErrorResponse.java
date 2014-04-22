package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class ErrorResponse extends Dumpable {

    public Integer status = null;
    public Integer code = null;
    public String message = null;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("status", this.status);
        out.field("code", this.code);
        out.field("message", this.message);
    }

    public static JsonReader<ErrorResponse> reader = new JsonReader<ErrorResponse>() {
        @Override
        public ErrorResponse read(JsonParser parser) throws IOException, JsonReadException {
            int status = -1;
            int code = -1;
            String message = null;

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
                    case FM_status:
                        status = (int) JsonReader.readUnsignedLongField(parser, fieldName, status);
                        break;
                    case FM_code:
                        code = (int) JsonReader.readUnsignedLongField(parser, fieldName, code);
                        break;
                    case FM_message:
                        message = JsonReader.STRING_READER.readField(parser, fieldName, message);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new ErrorResponse(status, code, message);
        }
    };

    private static final int FM_status = 0;
    private static final int FM_code = 1;
    private static final int FM_message = 2;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("status", FM_status);
        b.add("code", FM_code);
        b.add("message", FM_message);

        FM = b.build();
    }
}
