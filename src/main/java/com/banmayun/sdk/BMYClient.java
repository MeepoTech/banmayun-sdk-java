package com.banmayun.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.banmayun.sdk.core.ChunkedUpload;
import com.banmayun.sdk.core.Comment;
import com.banmayun.sdk.core.Delta;
import com.banmayun.sdk.core.Group;
import com.banmayun.sdk.core.GroupType;
import com.banmayun.sdk.core.Link;
import com.banmayun.sdk.core.Link.LinkDevice;
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
import com.banmayun.sdk.http.HttpRequestor;
import com.banmayun.sdk.http.HttpRequestor.Response;
import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.CountingOutputStream;
import com.banmayun.sdk.util.IOUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

/* BMYClient Version 1*/
public class BMYClient {

    private static final long ChunkedUploadThreshold = 8 * 1024 * 1024;
    private static final long ChunkedUploadChunkSize = 4 * 1024 * 1024;

    BMYRequestConfig requestConfig;
    String accessToken;
    BMYHost host;

    public enum ThumbnailFormat {
        JPEG, PNG
    }

    public String getThumbnailFormatStr(ThumbnailFormat format) {
        String formatStr = "";
        switch (format) {
        case JPEG:
            formatStr = "jpeg";
            break;
        case PNG:
            formatStr = "png";
            break;
        default:
            break;
        }
        return formatStr;
    }

    public enum ThumbnailSize {
        XS, S, M, L, XL
    }

    public String getThumbnailSizeStr(ThumbnailSize size) {
        String sizeStr = "";
        switch (size) {
        case XS:
            sizeStr = "xs";
            break;
        case S:
            sizeStr = "s";
            break;
        case M:
            sizeStr = "m";
            break;
        case L:
            sizeStr = "l";
            break;
        case XL:
            sizeStr = "xl";
            break;
        default:
            break;
        }
        return sizeStr;
    }

