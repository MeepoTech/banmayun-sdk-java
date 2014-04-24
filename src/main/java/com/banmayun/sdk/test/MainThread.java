package com.banmayun.sdk.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jws.soap.SOAPBinding.Use;

import com.banmayun.sdk.BMYClient;
import com.banmayun.sdk.BMYClient.ThumbnailSize;
import com.banmayun.sdk.BMYException;
import com.banmayun.sdk.BMYRequestConfig;
import com.banmayun.sdk.BMYClient.ThumbnailFormat;
import com.banmayun.sdk.core.Comment;
import com.banmayun.sdk.core.Link;
import com.banmayun.sdk.core.User;

public class MainThread {

    public static void main(String[] args) throws BMYException, IOException {
        BMYClient client = new BMYClient(new BMYRequestConfig("AndroidClient SDK"));

        Link link = client.signInUser("hhhhhh", "123456", "ihone", "PHONE_IOS", "IDAPNAME");
        client.setToken(link.token);
        Comment comment = new Comment("id", "rootid", "metaid", "contents", link.createdAt, null);
        System.out.println(comment.toJsonString());
    }
}
