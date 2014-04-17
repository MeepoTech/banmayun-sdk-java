package com.banmayun.sdk.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

import com.banmayun.sdk.BMYClient;
import com.banmayun.sdk.BMYClient.ThumbnailFormat;
import com.banmayun.sdk.BMYClient.ThumbnailSize;
import com.banmayun.sdk.BMYException;
import com.banmayun.sdk.BMYRequestConfig;
import com.banmayun.sdk.BMYStreamWriter;
import com.banmayun.sdk.core.Comment;
import com.banmayun.sdk.core.Delta;
import com.banmayun.sdk.core.Group;
import com.banmayun.sdk.core.GroupType;
import com.banmayun.sdk.core.Link;
import com.banmayun.sdk.core.Meta;
import com.banmayun.sdk.core.Permission;
import com.banmayun.sdk.core.Relation;
import com.banmayun.sdk.core.RelationRole;
import com.banmayun.sdk.core.ResultList;
import com.banmayun.sdk.core.Revision;
import com.banmayun.sdk.core.Root;
import com.banmayun.sdk.core.Share;
import com.banmayun.sdk.core.Time;
import com.banmayun.sdk.core.Trash;
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
    public static void main(String[] args) throws Throwable {
        MainThread mainThread = new MainThread();
        mainThread.init();
        client = new BMYClient(new BMYRequestConfig("AndroidClient SDK"));
        
/*        Link link = client.signInUser("hhhhhh", "123456", "Sumsung", LinkDevice.PHONE_ANDROID, "idapname");
        client.setAccessToken(link.token);
        link.print();*/
        

        
        Link link = client.signInUser("huangjian", "HUANGJIANV587", "Sumsung", LinkDevice.PHONE_ANDROID, "idapname");
        client.setAccessToken(link.token);
        ResultList<Group> groups = client.listGroupsForUser(link.userId, new RelationRole("member", "member"), -1, 8);
        Group group = groups.entries.get(2);
        String fileName = "111.JPG";
        long length = new File(fileName).length();
        //InputStream input = client.getFileByPath(group.rootId, "/IMG_0908.JPG", 244L, -1L, new File("111.JPG").length());
        
        client.setUserAvatar(link.userId, new FileInputStream(new File(fileName)));
   }
    
}
