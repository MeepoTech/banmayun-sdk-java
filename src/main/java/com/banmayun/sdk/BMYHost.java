package com.banmayun.sdk;

public class BMYHost {

    public static final BMYHost DEFAULT = new BMYHost("192.168.200.195:5000", "www.banmayun.com");

    public final String api;
    public final String web;

    public BMYHost(String api, String web) {
        this.api = api;
        this.web = web;
    }

    @Override
    public boolean equals(Object o) {
        return getClass().equals(o.getClass()) && equals((BMYHost) o);
    }

    public boolean equals(BMYHost o) {
        return this.api.equals(o.api) && this.web.equals(o.web);
    }
}
