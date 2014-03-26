package com.banmayun.sdk;

import java.util.Locale;
import java.util.TimeZone;

import com.banmayun.sdk.core.Link;

public class Session {

    public static Session obtainSessionBySignIn(Locale locale, TimeZone timeZone) {
        // TODO:
        return new Session(locale, timeZone);
    }

    public static Session obtainSessionBySignUp(Locale locale, TimeZone timeZone) {
        // TODO:
        return new Session(locale, timeZone);
    }

    private Locale locale = null;
    private TimeZone timeZone = null;
    private Link link = null;

    public Session(Locale locale, TimeZone timeZone, Link link) {
        this.locale = locale;
        this.timeZone = timeZone;
        this.link = link;
    }

    public Session(Locale locale, TimeZone timeZone) {
        this.locale = locale;
        this.timeZone = timeZone;
    }

    public Link getLink() {
        return this.link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
