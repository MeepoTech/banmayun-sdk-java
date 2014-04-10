package com.banmayun.sdk.json;

import com.banmayun.sdk.core.RelationRole;
import com.banmayun.sdk.util.Dumpable;

/* this class is not good for general purpose json builder, 
 * as MeePo APIs are simple, we can use this simple JsonBuilder 
 * for building the request body */
public class JsonBuilder {
  StringBuilder sb;

  public JsonBuilder() {
    sb = new StringBuilder();
    sb.append("{");
  }

  public JsonBuilder addInt(String field, int value) {
    sb.append("\"" + field + "\":" + value + ",\n");
    return this;
  }

  public JsonBuilder addLong(String field, long value) {
    sb.append("\"" + field + "\":" + value + ",\n");
    return this;
  }

  public JsonBuilder addString(String field, String value) {
    sb.append("\"" + field + "\":\"" + value + "\",\n");
    return this;
  }
  
  public JsonBuilder addBoolean(String field, boolean value){
    sb.append("\"" + field + "\":\"" + value + "\",\n");
    return this;
  }

  public JsonBuilder addStringArray(String field, String[] values) {
    sb.append("\"" + field + "\":" + stringArrayToJsonArray(values) + ",\n");
    return this;
  }
  
  public JsonBuilder addRelationRole(String field, RelationRole role) {
      sb.append("\"" + field + "\":{\"name\":" + role.name + "\",\"display_value\":" + role.displayValue + "\"},\n");
      return this;
  }
  

  public String makeJsonString() {
    /* replace the last ',' */
    int lastCommaPosition;
    lastCommaPosition = sb.lastIndexOf(",");
    sb.replace(lastCommaPosition, sb.length(), "\n");
    sb.append("}");
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
