package com.banmayun.sdk;

public class BMYHost {

    public final String api;
    public final String web;

    public static final BMYHost Default = new BMYHost("192.168.200.195:5000", "www.banmayun.com");

    // public static final BMYHost Default = new BMYHost("api.banmayun.com",
    // "www.banmayun.com");

    public BMYHost(String api, String web) {
        this.api = api;
        this.web = web;
    }

    public boolean equals(Object o) {
        return getClass().equals(o.getClass()) && equals((BMYHost) o);
    }

    public boolean equals(BMYHost o) {
        return api.equals(o.api) && web.equals(o.web);
    }

    private static BMYHost fromBaseHost(String s) {
        return new BMYHost("api." + s, "www." + s);
    }
}
