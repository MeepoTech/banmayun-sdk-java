package com.banmayun.sdk.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.banmayun.sdk.BMYClient;
import com.banmayun.sdk.BMYClient.ThumbnailSize;
import com.banmayun.sdk.BMYException;
import com.banmayun.sdk.BMYRequestConfig;
import com.banmayun.sdk.BMYClient.ThumbnailFormat;
import com.banmayun.sdk.core.Link;

public class MainThread {

    public static void main(String[] args) throws BMYException, IOException {
        BMYClient client = new BMYClient(new BMYRequestConfig("AndroidClient SDK"));
        Link link = client.signInUser("huangjian", "HUANGJIANV587", "ihone", "PHONE_IOS", "IDAPNAME");
        client.setToken(link.token);
        InputStream input = client.getUserAvatar(link.userId, ThumbnailFormat.JPEG, ThumbnailSize.L);
        File file = new File("test.jpg");
        OutputStream output = new FileOutputStream(file);
        byte buf[] = new byte[4 * 1024];
        int len = 0;
        while ((len = input.read(buf)) != -1) {
            output.write(buf, 0, len);
        }
        output.flush();
        output.close();
        input.close();
    }
}
