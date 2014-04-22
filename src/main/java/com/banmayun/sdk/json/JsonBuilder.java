package com.banmayun.sdk.json;

import com.banmayun.sdk.core.EnumElement;

/* this class is not good for general purpose json builder, 
 * as Banmayun APIs are simple, we can use this simple JsonBuilder 
 * for building the request body */
public class JsonBuilder {

    private StringBuilder sb;

    public JsonBuilder() {
        this.sb = new StringBuilder();
        this.sb.append("{");
    }

    public JsonBuilder addInt(String field, int value) {
        this.sb.append("\"" + field + "\":" + value + ",\n");
        return this;
    }

    public JsonBuilder addLong(String field, long value) {
        this.sb.append("\"" + field + "\":" + value + ",\n");
        return this;
    }

    public JsonBuilder addString(String field, String value) {
        this.sb.append("\"" + field + "\":\"" + value + "\",\n");
        return this;
    }

    public JsonBuilder addBoolean(String field, boolean value) {
        this.sb.append("\"" + field + "\":\"" + value + "\",\n");
        return this;
    }

    public JsonBuilder addStringArray(String field, String[] values) {
        this.sb.append("\"" + field + "\":" + stringArrayToJsonArray(values) + ",\n");
        return this;
    }

    public JsonBuilder addEnumElement(String field, EnumElement e) {
        this.sb.append("\"" + field + "\":{\"name\":\"" + e.name + "\",\"display_value\":\"" + e.displayValue
                + "\"},\n");
        return this;
    }

    public String makeJsonString() {
        /* replace the last ',' */
        int lastCommaPosition;
        lastCommaPosition = this.sb.lastIndexOf(",");
        this.sb.replace(lastCommaPosition, sb.length(), "\n");
        this.sb.append("}");
        return sb.toString();
    }

    public static String stringArrayToJsonArray(String[] values) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < values.length; i++) {
            sb.append("\"" + values[i] + "\"");
            if (i != (values.length - 1))
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
