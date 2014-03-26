package com.banmayun.sdk;

import javax.ws.rs.core.MediaType;

import com.banmayun.sdk.core.User;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.WebResource;

public class ClientImpl implements Client {

    private Session session = null;
    private WebResource base = null;

    public ClientImpl(String host, int port, Session session) {
        this.session = session;

        com.sun.jersey.api.client.config.ClientConfig clientConfig = new com.sun.jersey.api.client.config.DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create(clientConfig);

        this.base = client.resource("http://" + host + ":" + port + "/" + Version.API_VERSION);
    }

    public User getUser() {
        WebResource resource = this.fillCommonParams("/user" + this.session.getLink().token, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", User.class);
    }

    private WebResource fillCommonParams(String url, boolean tokenRequired) {
        WebResource ret = this.base.path(url).queryParam("locale", this.session.getLocale().toString())
                .queryParam("time_zone", this.session.getTimeZone().toString());
        if (!tokenRequired) {
            return ret;
        }

        return ret.queryParam("token", this.session.getLink().token);
    }
}
