package com.banmayun.sdk.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public abstract class DumpWriter {

    public abstract DumpWriter recordStart(String name);

    public abstract DumpWriter recordEnd();

    public abstract DumpWriter fieldStart(String name);

    public abstract DumpWriter listStart();

    public abstract DumpWriter listEnd();

    public abstract DumpWriter verbatim(String s);

    public static final class Multiline extends DumpWriter {

        private StringBuilder buf = null;
        private int indentAmount = 4;
        private int currentIndent = 0;
        private boolean nl = true;

        public Multiline(StringBuilder buf, int indentAmount, int currentIndent, boolean nl) {
            if (buf == null) {
                throw new IllegalArgumentException("'buf' must not be null");
            }
            if (indentAmount < 0) {
                throw new IllegalArgumentException("'indentAmount' must be non-negative");
            }
            if (currentIndent < 0) {
                throw new IllegalArgumentException("'currentIndent' must be non-negative");
            }
            this.buf = buf;
            this.indentAmount = indentAmount;
            this.currentIndent = currentIndent;
            this.nl = nl;
        }

        public Multiline(StringBuilder buf, int indentAmount, boolean nl) {
            this(buf, indentAmount, 0, nl);
        }

        private void prefix() {
            if (this.nl) {
                int l = this.currentIndent;
                for (int i = 0; i < l; i++) {
                    this.buf.append(' ');
                }
            }
        }

        private void indentMore() {
            this.currentIndent += this.indentAmount;
        }

        private void indentLess() {
            if (this.indentAmount > this.currentIndent) {
                throw new IllegalStateException("indent went negative");
            }
            this.currentIndent -= this.indentAmount;
        }

        @Override
        public DumpWriter recordStart(String name) {
            prefix();
            if (name != null) {
                this.buf.append(name).append(" ");
            }
            this.buf.append("{\n");
            this.nl = true;
            indentMore();
            return this;
        }

        @Override
        public DumpWriter recordEnd() {
            if (!this.nl) {
                throw new AssertionError("called recordEnd() in a bad state");
            }
            indentLess();
            prefix();
            this.buf.append("}\n");
            this.nl = true;
            return this;
        }

        @Override
        public DumpWriter fieldStart(String name) {
            if (!this.nl) {
                throw new AssertionError("called fieldStart() in a bad state");
            }
            prefix();
            this.buf.append(name).append(" = ");
            this.nl = false;
            return this;
        }

        @Override
        public DumpWriter listStart() {
            prefix();
            this.buf.append("[\n");
            this.nl = true;
            indentMore();
            return this;
        }

        @Override
        public DumpWriter listEnd() {
            if (!this.nl) {
                throw new AssertionError("called listEnd() in a bad state");
            }
            indentLess();
            this.buf.append("]\n");
            this.nl = true;
            return this;
        }

        @Override
        public DumpWriter verbatim(String s) {
            prefix();
            this.buf.append(s);
            this.buf.append('\n');
            this.nl = true;
            return this;
        }
    }

    public static final class Plain extends DumpWriter {

        private StringBuilder buf = null;
        private boolean needSep = false;

        public Plain(StringBuilder buf) {
            this.buf = buf;
        }

        private void sep() {
            if (this.needSep) {
                this.buf.append(", ");
            } else {
                this.needSep = true;
            }
        }

        @Override
        public DumpWriter recordStart(String name) {
            if (name != null) {
                this.buf.append(name);
            }
            this.buf.append("(");
            this.needSep = false;
            return this;
        }

        @Override
        public DumpWriter recordEnd() {
            this.buf.append(")");
            this.needSep = true;
            return this;
        }

        @Override
        public DumpWriter fieldStart(String name) {
            sep();
            this.buf.append(name).append('=');
            this.needSep = false;
            return this;
        }

        @Override
        public DumpWriter listStart() {
            sep();
            this.buf.append("[");
            this.needSep = false;
            return this;
        }

        @Override
        public DumpWriter listEnd() {
            this.buf.append("]");
            this.needSep = true;
            return this;
        }

        @Override
        public DumpWriter verbatim(String s) {
            sep();
            this.buf.append(s);
            return this;
        }
    }

    public DumpWriter fieldVebatim(String name, String s) {
        return fieldStart(name).verbatim(s);
    }

    public DumpWriter field(String name, String v) {
        return fieldStart(name).value(v);
    }

    public DumpWriter field(String name, int v) {
        return fieldStart(name).value(v);
    }

    public DumpWriter field(String name, long v) {
        return fieldStart(name).value(v);
    }

    public DumpWriter field(String name, boolean v) {
        return fieldStart(name).value(v);
    }

    public DumpWriter field(String name, Date v) {
        return fieldStart(name).value(v);
    }

    public DumpWriter field(String name, Dumpable v) {
        return fieldStart(name).value(v);
    }

    public DumpWriter list(Iterable<? extends Dumpable> list) {
        listStart();
        values(list);
        return listEnd();
    }

    public DumpWriter value(String v) {
        if (v == null) {
            verbatim("null");
        } else {
            verbatim(StringUtil.jq(v));
        }
        return this;
    }

    public DumpWriter value(int v) {
        return verbatim(Integer.toString(v));
    }

    public DumpWriter value(long v) {
        return verbatim(Long.toString(v));
    }

    public DumpWriter value(boolean v) {
        return verbatim(Boolean.toString(v));
    }

    public DumpWriter value(Date v) {
        return verbatim(toStringDate(v));
    }

    public DumpWriter value(Dumpable v) {
        if (v == null) {
            return this.value((String) null);
        }
        recordStart(v.getTypeName());
        v.dumpFields(this);
        return recordEnd();
    }

    public DumpWriter values(Iterable<? extends Dumpable> list) {
        listStart();
        for (Dumpable d : list) {
            value(d);
        }
        return listEnd();
    }

    public static String toStringDate(Date date) {
        if (date == null) {
            return "null";
        } else {
            StringBuilder buf = new StringBuilder();
            GregorianCalendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            c.setTime(date);
            String year = Integer.toString(c.get(Calendar.YEAR));
            String month = zeroPad(Integer.toString(c.get(Calendar.MONTH) + 1), 2);
            String day = zeroPad(Integer.toString(c.get(Calendar.DAY_OF_MONTH)), 2);
            String hour = zeroPad(Integer.toString(c.get(Calendar.HOUR_OF_DAY)), 2);
            String minute = zeroPad(Integer.toString(c.get(Calendar.MINUTE)), 2);
            String second = zeroPad(Integer.toString(c.get(Calendar.SECOND)), 2);
            buf.append('"');
            buf.append(year).append("/").append(month).append("/").append(day).append(" ");
            buf.append(hour).append(":").append(minute).append(":").append(second).append(" UTC");
            buf.append('"');
            return buf.toString();
        }
    }

    private static String zeroPad(String v, int desiredLength) {
        while (v.length() < desiredLength) {
            v = "0" + v;
        }
        return v;
    }
}