    public String inputString2String(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                // sb.append(line + "/n");
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void inputStreamToFile(InputStream input, String fileName) throws IOException {
        OutputStream output = new FileOutputStream(new File(fileName));
        int ch = 0;
        while ((ch = input.read()) != -1) {
            output.write(ch);
        }
        output.close();
        input.close();

    }

    public int getInputStreamLength(InputStream in) {
        InputStream input = in;
        String inputStr = inputString2String(input);
        return inputStr.length();
    }

    public BMYClient(BMYRequestConfig requestConfig) {
        this(requestConfig, null);
    }

    public BMYClient(BMYRequestConfig requestConfig, String accessToken) {
        this(requestConfig, accessToken, BMYHost.Default);
    }

    public BMYClient(BMYRequestConfig requestConfig, String accessToken, BMYHost host) {
        if (requestConfig == null)
            throw new IllegalArgumentException("'requestConfig' is null");
        if (host == null)
            throw new IllegalArgumentException("'host' is null");

        this.requestConfig = requestConfig;
        this.accessToken = accessToken;
        this.host = host;
    }

    public BMYRequestConfig getRequestConfig() {
        return requestConfig;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    // ok
    public Link signInUser(String userName, String password, String linkName, LinkDevice linkDevice, String ldapName)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/auth/sign_in";
        String[] params = { "username", userName, "password", password, "link_name", linkName, "device",
                Link.getLinkDeviceStr(linkDevice) };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Link>() {
            @Override
            public Link handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Link.Reader, response.body);
            }
        });
    }

    // ok
    public Link signOutUser() throws BMYException {
        String host = this.host.api;
        String apiPath = "1/auth/sign_out";
        String[] params = { "token", this.accessToken };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Link>() {

            @Override
            public Link handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Link.Reader, response.body);
            }
        });
    }

    // ok
    public User forgotUserPassword(String email) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/auth/forgot_password";
        String[] params = { "email", email };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public User changeUserPassword(String userName, String oldPassword, String newPassword) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/auth/change_password";
        String[] params = { "username", userName, "password", oldPassword, "new_password", newPassword };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // not convenient to test
    public User resetUserPassword(String passwordResetToken, String newPassword) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/auth/reset_password";
        String[] params = { "token", passwordResetToken, "new_password", newPassword };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public User existsUser(User user) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/exists";
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        JsonBuilder jb = new JsonBuilder();
        if (user.name != null) {
            jb.addString("name", user.name);
        }
        if (user.email != null) {
            jb.addString("email", user.email);
        }
        String body = jb.makeJsonString();
        return doPost(host, apiPath, null, headers, body, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public User createUser(User user, String password) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users";
        String[] params = { "token", this.accessToken, "password", password };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        JsonBuilder jb = new JsonBuilder();
        jb.addString("name", user.name);
        jb.addString("email", user.email);
        if (user.displayName != null) {
            jb.addString("display_name", user.displayName);
        }
        String body = jb.makeJsonString();
        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public User getUser(String targetUserId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<User> listUsers(UserRole role, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users";
        String[] params = { "token", this.accessToken, "role", role.name, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {

            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.Reader), response.body);
            }
        });
    }

    // ok
    public User updateUser(String targetUserId, User update) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users" + "/" + targetUserId + "/update";
        String[] params = { "token", this.accessToken };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        JsonBuilder jb = new JsonBuilder();
        if (update.displayName != null) {
            jb.addString("display_name", update.displayName);
        }
        String body = jb.makeJsonString();
        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });

    }

    // ok
    public User setUserPassword(String targetUserId, String newPassword) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/password";
        String[] params = { "token", this.accessToken, "new_password", newPassword };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public void setUserAvatar(String targetUserId, InputStream input) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/avatar";
        String[] params = { "token", this.accessToken };
        String body = inputString2String(input);
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        headers.add(new HttpRequestor.Header("Content-Length", "" + body.length()));
        
        System.out.println("" + body.length());
        doPost(host, apiPath, params, headers, body, null);
    }

    // TODO: 500
    public InputStream getUserAvatar(String targetUserId, ThumbnailFormat format, ThumbnailSize size)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/avatar";
        String[] params = { "token", this.accessToken, "format", getThumbnailFormatStr(format), "size",
                getThumbnailSizeStr(size) };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<InputStream>() {

            @Override
            public InputStream handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return response.body;
            }
        });
    }

    // ok
    public Group addUserGroup(String targetUserId, String groupId, Relation relation) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/groups";
        String[] params = { "token", this.accessToken, "group_id", groupId };

        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        String body = new JsonBuilder().addRelationRole("role", relation.role).makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group addUserGroup(String targetUserId, String groupId, Relation relation, String remark)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/groups";

        String[] params = { "token", this.accessToken, "group_id", groupId };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        String body = new JsonBuilder().addRelationRole("role", relation.role).addString("remark", remark)
                .makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group getUserGroup(String targetUserId, String groupId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/groups/" + groupId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<Group> listGroupsForUser(String targetUserId, RelationRole role, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/groups";
        //
        String[] params = { "token", this.accessToken, "role", role.name, "offset", "" + offset, "limit", "" + limit };
        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {

            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.Reader), response.body);
            }
        });
    }

    // ok
    public Group updateUserGroup(String targetUserId, String groupId, Relation update) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/groups/" + groupId + "/update";

        String[] params = { "token", this.accessToken };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        String body = new JsonBuilder().addRelationRole("role", update.role)
                .addBoolean("is_activated", update.isActivated).addBoolean("is_blocked", update.isBlocked)
                .makeJsonString();
        System.out.println(body);
        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                System.out.println(response.statusCode);
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group removeUserGroup(String targetUserId, String groupId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/groups/" + groupId;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group existsGroup(Group group) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/exists";
        String[] params = { "token", this.accessToken };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        String body = new JsonBuilder().addString("name", group.name).makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group createGroup(Group group, String ownerId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups";
        String[] params = { "token", this.accessToken };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));
        JsonBuilder jb = new JsonBuilder().addString("name", group.name);
        jb.addGroupType("type", group.type);
        jb.addBoolean("is_visible", group.isVisible);
        if (group.annouce != null) {
            jb.addString("announce", group.annouce);
        }
        if (group.intro != null) {
            jb.addString("intro", group.intro);
        }
        if (group.tags != null) {
            jb.addString("tags", group.tags);
        }
        String body = jb.makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group getGroup(String groupId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId;
        String[] params = { "token", this.accessToken };
        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<Group> listGroups(GroupType type, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups";
        String[] params = { "token", this.accessToken, "type", type.name, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {

            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.Reader), response.body);
            }
        });
    }

    // ok
    public Group updateGroup(String groupId, Group update) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/update";
        String[] params = { "token", this.accessToken };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));

        JsonBuilder jb = new JsonBuilder();
        if (update.type != null) {
            jb.addGroupType("type", update.type);
        }
        jb.addBoolean("is_visible", update.isVisible);
        if (update.annouce != null) {
            jb.addString("announce", update.annouce);
        }
        if (update.intro != null) {
            jb.addString("intro", update.intro);
        }
        if (update.tags != null) {
            jb.addString("tags", update.tags);
        }
        if (update.source != null) {
            jb.addString("source", update.source);
        }
        String body = jb.makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public Group deleteGroup(String groupId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId;
        String[] params = { "token", this.accessToken };
        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Group>() {

            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Group.Reader, response.body);
            }
        });
    }

    // ok
    public void setGroupLogo(String groupId, InputStream input) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/logo";
        String[] params = { "token", this.accessToken };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        String body = inputString2String(input);

        doPost(host, apiPath, params, headers, body, null);
    }

    // TODO: 500
    public InputStream getGroupLogo(String groupId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/logo";
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<InputStream>() {

            @Override
            public InputStream handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return response.body;
            }
        });
    }

    // ok
    public User addGroupUser(String groupId, String targetUserId, Relation relation) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/users";
        String[] params = { "token", this.accessToken, "user_id", targetUserId };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public User getGroupUser(String groupId, String targetUserId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/users/" + targetUserId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<User> listUsersForGroup(String groupId, RelationRole role, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/users";
        String[] params = { "token", this.accessToken, "role", role.name, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {

            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.Reader), response.body);
            }
        });
    }

    // ok
    public User updateGroupUser(String groupId, String targetUserId, Relation update) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/users/" + targetUserId + "/update";
        String[] params = { "token", this.accessToken };

        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));

        JsonBuilder jb = new JsonBuilder();
        jb.addRelationRole("role", update.role);
        jb.addBoolean("is_activated", update.isActivated);
        jb.addBoolean("is_blocked", update.isBlocked);
        String body = jb.makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public User removeGroupUser(String groupId, String targetUserId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/users/" + targetUserId;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<User>() {

            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(User.Reader, response.body);
            }
        });
    }

    // ok
    public Root getRoot(String rootId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Root>() {

            @Override
            public Root handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Root.Reader, response.body);
            }
        });
    }

    // ok
    public Root setRootDefaultPermission(String rootId, Permission defaultPermission) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/default_permission";
        String[] params = { "token", this.accessToken };

        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));

        JsonBuilder jb = new JsonBuilder();
        jb.addBoolean("insertable_to_owner", defaultPermission.insertableToOwner);
        jb.addBoolean("readable_to_owner", defaultPermission.readableToOwner);
        jb.addBoolean("writable_to_owner", defaultPermission.writableToOwner);
        jb.addBoolean("deletable_to_owner", defaultPermission.deletableToOwner);
        jb.addBoolean("insertable_to_others", defaultPermission.insertableToOthers);
        jb.addBoolean("readable_to_others", defaultPermission.readableToOthers);
        jb.addBoolean("writable_to_others", defaultPermission.writableToOthers);
        jb.addBoolean("deletable_to_others", defaultPermission.deletableToOthers);
        String body = jb.makeJsonString();
        System.out.println(body);
        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Root>() {
            @Override
            public Root handle(Response response) throws BMYException {
                System.out.println(response.statusCode);
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Root.Reader, response.body);
            }
        });
    }

    // ok
    public Root setRootQuota(String rootId, String quota) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/quota";
        String[] params = { "token", this.accessToken, "quota", quota };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Root>() {

            @Override
            public Root handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Root.Reader, response.body);
            }
        });
    }

    // ok
    public <E extends Throwable> Meta uploadFileByPath(String rootId, String path, Time clientMtime, Boolean overwrite,
            InputStream input, long numBytes) throws Throwable {
        // int numBytes = getInputStreamLength(input);
        BMYStreamWriter<?> writer = new BMYStreamWriter.InputStreamCopier(input);
        Uploader uploader = startUploadFile(rootId, path, numBytes, clientMtime, overwrite);
        return finishUploadFile(uploader, writer);

    }

    // TODO: 500
    public InputStream getFileByPath(String rootId, String path, Long version, Long offset, Long bytes)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/p/" + path;
        String[] params = { "token", this.accessToken, "version", "" + version, "offset", "" + offset, "size",
                "" + bytes };
        Response response = BMYRequestUtil.startGet(requestConfig, host, apiPath, params, null);
        System.out.println(response.statusCode);
        return response.body;
    }

    // ok
    public Meta trashRecursivelyByPath(String rootId, String path) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/p/" + path;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // TODO: not check
    public Meta uploadFile(String rootId, String metaId, Time clientMtime, InputStream input) throws BMYException {
        return null;
    }

    // TODO: 500
    public InputStream getFile(String rootId, String metaId, Long version, Long offset, Long bytes) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId;
        String[] params = { "token", this.accessToken, "version", "" + version, "offset", "" + offset, "size",
                "" + bytes };
        Response response = BMYRequestUtil.startGet(requestConfig, host, apiPath, params, null);
        System.out.println(response.statusCode);
        return response.body;
    }

    // ok
    public Meta trashRecursively(String rootId, String metaId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta getMeta(String rootId, String metaId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/meta";
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // TOD0: 500
    public InputStream getFileThumbnail(String rootId, String metaId, ThumbnailFormat format, ThumbnailSize size)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/thumbnail";
        String[] params = { "token", this.accessToken };

        return BMYRequestUtil.startGet(requestConfig, host, apiPath, params, null).body;
    }

    // ok
    public ResultList<Revision> listRevisionsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/revisions";
        String[] params = { "token", this.accessToken, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Revision>>() {

            @Override
            public ResultList<Revision> handle(Response response) throws BMYException {
                if (response.statusCode != 200)
                    throw BMYRequestUtil.unexpectedStatus(response);
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Revision>(Revision.Reader),
                        response.body);
            }
        });
    }

    // ok
    public Comment createComment(String rootId, String metaId, Comment comment) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments";

        String[] params = { "token", this.accessToken };

        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));

        String body = new JsonBuilder().addString("contents", comment.contents).makeJsonString();
        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Comment>() {

            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.Reader, response.body);
            }
        });
    }

    // ok
    public Comment getComment(String rootId, String metaId, String commentId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments/" + commentId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Comment>() {

            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<Comment> listComments(Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/all/files/all/comments";
        String[] params = { "token", this.accessToken, "offset", Integer.toString(offset), "limit",
                Integer.toString(limit) };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Comment>>() {

            @Override
            public ResultList<Comment> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Comment>(Comment.Reader),
                        response.body);
            }
        });
    }

    // ok
    public ResultList<Comment> listCommentsForRoot(String rootId, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/all/comments";
        String[] params = { "token", this.accessToken, "offset", Integer.toString(offset), "limit",
                Integer.toString(limit) };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Comment>>() {

            @Override
            public ResultList<Comment> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Comment>(Comment.Reader),
                        response.body);
            }
        });
    }

    // ok
    public ResultList<Comment> listCommentsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments";
        String[] params = { "token", this.accessToken, "offset", Integer.toString(offset), "limit",
                Integer.toString(limit) };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Comment>>() {

            @Override
            public ResultList<Comment> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Comment>(Comment.Reader),
                        response.body);
            }
        });
    }

    // ok
    public Comment deleteComment(String rootId, String metaId, String commentId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments/" + commentId;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Comment>() {

            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteComments() throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/all/files/all/comments";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Comment>() {

            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteCommentsForRoot(String rootId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/all/comments";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Comment>() {

            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteCommentsForMeta(String rootId, String metaId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Comment>() {

            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.Reader, response.body);
            }
        });
    }

    // ok
    public Share createShare(String rootId, String metaId, String password, Time expiresAt) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares";
        String[] params = { "token", this.accessToken, "password", password, "expires_at_millis", "" + expiresAt.millis };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Share>() {

            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.Reader, response.body);
            }
        });
    }

    // ok
    public Share getShare(String rootId, String metaId, String shareId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares/" + shareId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Share>() {

            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<Share> listShares(Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/all/files/all/shares";
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Share>>() {

            @Override
            public ResultList<Share> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<>(Share.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Share> listSharesForRoot(String rootId, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/all/shares";
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Share>>() {

            @Override
            public ResultList<Share> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<>(Share.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Share> listSharesForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares";
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Share>>() {

            @Override
            public ResultList<Share> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<>(Share.Reader), response.body);
            }
        });
    }

    // ok
    public Share deleteShare(String rootId, String metaId, String shareId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares/" + shareId;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Share>() {

            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteShares() throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/all/files/all/shares";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Share>() {

            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteSharesForRoot(String rootId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/all/shares";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Share>() {

            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteSharesForMeta(String rootId, String metaId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Share>() {

            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.Reader, response.body);
            }
        });
    }

    // ok
    public Meta commitChunkedUploadByPath(String rootId, String path, String uploadId, Time clientMtime)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/commit_chunked_upload";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "upload_id", uploadId,
                "modified_at_millis", "" + clientMtime.millis };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta copyRecursivelyByPath(String rootId, String path, String toPath) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/copy";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "to_path", toPath };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta createFolderByPath(String rootId, String path, Time clientMtime) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/create_folder";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "modified_at_millis",
                "" + clientMtime.millis };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta getMetaByPath(String rootId, String path, Boolean list) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/get_meta";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "list", Boolean.toString(list) };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public List<Meta> listFolderByPath(String rootId, String path) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/list_folder";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<List<Meta>>() {

            @Override
            public List<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                JsonParser parser;
                ArrayList<Meta> metaList = new ArrayList<>();
                try {
                    parser = new JsonFactory().createParser(response.body);

                    try {
                        parser.nextToken();
                        JsonReader.expectArrayStart(parser);
                        while (!JsonReader.isArrayEnd(parser)) {
                            Meta meta;

                            meta = Meta.Reader.read(parser);
                            metaList.add(meta);
                        }
                        parser.nextToken();
                        return metaList;
                    } catch (JsonReadException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (JsonParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }
        });
    }

    // ok
    public Meta moveRecursivelyByPath(String rootId, String path, String toPath) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/move";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "to_path", toPath };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta rollbackFileByPath(String rootId, String path, long toVersion) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/rollback";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "to_version",
                Long.toString(toVersion) };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // TODO: not checked
    public Meta thunderUploadFileByPath(String rootId, String path, String md5, long bytes, Time clientMtime)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/thunder_upload";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "md5", md5, "bytes",
                Long.toString(bytes), "modified_at_millis", "" + clientMtime.millis };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta utimeFolderByPath(String rootId, String path, Time clientMtime) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/utime_folder";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path, "modified_at_millis",
                "" + clientMtime.millis };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // ok
    public Meta setPermissionByPath(String rootId, String path, Permission permission) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/set_permission";
        String[] params = { "token", this.accessToken, "root_id", rootId, "path", path };
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));
        headers.add(new HttpRequestor.Header("Accept", "application/json"));

        JsonBuilder jb = new JsonBuilder();
        jb.addBoolean("insertable_to_owner", permission.insertableToOwner);
        jb.addBoolean("readable_to_owner", permission.readableToOwner);
        jb.addBoolean("writable_to_owner", permission.writableToOwner);
        jb.addBoolean("deletable_to_owner", permission.deletableToOwner);
        jb.addBoolean("insertable_to_others", permission.insertableToOthers);
        jb.addBoolean("readable_to_others", permission.readableToOthers);
        jb.addBoolean("writable_to_others", permission.writableToOthers);
        jb.addBoolean("deletable_to_others", permission.deletableToOthers);
        String body = jb.makeJsonString();

        return doPost(host, apiPath, params, headers, body, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });

    }

    // ok
    public Meta listPermissionsForRoot(String rootId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/fileops/list_permissions";
        String[] params = { "token", this.accessToken, "root_id", rootId };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {

            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
            }
        });
    }

    // TODO: 404
    public Delta deltaForRoot(String rootId, String cursorId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/delta";
        String[] params = { "token", this.accessToken, "root_id", rootId };
        // , "cursor_id", cursorId
        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Delta>() {

            @Override
            public Delta handle(Response response) throws BMYException {
                System.out.println(response.statusCode);
                if (response.statusCode != 200) {

                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Delta.Reader, response.body);
            }
        });
    }

    // ok
    public Trash getTrash(String rootId, String trashId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/trashes/" + trashId;
        String[] params = { "token", this.accessToken };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Trash>() {

            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<Trash> listTrashesForRoot(String rootId, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/trashes";
        String[] params = { "token", this.accessToken, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Trash>>() {

            @Override
            public ResultList<Trash> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Trash>(Trash.Reader), response.body);
            }
        });
    }

    // ok
    public Trash deleteTrash(String rootId, String trashId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/trashes/" + trashId;
        String[] params = { "token", this.accessToken };

        return doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Trash>() {

            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.Reader, response.body);
            }
        });
    }

    // ok
    public void deleteTrashesForRoot(String rootId) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/trashes/";
        String[] params = { "token", this.accessToken };

        doDelete(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<Trash>() {

            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.Reader, response.body);
            }
        });
    }

    // ok
    public Trash restoreTrashRecursively(String rootId, String trashId, String toPath) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/trashes/" + trashId + "/restore";
        String[] params = { "token", this.accessToken, "to_path", toPath };

        return doPost(host, apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Trash>() {

            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.Reader, response.body);
            }
        });
    }

    // ok
    public ResultList<User> searchUsers(String query, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/users";
        String[] params = { "token", this.accessToken, "query", query, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {

            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<User> searchUsersForGroup(String groupId, String query, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/users";
        String[] params = { "token", this.accessToken, "query", query, "group_id", groupId, "offset", "" + offset,
                "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {

            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Group> searchGroups(String query, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/groups";
        String[] params = { "token", this.accessToken, "query", query, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {

            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Group> searchGroupsForUser(String userId, String query, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/groups";
        String[] params = { "token", this.accessToken, "query", query, "user_id", userId, "offset", "" + offset,
                "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {

            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Meta> searchFiles(String query, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/files";
        String[] params = { "token", this.accessToken, "query", query, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {

            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Meta> searchFilesForRoot(String rootId, String query, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/files";
        String[] params = { "token", this.accessToken, "query", query, "root_id", rootId, "offset", "" + offset,
                "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {

            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Meta> searchFilesForMeta(String rootId, String path, String query, Integer offset, Integer limit)
            throws BMYException {
        String host = this.host.api;
        String apiPath = "1/search/files";
        String[] params = { "token", this.accessToken, "query", query, "path", path, "offset", "" + offset, "limit",
                "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {

            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<User> topUsers(String orderBy, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/top/users";
        String[] params = { "token", this.accessToken, "order_by", orderBy, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {

            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Group> topGroups(String orderBy, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/top/groups";
        String[] params = { "token", this.accessToken, "order_by", orderBy, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {

            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.Reader), response.body);
            }
        });
    }

    // ok
    public ResultList<Meta> topFiles(String orderBy, Integer offset, Integer limit) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/top/files";
        String[] params = { "token", this.accessToken, "order_by", orderBy, "offset", "" + offset, "limit", "" + limit };

        return doGet(host, apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {

            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.Reader), response.body);
            }
        });
    }

    public Uploader startUploadFile(String rootId, String path, long numBytes, Time clientMtime, Boolean overwrite)
            throws BMYException {
        if (numBytes < 0) {
            if (numBytes != -1) {
                throw new IllegalArgumentException("numBytes must be -1 or greater; given " + numBytes);
            }
            return startUploadFileChunked(rootId, path, numBytes, clientMtime, overwrite);
        } else if (numBytes > ChunkedUploadThreshold) {
            return startUploadFileChunked(rootId, path, numBytes, clientMtime, overwrite);
        } else {
            return startUploadFileSingle(rootId, path, numBytes, clientMtime, overwrite);
        }
    }

    public <E extends Throwable> Meta finishUploadFile(Uploader uploader, BMYStreamWriter<E> writer)
            throws BMYException, E {
        NoThrowOutputStream streamWrapper = new NoThrowOutputStream(uploader.getBody());
        try {
            writer.write(streamWrapper);
            return uploader.finish();
        } catch (NoThrowOutputStream.HiddenException e) {
            throw new BMYException.NetworkIO(e.underlying);
        } finally {
            uploader.close();
        }
    }

    public Uploader startUploadFileSingle(String rootId, String path, long numBytes, Time clientMtime, Boolean overwrite)
            throws BMYException {
        if (numBytes < 0)
            throw new IllegalArgumentException("numBytes must be zero or greater");

        String host = this.host.api;
        String apiPath = "1/roots/" + rootId + "/files/p/" + path;
        String[] params = { "token", this.accessToken, "modified_at_millis", "" + clientMtime.millis, "overwrite",
                "" + overwrite };

        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        headers.add(new HttpRequestor.Header("Content-Length", Long.toString(numBytes)));

        HttpRequestor.Uploader uploader = BMYRequestUtil.getUploaderWithPut(requestConfig, host, apiPath, params,
                headers);
        return new SingleUploader(uploader, numBytes);
    }

    // ok
    public <E extends Throwable> Meta uploadFileSingle(String rootId, String path, long numBytes, Time clientMtime,
            Boolean overwrite, BMYStreamWriter<E> writer) throws BMYException, E {
        Uploader uploader = startUploadFileSingle(rootId, path, numBytes, clientMtime, overwrite);
        return finishUploadFile(uploader, writer);
    }

    public static class SingleUploader extends Uploader {

        private HttpRequestor.Uploader httpUploader;
        private final long claimedBytes;
        private final CountingOutputStream body;

        public SingleUploader(HttpRequestor.Uploader httpUploader, long claimedBytes) {
            if (claimedBytes < 0) {
                throw new IllegalArgumentException("'numBytes' must be greater than or equal to 0");
            }

            this.httpUploader = httpUploader;
            this.claimedBytes = claimedBytes;
            this.body = new CountingOutputStream(httpUploader.body);
        }

        @Override
        public OutputStream getBody() {
            return this.body;
        }

        @Override
        public void abort() {
            if (httpUploader == null) {
                throw new IllegalStateException("already called 'finish', 'abort', or 'close'");
            }
            HttpRequestor.Uploader p = httpUploader;
            httpUploader = null;
            p.abort();
        }

        @Override
        public void close() {
            if (httpUploader == null)
                return;

            abort();
        }

        @Override
        public Meta finish() throws BMYException {
            if (httpUploader == null) {
                throw new IllegalStateException("already called 'finish', 'abort', or 'close'");
            }

            HttpRequestor.Uploader u = httpUploader;
            httpUploader = null;

            HttpRequestor.Response response;
            final long bytesWritten;
            try {
                bytesWritten = this.body.getBytesWritten();

                if (claimedBytes != bytesWritten) {
                    u.abort();
                    throw new IllegalStateException("You said you were going to upload " + claimedBytes
                            + " bytes, but you wrote " + bytesWritten + " bytes to the Uploader's 'body' stream.");
                }

                response = u.finish();
            } catch (IOException ex) {
                throw new BMYException.NetworkIO(ex);
            } finally {
                u.close();
            }

            return BMYRequestUtil.finishResponse(response, new BMYRequestUtil.ResponseHandler<Meta>() {
                @Override
                public Meta handle(HttpRequestor.Response response) throws BMYException {
                    System.out.println("statusCode: " + response.statusCode);
                    if (response.statusCode != 200)
                        throw BMYRequestUtil.unexpectedStatus(response);
                    Meta entry = BMYRequestUtil.readJsonFromResponse(Meta.Reader, response.body);
                    if (entry.isDir) {
                        throw new BMYException.BadResponse(
                                "uploaded file, but server returned metadata entry for a folder");
                    }
                    Meta f = entry;
                    if (f.size.bytes != bytesWritten) {
                        throw new BMYException.BadResponse("we uploaded " + bytesWritten
                                + ", but server returned metadata entry with file size " + f.size);
                    }
                    return f;
                }
            });
        }
    }

    // ok
    private <E extends Throwable> HttpRequestor.Response chunkedUploadCommon(String rootId, String[] params,
            long chunkSize, BMYStreamWriter<E> writer) throws BMYException, E {

        String host = this.host.api;
        String apiPath = "1/chunked_upload";
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        headers.add(new HttpRequestor.Header("Content-Length", Long.toString(chunkSize)));
        for (int i = 0; i < params.length; i++) {
            System.out.print(params[i] + "  ");
        }
        System.out.println();
        HttpRequestor.Uploader uploader = BMYRequestUtil.getUploaderWithPost(requestConfig, host, apiPath, params,
                headers);

        try {
            try {
                NoThrowOutputStream nt = new NoThrowOutputStream(uploader.body);
                writer.write(nt);
                long bytesWritten = nt.getBytesWritten();
                if (bytesWritten != chunkSize) {
                    throw new IllegalStateException("'chunkSize' is " + chunkSize + ", but 'writer' only wrote "
                            + bytesWritten + " bytes");
                }
                Response response = uploader.finish();
                System.out.println(response.statusCode);
                return response;
            } catch (IOException e) {
                throw new BMYException.NetworkIO(e);
            } catch (NoThrowOutputStream.HiddenException e) {
                throw new BMYException.NetworkIO(e.underlying);
            }

        } finally {
            uploader.close();
        }
    }

    private ChunkedUpload chunkedUploadParse200(HttpRequestor.Response response) throws BMYException.BadResponse,
            BMYException.NetworkIO {
        assert response.statusCode == 200 : response.statusCode;
        return BMYRequestUtil.readJsonFromResponse(ChunkedUpload.Reader, response.body);
    }

    public String chunkedUploadFirst(String rootId, byte[] data) throws BMYException {
        return chunkedUploadFirst(rootId, data, 0, data.length);
    }

    public String chunkedUploadFirst(String rootId, byte[] data, int dataOffset, int dataLength) throws BMYException {
        return chunkedUploadFirst(rootId, dataLength, new BMYStreamWriter.ByteArrayCopier(data, dataOffset, dataLength));
    }

    public <E extends Throwable> String chunkedUploadFirst(String rootId, int chunkSize, BMYStreamWriter<E> writer)
            throws BMYException, E {
        String[] params = { "token", this.accessToken };
        HttpRequestor.Response response = chunkedUploadCommon(rootId, params, chunkSize, writer);
        try {
            if (response.statusCode != 200) {
                System.out.println(response.statusCode);
                throw BMYRequestUtil.unexpectedStatus(response);
            }

            ChunkedUpload returnedState = chunkedUploadParse200(response);
            // System.out.println(returnedState.uploadId + " " +
            // returnedState.expiresString + " " + returnedState.offsetString +
            // " " + returnedState.offsetString + " " +
            // returnedState.sizeString);
            if (returnedState.offset != chunkSize) {
                throw new BMYException.BadResponse("Sent " + chunkSize + " bytes, but returned offset is "
                        + returnedState.offset);
            }
            return returnedState.id;
        } finally {
            IOUtil.closeInput(response.body);
        }
    }

    public long chunkedUploadAppend(String rootId, String uploadId, long uploadOffset, byte[] data) throws BMYException {
        return chunkedUploadAppend(rootId, uploadId, uploadOffset, data, 0, data.length);
    }

    public long chunkedUploadAppend(String rootId, String uploadId, long uploadOffset, byte[] data, int dataOffset,
            int dataLength) throws BMYException {
        return chunkedUploadAppend(rootId, uploadId, uploadOffset, dataLength, new BMYStreamWriter.ByteArrayCopier(
                data, dataOffset, dataLength));
    }

    /* return -1 when correct, which is strange but same as dropbox */
    public <E extends Throwable> long chunkedUploadAppend(String rootId, String uploadId, long uploadOffset,
            long chunkSize, BMYStreamWriter<E> writer) throws BMYException, E {
        if (uploadId == null)
            throw new IllegalArgumentException("'uploadId' can't be null");
        if (uploadId.length() == 0)
            throw new IllegalArgumentException("'uploadId' can't be empty");
        if (uploadOffset < 0)
            throw new IllegalArgumentException("'offset' can't be negative");

        String[] params = { "token", this.accessToken, "upload_id", uploadId, "offset", Long.toString(uploadOffset) };

        HttpRequestor.Response response = chunkedUploadCommon(rootId, params, chunkSize, writer);
        try {
            if (response.statusCode != 200)
                throw BMYRequestUtil.unexpectedStatus(response);
            ChunkedUpload returnedState = chunkedUploadParse200(response);
            // System.out.println(returnedState.uploadId + " " +
            // Long.toString(returnedState.expires) + " " +
            // Long.toString(returnedState.offset) + " " +
            // returnedState.sizeString);
            long expectedOffset = uploadOffset + chunkSize;
            if (returnedState.offset != expectedOffset) {
                throw new BMYException.BadResponse("Expected offset " + expectedOffset
                        + " bytes, but returned offset is " + returnedState.offset);
            }
            return -1;
        } finally {
            IOUtil.closeInput(response.body);
        }
    }

    public Uploader startUploadFileChunked(String rootId, String path, long numBytes, Time clientMtime,
            Boolean overwrite) {
        return startUploadFileChunked(rootId, path, (int) ChunkedUploadChunkSize, numBytes, clientMtime, overwrite);
    }

    public Uploader startUploadFileChunked(String rootId, String path, int chunkSize, long numBytes, Time clientMtime,
            Boolean overwrite) {
        Uploader uploader = new ChunkedUploader(rootId, path, numBytes, clientMtime, overwrite,
                new ChunkedUploadOutputStream(rootId, chunkSize));
        return uploader;

    }

    // ok
    public <E extends Throwable> Meta uploadFileChunked(String rootId, String path, long numBytes, Time clientMtime,
            Boolean overwrite, BMYStreamWriter<E> writer) throws BMYException, E {
        Uploader uploader = startUploadFileChunked(rootId, path, numBytes, clientMtime, overwrite);
        return finishUploadFile(uploader, writer);
    }

    // ok
    public <E extends Throwable> Meta uploadFileChunked(String rootId, String path, int chunkSize, long numBytes,
            Time clientMtime, Boolean overwrite, BMYStreamWriter<E> writer) throws BMYException, E {
        Uploader uploader = startUploadFileChunked(rootId, path, chunkSize, numBytes, clientMtime, overwrite);
        return finishUploadFile(uploader, writer);
    }

    public final class ChunkedUploader extends Uploader {
        private final String rootId;
        private final String path;
        private final long numBytes;
        private final Time clientMtime;
        private final Boolean overwrite;
        private final ChunkedUploadOutputStream body;

        private ChunkedUploader(String rootId, String path, long numBytes, Time clientMtime, Boolean overwrite,
                ChunkedUploadOutputStream body) {
            this.rootId = rootId;
            this.path = path;
            this.numBytes = numBytes;
            this.clientMtime = clientMtime;
            this.overwrite = overwrite;
            this.body = body;
        }

        @Override
        public OutputStream getBody() {
            return body;
        }

        @Override
        public void abort() {
            // TODO: figure out how to abort chunked uploads
        }

        @Override
        public Meta finish() throws BMYException {
            if (body.uploadId == null) {
                return uploadFileSingle(rootId, path, numBytes, clientMtime, overwrite,
                        new BMYStreamWriter.ByteArrayCopier(body.chunk, 0, body.chunkPos));

            } else {
                body.finishChunk();
                if (numBytes != -1) {
                    // Make sure the number of bytes they sent matches what they
                    // said
                    // they'd send.
                    if (numBytes != body.uploadOffset) {
                        throw new IllegalStateException("'numBytes' is " + numBytes + " but you wrote "
                                + body.uploadOffset + " bytes");
                    }
                }

                return BMYRequestUtil.runAndRetry(3, new BMYRequestUtil.RequestMaker<Meta, RuntimeException>() {
                    public Meta run() throws BMYException {
                        return commitChunkedUploadByPath(rootId, path, body.uploadId, clientMtime);

                    }
                });
            }
        }

        @Override
        public void close() {
            // TODO: firgure out how to abort chunked uploads
        }
    }

    private final class ChunkedUploadOutputStream extends OutputStream {
        private final String rootId;
        private final byte[] chunk;
        private int chunkPos = 0;
        private String uploadId;
        private long uploadOffset;

        private ChunkedUploadOutputStream(String rootId, int chunkSize) {
            this.rootId = rootId;
            this.chunk = new byte[chunkSize];
            this.chunkPos = 0;
        }

        @Override
        public void write(int i) throws IOException {
            chunk[chunkPos++] = (byte) i;
            try {
                finishChunkIfNecessary();
            } catch (BMYException e) {
                throw new IOBMYException(e);
            }
        }

        private void finishChunkIfNecessary() throws BMYException {
            assert chunkPos <= chunk.length;
            if (chunkPos == chunk.length) {
                finishChunk();
            }
        }

        private void finishChunk() throws BMYException {
            if (chunkPos == 0)
                return;
            if (uploadId == null) {
                uploadId = BMYRequestUtil.runAndRetry(3, new BMYRequestUtil.RequestMaker<String, RuntimeException>() {

                    @Override
                    public String run() throws BMYException, RuntimeException {
                        return chunkedUploadFirst(rootId, chunk, 0, chunkPos);
                    }
                });
                uploadOffset = chunkPos;
            } else {
                int arrayOffset = 0;
                while (true) {
                    final int arrayOffsetFinal = arrayOffset;
                    long correctedOffset = BMYRequestUtil.runAndRetry(3,
                            new BMYRequestUtil.RequestMaker<Long, RuntimeException>() {
                                @Override
                                public Long run() throws BMYException, RuntimeException {
                                    return chunkedUploadAppend(rootId, uploadId, uploadOffset, chunk, arrayOffsetFinal,
                                            chunkPos - arrayOffsetFinal);
                                }
                            });
                    long expectedOffset = uploadOffset + chunkPos;
                    if (correctedOffset == -1) {
                        // Everthing ok
                        uploadOffset = expectedOffset;
                        break;
                    } else {/* the following code never get executed by BMY */
                        // Make sure the returned offset is within what we
                        // expect.
                        assert correctedOffset != expectedOffset;
                        if (correctedOffset < uploadOffset) {
                            // Somehow the server lost track of the previous
                            // data we sent it.
                            throw new BMYException.BadResponse("we were at offset " + uploadOffset + ", server said "
                                    + correctedOffset);
                        } else if (correctedOffset > expectedOffset) {
                            // Somehow the server has more data than we gave it!
                            throw new BMYException.BadResponse("we were at offset " + uploadOffset + ", server said "
                                    + correctedOffset);
                        }
                        // Server needs us to resend partial data.
                        int adjustAmount = (int) (correctedOffset - uploadOffset);
                        arrayOffset += adjustAmount;
                    }
                }
            }
            chunkPos = 0;
        }

        @Override
        public void write(byte[] bytes, int offset, int length) throws IOException {
            int inputEnd = offset + length;
            int inputPos = offset;
            while (inputPos < inputEnd) {
                int spaceInChunk = chunk.length - chunkPos;
                int leftToWrite = inputEnd - inputPos;
                int bytesToCopy = Math.min(leftToWrite, spaceInChunk);
                System.arraycopy(bytes, inputPos, chunk, chunkPos, bytesToCopy);
                chunkPos += bytesToCopy;
                inputPos += bytesToCopy;
                try {
                    finishChunkIfNecessary();
                } catch (BMYException ex) {
                    throw new IOBMYException(ex);
                }
            }
        }

        @Override
        public void close() throws IOException {

        }
    }

    public static final class IOBMYException extends IOException {
        private static final long serialVersionUID = 1L;
        public final BMYException underlying;

        public IOBMYException(BMYException underlying) {
            super(underlying);
            this.underlying = underlying;
        }
    }

    // ok

    public static abstract class Uploader {
        public abstract OutputStream getBody();

        public abstract void abort();

        public abstract void close();

        public abstract Meta finish() throws BMYException;
    }

    public static final class Downloader {
        public final Meta meta;
        public final InputStream body;

        public Downloader(Meta meta, InputStream body) {
            this.meta = meta;
            this.body = body;
        }

        Meta copyBodyAndClose(OutputStream target) throws BMYException, IOException {
            try {
                IOUtil.copyStreamToStream(this.body, target);
            } catch (IOUtil.ReadException ex) {
                throw new BMYException.NetworkIO(ex.underlying);
            } catch (IOUtil.WriteException ex) {
                throw ex.underlying;
            } finally {
                this.close();
            }
            return this.meta;
        }

        public void close() {
            IOUtil.closeInput(body);
        }
    }

    // ========================================================

    private <T> T doDelete(String host, String path, String[] params, ArrayList<HttpRequestor.Header> headers,
            BMYRequestUtil.ResponseHandler<T> handler) throws BMYException {
        return BMYRequestUtil.doDelete(requestConfig, host, path, params, headers, handler);
    }

    private <T> T doGet(String host, String path, String[] params, ArrayList<HttpRequestor.Header> headers,
            BMYRequestUtil.ResponseHandler<T> handler) throws BMYException {
        return BMYRequestUtil.doGet(requestConfig, host, path, params, headers, handler);
    }

    private <T> T doPost(String host, String path, String[] params, ArrayList<HttpRequestor.Header> headers,
            String body,/* json body */
            BMYRequestUtil.ResponseHandler<T> handler) throws BMYException {

        return BMYRequestUtil.doPost(requestConfig, host, path, params, body, headers, handler);
    }
}