package com.banmayun.sdk.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.banmayun.sdk.BMYClient;
import com.banmayun.sdk.BMYException;
import com.banmayun.sdk.BMYRequestConfig;
import com.banmayun.sdk.core.Link;
import com.banmayun.sdk.core.ResultList;
import com.banmayun.sdk.core.Time;
import com.banmayun.sdk.core.User;
import com.banmayun.sdk.core.UserRole;
import com.banmayun.sdk.core.Link.LinkDevice;
import com.banmayun.sdk.http.BMYTrustManager;


public class MainThread {
    static BMYClient client;
    
    public void init() {
        InputStream[] inputStreams = new InputStream[2];
        try {
          inputStreams[0] = new FileInputStream("./res/raw/cert0");
          inputStreams[1] = new FileInputStream("./res/raw/cert1");
          BMYTrustManager.trustMeePoHosts(inputStreams);
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }
    public static void main(String[] args) throws BMYException {
        MainThread mainThread = new MainThread();
        mainThread.init();
        client = new BMYClient(new BMYRequestConfig("AndroidClient SDK"));
        Link link = client.signInUser("huangjian", "123456", "Sumsung", LinkDevice.PHONE_ANDROID, "idapname");
        client.setAccessToken(link.token);
        System.out.println(link.id);
        /*client.getUser(targetUserId)
        
        try {
            InputStream input;
            input = new FileInputStream(new File("avatar.jpg"));
            Time clientMtime = new Time("1111", "sldfjlk");
            client.uploadFileByPath(rootId, "/aaaa.jpg", clientMtime, true, input);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
        //ResultList<User> user = client.updateUser("ljka", update)
   }
    
}
