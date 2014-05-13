package com.banmayun.sdk.json;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;

public final class JsonReadException extends java.lang.Exception {

    public static final long serialVersionUID = 1L;

    private String error = null;
    private JsonLocation location = null;
    private PathPart path = null;

    public JsonReadException(String error, JsonLocation location) {
        this.error = error;
        this.location = location;
        this.path = null;
    }

    public JsonReadException addFieldContext(String fieldName) {
        this.path = new PathPart('"' + fieldName + '"', this.path);
        return this;
    }

    public JsonReadException addArrayContext(int index) {
        this.path = new PathPart(Integer.toString(index), this.path);
        return this;
    }

    @Override
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        toStringLocation(buf, this.location);
        buf.append(": ");

        if (this.path != null) {
            PathPart p = this.path;
            buf.append(p.description);
            while (p.next != null) {
                p = p.next;
                buf.append(".");
                buf.append(p.description);
            }
            buf.append(": ");
        }
        buf.append(this.error);
        return buf.toString();
    }

    public static void toStringLocation(StringBuilder buf, JsonLocation location) {
        Object sourceRef = location.getSourceRef();
        if (sourceRef instanceof File) {
            File f = (File) sourceRef;
            buf.append(f.getPath());
            buf.append(": ");
        }

        buf.append(location.getLineNr());
        buf.append(".");
        buf.append(location.getColumnNr());
    }

    public static class PathPart {

        private String description = null;
        private PathPart next = null;

        public PathPart(String description, PathPart next) {
            this.description = description;
            this.next = next;
        }

        public String getDescription() {
            return this.description;
        }

        public PathPart getNext() {
            return this.next;
        }
    }

    public static final JsonReadException fromJackson(JsonProcessingException ex) {
        String message = ex.getMessage();
        int locPos = message.lastIndexOf(" at [Source");
        if (locPos >= 0) {
            message = message.substring(0, locPos);
        }

        return new JsonReadException(message, ex.getLocation());
    }
}
