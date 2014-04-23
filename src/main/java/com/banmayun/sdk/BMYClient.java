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
import com.banmayun.sdk.core.Link;
import com.banmayun.sdk.core.Meta;
import com.banmayun.sdk.core.Permission;
import com.banmayun.sdk.core.Relation;
import com.banmayun.sdk.core.ResultList;
import com.banmayun.sdk.core.Revision;
import com.banmayun.sdk.core.Root;
import com.banmayun.sdk.core.Share;
import com.banmayun.sdk.core.Time;
import com.banmayun.sdk.core.Trash;
import com.banmayun.sdk.core.User;
import com.banmayun.sdk.http.HttpRequestor;
import com.banmayun.sdk.http.HttpRequestor.Response;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.CountingOutputStream;
import com.banmayun.sdk.util.IOUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

/* BMYClient Version 1*/
public class BMYClient {

    private static final long CHUNKED_UPLOAD_THRESHOLD = 64 * 1024 * 1024;
    private static final long CHUNKED_UPLOAD_CHUNK_SIZE = 4 * 1024 * 1024;

    private BMYRequestConfig requestConfig = null;
    private String token = null;
    private BMYHost host = null;

    public enum ThumbnailFormat {
        JPEG,
        PNG
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
        XS,
        S,
        M,
        L,
        XL
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

    public String inputStream2String(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
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
        String inputStr = inputStream2String(input);
        return inputStr.length();
    }

    public BMYClient(BMYRequestConfig requestConfig) {
        this(requestConfig, null);
    }

    public BMYClient(BMYRequestConfig requestConfig, String accessToken) {
        this(requestConfig, accessToken, BMYHost.DEFAULT);
    }

    public BMYClient(BMYRequestConfig requestConfig, String accessToken, BMYHost host) {
        if (requestConfig == null) {
            throw new IllegalArgumentException("'requestConfig' is null");
        }
        if (host == null) {
            throw new IllegalArgumentException("'host' is null");
        }

        this.requestConfig = requestConfig;
        this.token = accessToken;
        this.host = host;
    }

    public BMYRequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public Link signInUser(String userName, String password, String linkName, String linkDevice, String ldapName)
            throws BMYException {
        String apiPath = "1/auth/sign_in";
        String[] params = { "username", userName, "password", password, "link_name", linkName, "link_device",
                linkDevice };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Link>() {
            @Override
            public Link handle(Response response) throws BMYException {
                System.out.println(response.statusCode);
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Link.reader, response.body);
            }
        });
    }

    public Link signOutUser() throws BMYException {
        String apiPath = "1/auth/sign_out";

        return this.doPost(apiPath, null, null, null, new BMYRequestUtil.ResponseHandler<Link>() {
            @Override
            public Link handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Link.reader, response.body);
            }
        });
    }

    public User forgotUserPassword(String email) throws BMYException {
        String apiPath = "1/auth/forgot_password";
        String[] params = { "email", email };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User changeUserPassword(String userName, String oldPassword, String newPassword) throws BMYException {
        String apiPath = "1/auth/change_password";
        String[] params = { "username", userName, "password", oldPassword, "new_password", newPassword };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User existsUser(User user) throws BMYException {
        String apiPath = "1/users/exists";
        String body = user.objectToJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User createUser(User user, String password) throws BMYException {
        String apiPath = "1/users";
        String[] params = { "password", password };
        String body = user.objectToJsonString();

        return this.doPost(apiPath, params, null, body, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User getUser(String targetUserId) throws BMYException {
        String apiPath = "1/users/" + targetUserId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public ResultList<User> listUsers(String role, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/users";
        List<String> paramList = getPaginationParams(offset, limit);
        if (role != null) {
            paramList.add("role");
            paramList.add(role);
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {
            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.reader), response.body);
            }
        });
    }

    public User updateUser(String targetUserId, User update) throws BMYException {
        String apiPath = "1/users" + "/" + targetUserId + "/update";
        String body = update.objectToJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User setUserPassword(String targetUserId, String newPassword) throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/password";
        String[] params = { "new_password", newPassword };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public void setUserAvatar(String targetUserId, InputStream input) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/users/" + targetUserId + "/avatar";
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        String url = BMYRequestUtil.buildUrlWithParams(host, apiPath, this.token, requestConfig.userLocale, null);
        System.out.println(url);
        headers = BMYRequestUtil.addUserAgentHeader(headers, requestConfig);
        // TODO: what? think before code!
        headers.add(new HttpRequestor.Header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));

        try {
            HttpRequestor.Uploader uploader = requestConfig.httpRequestor.startPost(url, headers);
            try {
                byte[] buf = new byte[1024];
                while (true) {
                    int r = input.read(buf);
                    if (r == -1) {
                        break;
                    }
                    uploader.getBody().write(buf, 0, r);
                }
                uploader.finish();
            } finally {
                uploader.close();
            }
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public InputStream getUserAvatar(String targetUserId, ThumbnailFormat format, ThumbnailSize size)
            throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/avatar";
        // TODO: format and size is optional! see api doc!!!!!!!
        String[] params = { "format", getThumbnailFormatStr(format), "size", getThumbnailSizeStr(size) };

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<InputStream>() {
            @Override
            public InputStream handle(Response response) throws BMYException {
                System.out.println(response.statusCode);
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return response.body;
            }
        });
    }

    public Group addUserGroup(String targetUserId, String groupId, Relation relation) throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/groups";
        String[] params = { "group_id", groupId };
        String body = relation.toJsonString();

        return this.doPost(apiPath, params, null, body, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public Group getUserGroup(String targetUserId, String groupId) throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/groups/" + groupId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public ResultList<Group> listGroupsForUser(String targetUserId, String role, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/groups";
        List<String> paramList = getPaginationParams(offset, limit);
        if (role != null) {
            paramList.add("role");
            paramList.add(role);
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {
            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.reader), response.body);
            }
        });
    }

    public Group updateUserGroup(String targetUserId, String groupId, Relation update) throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/groups/" + groupId + "/update";
        String body = update.toJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public Group removeUserGroup(String targetUserId, String groupId) throws BMYException {
        String apiPath = "1/users/" + targetUserId + "/groups/" + groupId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public Group existsGroup(Group group) throws BMYException {
        String apiPath = "1/groups/exists";
        String body = group.toJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public Group createGroup(Group group, String ownerId) throws BMYException {
        String apiPath = "1/groups";
        String body = group.toJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public Group getGroup(String groupId) throws BMYException {
        String apiPath = "1/groups/" + groupId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public ResultList<Group> listGroups(String type, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/groups";
        List<String> paramList = getPaginationParams(offset, limit);
        if (type != null) {
            paramList.add("type");
            paramList.add(type);
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {
            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.reader), response.body);
            }
        });
    }

    public Group updateGroup(String groupId, Group update) throws BMYException {
        String apiPath = "1/groups/" + groupId + "/update";
        String body = update.toJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public Group deleteGroup(String groupId) throws BMYException {
        String apiPath = "1/groups/" + groupId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Group>() {
            @Override
            public Group handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Group.reader, response.body);
            }
        });
    }

    public void setGroupLogo(String groupId, InputStream input) throws BMYException {
        String host = this.host.api;
        String apiPath = "1/groups/" + groupId + "/logo";
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        String url = BMYRequestUtil.buildUrlWithParams(host, apiPath, this.token, requestConfig.userLocale, null);
        System.out.println(url);
        // TODO: again
        headers = BMYRequestUtil.addUserAgentHeader(headers, requestConfig);

        try {
            HttpRequestor.Uploader uploader = requestConfig.httpRequestor.startPost(url, headers);
            try {
                byte[] buf = new byte[1024];
                while (true) {
                    int r = input.read(buf);
                    if (r == -1) {
                        break;
                    }
                    uploader.getBody().write(buf, 0, r);
                }
                uploader.finish();
            } finally {
                uploader.close();
            }
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public InputStream getGroupLogo(String groupId) throws BMYException {
        String apiPath = "1/groups/" + groupId + "/logo";

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<InputStream>() {
            @Override
            public InputStream handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return response.body;
            }
        });
    }

    public User addGroupUser(String groupId, String targetUserId, Relation relation) throws BMYException {
        String apiPath = "1/groups/" + groupId + "/users";
        String[] params = { "user_id", targetUserId };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User getGroupUser(String groupId, String targetUserId) throws BMYException {
        String apiPath = "1/groups/" + groupId + "/users/" + targetUserId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public ResultList<User> listUsersForGroup(String groupId, String role, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/groups/" + groupId + "/users";
        List<String> paramList = getPaginationParams(offset, limit);
        if (role != null) {
            paramList.add("role");
            paramList.add(role);
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {
            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.reader), response.body);
            }
        });
    }

    public User updateGroupUser(String groupId, String targetUserId, Relation update) throws BMYException {
        String apiPath = "1/groups/" + groupId + "/users/" + targetUserId + "/update";
        String body = update.toJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public User removeGroupUser(String groupId, String targetUserId) throws BMYException {
        String apiPath = "1/groups/" + groupId + "/users/" + targetUserId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<User>() {
            @Override
            public User handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(User.reader, response.body);
            }
        });
    }

    public Root getRoot(String rootId) throws BMYException {
        String apiPath = "1/roots/" + rootId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Root>() {
            @Override
            public Root handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Root.reader, response.body);
            }
        });
    }

    public Root setRootDefaultPermission(String rootId, Permission defaultPermission) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/default_permission";
        String body = defaultPermission.objectToJsonObject();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<Root>() {
            @Override
            public Root handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Root.reader, response.body);
            }
        });
    }

    public Root setRootQuota(String rootId, String quota) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/quota";
        String[] params = { "quota", quota };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Root>() {
            @Override
            public Root handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Root.reader, response.body);
            }
        });
    }

    public <E extends Throwable> Meta uploadFileByPath(String rootId, String path, long modifiedAtMillis,
            Boolean overwrite, InputStream input, long numBytes) throws Throwable {
        BMYStreamWriter<?> writer = new BMYStreamWriter.InputStreamCopier(input);
        Uploader uploader = startUploadFile(rootId, path, numBytes, modifiedAtMillis, overwrite);
        return finishUploadFile(uploader, writer);
    }

    // TODO: 500
    public InputStream getFileByPath(String rootId, String path, Long version, Long offset, Long bytes)
            throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/p/" + path;
        List<String> paramList = new ArrayList<String>();
        if (version != null) {
            paramList.add("version");
            paramList.add(String.valueOf(version));
        }
        if (offset != null) {
            paramList.add("offset");
            paramList.add(String.valueOf(offset));
        }
        if (bytes != null) {
            paramList.add("bytes");
            paramList.add(String.valueOf(bytes));
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<InputStream>() {
            @Override
            public InputStream handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return response.body;
            }
        });
    }

    public Meta trashRecursivelyByPath(String rootId, String path) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/p/" + path;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta uploadFile(String rootId, String metaId, Time clientMtime, InputStream input) throws BMYException {
        // TODO:
        return null;
    }

    // TODO: return downloader?
    // TODO: 500
    public InputStream getFile(String rootId, String metaId, Long version, Long offset, Long bytes) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId;
        List<String> paramList = new ArrayList<String>();
        if (version != null) {
            paramList.add("version");
            paramList.add(String.valueOf(version));
        }
        if (offset != null) {
            paramList.add("offset");
            paramList.add(String.valueOf(offset));
        }
        if (bytes != null) {
            paramList.add("bytes");
            paramList.add(String.valueOf(bytes));
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<InputStream>() {
            @Override
            public InputStream handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return response.body;
            }
        });
    }

    public Meta trashRecursively(String rootId, String metaId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta getMeta(String rootId, String metaId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/meta";

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    // TODO: return downloader?
    // TODO: 500
    public InputStream getFileThumbnail(String rootId, String metaId, ThumbnailFormat format, ThumbnailSize size)
            throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/thumbnail";
        // TODO: format and size is optional
        String[] params = { "format", getThumbnailFormatStr(format), "size", getThumbnailSizeStr(size) };

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<InputStream>() {
            @Override
            public InputStream handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return response.body;
            }
        });
    }

    public ResultList<Revision> listRevisionsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/revisions";
        List<String> paramList = getPaginationParams(offset, limit);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Revision>>() {
            @Override
            public ResultList<Revision> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Revision>(Revision.reader),
                        response.body);
            }
        });
    }

    public Comment createComment(String rootId, String metaId, Comment comment) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments";
        String body = comment.toJsonString();

        return this.doPost(apiPath, null, null, body, new BMYRequestUtil.ResponseHandler<Comment>() {
            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.reader, response.body);
            }
        });
    }

    public Comment getComment(String rootId, String metaId, String commentId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments/" + commentId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Comment>() {
            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.reader, response.body);
            }
        });
    }

    public ResultList<Comment> listComments(Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/roots/all/files/all/comments";
        List<String> paramList = getPaginationParams(offset, limit);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Comment>>() {
            @Override
            public ResultList<Comment> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Comment>(Comment.reader),
                        response.body);
            }
        });
    }

    public ResultList<Comment> listCommentsForRoot(String rootId, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/all/comments";
        // TODO:
        String[] params = { "offset", Integer.toString(offset), "limit", Integer.toString(limit) };

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Comment>>() {
            @Override
            public ResultList<Comment> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Comment>(Comment.reader),
                        response.body);
            }
        });
    }

    public ResultList<Comment> listCommentsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments";
        List<String> paramList = getPaginationParams(offset, limit);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Comment>>() {
            @Override
            public ResultList<Comment> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Comment>(Comment.reader),
                        response.body);
            }
        });
    }

    public Comment deleteComment(String rootId, String metaId, String commentId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments/" + commentId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Comment>() {
            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Comment.reader, response.body);
            }
        });
    }

    public void deleteComments() throws BMYException {
        String apiPath = "1/roots/all/files/all/comments";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public void deleteCommentsForRoot(String rootId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/all/comments";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public void deleteCommentsForMeta(String rootId, String metaId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/comments";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Comment handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public Share createShare(String rootId, String metaId, String password, Long expiresAtMillis) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares";
        // TODO: expiresAtMillis is optional
        String[] params = { "password", password, "expires_at_millis", String.valueOf(expiresAtMillis) };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Share>() {
            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.reader, response.body);
            }
        });
    }

    public Share getShare(String rootId, String metaId, String shareId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares/" + shareId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Share>() {
            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.reader, response.body);
            }
        });
    }

    public ResultList<Share> listShares(Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/roots/all/files/all/shares";

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<ResultList<Share>>() {
            @Override
            public ResultList<Share> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<>(Share.reader), response.body);
            }
        });
    }

    public ResultList<Share> listSharesForRoot(String rootId, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/all/shares";

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<ResultList<Share>>() {
            @Override
            public ResultList<Share> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<>(Share.reader), response.body);
            }
        });
    }

    public ResultList<Share> listSharesForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares";

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<ResultList<Share>>() {
            @Override
            public ResultList<Share> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<>(Share.reader), response.body);
            }
        });
    }

    public Share deleteShare(String rootId, String metaId, String shareId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares/" + shareId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Share>() {
            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Share.reader, response.body);
            }
        });
    }

    public void deleteShares() throws BMYException {
        String apiPath = "1/roots/all/files/all/shares";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public void deleteSharesForRoot(String rootId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/all/shares";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public void deleteSharesForMeta(String rootId, String metaId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/files/" + metaId + "/shares";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Share handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public Meta commitChunkedUploadByPath(String rootId, String path, String uploadId, long modifiedAtMillis)
            throws BMYException {
        String apiPath = "1/fileops/commit_chunked_upload";
        String[] params = { "root_id", rootId, "path", path, "upload_id", uploadId, "modified_at_millis",
                String.valueOf(modifiedAtMillis) };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta copyRecursivelyByPath(String rootId, String path, String toPath) throws BMYException {
        String apiPath = "1/fileops/copy";
        String[] params = { "root_id", rootId, "path", path, "to_path", toPath };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta createFolderByPath(String rootId, String path, Long modifiedAtMillis) throws BMYException {
        String apiPath = "1/fileops/create_folder";
        List<String> paramList = new ArrayList<String>();
        paramList.add("root_id");
        paramList.add(rootId);
        paramList.add("path");
        paramList.add(path);
        if (modifiedAtMillis != null) {
            paramList.add("modified_at_millis");
            paramList.add(String.valueOf(modifiedAtMillis));
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta getMetaByPath(String rootId, String path, Boolean list) throws BMYException {
        String apiPath = "1/fileops/get_meta";
        List<String> paramList = new ArrayList<String>();
        paramList.add("root_id");
        paramList.add(rootId);
        paramList.add("path");
        paramList.add(path);
        if (list != null) {
            paramList.add("list");
            paramList.add(String.valueOf(list));
        }
        String[] params = paramList.toArray(new String[0]);

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public List<Meta> listFolderByPath(String rootId, String path) throws BMYException {
        String apiPath = "1/fileops/list_folder";
        String[] params = { "root_id", rootId, "path", path };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<List<Meta>>() {
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
                            metaList.add(Meta.reader.read(parser));
                        }
                        parser.nextToken();
                        return metaList;
                    } catch (JsonReadException e) {
                        // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println("JsonReadException");
                    }
                } catch (JsonParseException e) {
                    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    System.out.println("JsonParseException");
                } catch (IOException e) {
                    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    System.out.println("IOException");
                }

                return null;
            }
        });
    }

    public Meta moveRecursivelyByPath(String rootId, String path, String toPath) throws BMYException {
        String apiPath = "1/fileops/move";
        String[] params = { "root_id", rootId, "path", path, "to_path", toPath };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta rollbackFileByPath(String rootId, String path, long toVersion) throws BMYException {
        String apiPath = "1/fileops/rollback";
        String[] params = { "root_id", rootId, "path", path, "to_version", Long.toString(toVersion) };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    // TODO: not checked
    public Meta thunderUploadFileByPath(String rootId, String path, String md5, long bytes, long modifiedAtMillis)
            throws BMYException {
        String apiPath = "1/fileops/thunder_upload";
        String[] params = { "root_id", rootId, "path", path, "md5", md5, "bytes", Long.toString(bytes),
                "modified_at_millis", String.valueOf(modifiedAtMillis) };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta utimeFolderByPath(String rootId, String path, long modifiedAtMillis) throws BMYException {
        String apiPath = "1/fileops/utime_folder";
        String[] params = { "root_id", rootId, "path", path, "modified_at_millis", String.valueOf(modifiedAtMillis) };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta setPermissionByPath(String rootId, String path, Permission permission) throws BMYException {
        String apiPath = "1/fileops/set_permission";
        String[] params = { "root_id", rootId, "path", path };
        String body = permission.objectToJsonObject();

        return this.doPost(apiPath, params, null, body, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    public Meta listPermissionsForRoot(String rootId) throws BMYException {
        String apiPath = "1/fileops/list_permissions";
        String[] params = { "root_id", rootId };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Meta>() {
            @Override
            public Meta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
            }
        });
    }

    // TODO: 404
    public Delta deltaForRoot(String rootId, String cursorId) throws BMYException {
        String apiPath = "1/delta";
        String[] params = { "root_id", rootId };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Delta>() {
            @Override
            public Delta handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Delta.Reader, response.body);
            }
        });
    }

    public Trash getTrash(String rootId, String trashId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/trashes/" + trashId;

        return this.doGet(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Trash>() {
            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.reader, response.body);
            }
        });
    }

    public ResultList<Trash> listTrashesForRoot(String rootId, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/trashes";
        List<String> paramList = getPaginationParams(offset, limit);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Trash>>() {
            @Override
            public ResultList<Trash> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Trash>(Trash.reader), response.body);
            }
        });
    }

    public Trash deleteTrash(String rootId, String trashId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/trashes/" + trashId;

        return this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Trash>() {
            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.reader, response.body);
            }
        });
    }

    public void deleteTrashesForRoot(String rootId) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/trashes/";

        this.doDelete(apiPath, null, null, new BMYRequestUtil.ResponseHandler<Object>() {
            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return null;
            }
        });
    }

    public Trash restoreTrashRecursively(String rootId, String trashId, String toPath) throws BMYException {
        String apiPath = "1/roots/" + rootId + "/trashes/" + trashId + "/restore";
        // TODO: null toPath?
        String[] params = { "to_path", toPath };

        return this.doPost(apiPath, params, null, null, new BMYRequestUtil.ResponseHandler<Trash>() {
            @Override
            public Trash handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(Trash.reader, response.body);
            }
        });
    }

    public ResultList<User> searchUsers(String query, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/search/users";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("query");
        paramList.add(query);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {
            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.reader), response.body);
            }
        });
    }

    public ResultList<User> searchUsersForGroup(String groupId, String query, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/search/users";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("group_id");
        paramList.add(groupId);
        paramList.add("query");
        paramList.add(query);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {
            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.reader), response.body);
            }
        });
    }

    public ResultList<Group> searchGroups(String query, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/search/groups";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("query");
        paramList.add(query);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {
            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.reader), response.body);
            }
        });
    }

    public ResultList<Group> searchGroupsForUser(String userId, String query, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/search/groups";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("query");
        paramList.add(query);
        paramList.add("user_id");
        paramList.add(userId);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {
            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.reader), response.body);
            }
        });
    }

    public ResultList<Meta> searchFiles(String query, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/search/files";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("query");
        paramList.add(query);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {
            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.reader), response.body);
            }
        });
    }

    public ResultList<Meta> searchFilesForRoot(String rootId, String query, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/search/files";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("query");
        paramList.add(query);
        paramList.add("root_id");
        paramList.add(rootId);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {
            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.reader), response.body);
            }
        });
    }

    public ResultList<Meta> searchFilesForMeta(String rootId, String path, String query, Integer offset, Integer limit)
            throws BMYException {
        String apiPath = "1/search/files";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("query");
        paramList.add(query);
        paramList.add("root_id");
        paramList.add(rootId);
        paramList.add("path");
        paramList.add(path);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {
            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.reader), response.body);
            }
        });
    }

    public ResultList<User> topUsers(String orderBy, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/top/users";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("order_by");
        paramList.add(orderBy);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<User>>() {
            @Override
            public ResultList<User> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<User>(User.reader), response.body);
            }
        });
    }

    public ResultList<Group> topGroups(String orderBy, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/top/groups";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("order_by");
        paramList.add(orderBy);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Group>>() {
            @Override
            public ResultList<Group> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Group>(Group.reader), response.body);
            }
        });
    }

    public ResultList<Meta> topFiles(String orderBy, Integer offset, Integer limit) throws BMYException {
        String apiPath = "1/top/files";
        List<String> paramList = getPaginationParams(offset, limit);
        paramList.add("order_by");
        paramList.add(orderBy);
        String[] params = paramList.toArray(new String[0]);

        return this.doGet(apiPath, params, null, new BMYRequestUtil.ResponseHandler<ResultList<Meta>>() {
            @Override
            public ResultList<Meta> handle(Response response) throws BMYException {
                if (response.statusCode != 200) {
                    throw BMYRequestUtil.unexpectedStatus(response);
                }
                return BMYRequestUtil.readJsonFromResponse(new ResultList.Reader<Meta>(Meta.reader), response.body);
            }
        });
    }

    public Uploader startUploadFile(String rootId, String path, long numBytes, long modifiedAtMillis, Boolean overwrite)
            throws BMYException {
        if (numBytes < 0) {
            if (numBytes != -1) {
                throw new IllegalArgumentException("numBytes must be -1 or greater; given " + numBytes);
            }
            return startUploadFileChunked(rootId, path, numBytes, modifiedAtMillis, overwrite);
        } else if (numBytes > CHUNKED_UPLOAD_THRESHOLD) {
            return startUploadFileChunked(rootId, path, numBytes, modifiedAtMillis, overwrite);
        } else {
            return startUploadFileSingle(rootId, path, numBytes, modifiedAtMillis, overwrite);
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

    public Uploader startUploadFileSingle(String rootId, String path, long numBytes, long modifiedAtMillis,
            Boolean overwrite) throws BMYException {
        if (numBytes < 0) {
            throw new IllegalArgumentException("numBytes must be zero or greater");
        }

        String apiPath = "1/roots/" + rootId + "/files/p/" + path;
        List<String> paramList = new ArrayList<String>();
        paramList.add("modified_at_millis");
        paramList.add(String.valueOf(modifiedAtMillis));
        if (overwrite != null) {
            paramList.add("overwrite");
            paramList.add(String.valueOf(overwrite));
        }
        String[] params = paramList.toArray(new String[0]);

        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        headers.add(new HttpRequestor.Header("Content-Length", String.valueOf(numBytes)));

        HttpRequestor.Uploader uploader = this.getUploaderWithPut(apiPath, params, headers);
        return new SingleUploader(uploader, numBytes);
    }

    public <E extends Throwable> Meta uploadFileSingle(String rootId, String path, long numBytes,
            long modifiedAtMillis, Boolean overwrite, BMYStreamWriter<E> writer) throws BMYException, E {
        Uploader uploader = startUploadFileSingle(rootId, path, numBytes, modifiedAtMillis, overwrite);
        return finishUploadFile(uploader, writer);
    }

    public static class SingleUploader extends Uploader {
        private HttpRequestor.Uploader httpUploader;
        private long claimedBytes;
        private CountingOutputStream body;

        public SingleUploader(HttpRequestor.Uploader httpUploader, long claimedBytes) {
            if (claimedBytes < 0) {
                throw new IllegalArgumentException("'numBytes' must be greater than or equal to 0");
            }

            this.httpUploader = httpUploader;
            this.claimedBytes = claimedBytes;
            this.body = new CountingOutputStream(httpUploader.getBody());
        }

        @Override
        public OutputStream getBody() {
            return this.body;
        }

        @Override
        public void abort() {
            if (this.httpUploader == null) {
                throw new IllegalStateException("already called 'finish', 'abort', or 'close'");
            }
            HttpRequestor.Uploader p = httpUploader;
            this.httpUploader = null;
            p.abort();
        }

        @Override
        public void close() {
            if (this.httpUploader == null) {
                return;
            }

            abort();
        }

        @Override
        public Meta finish() throws BMYException {
            if (this.httpUploader == null) {
                throw new IllegalStateException("already called 'finish', 'abort', or 'close'");
            }

            HttpRequestor.Uploader u = this.httpUploader;
            this.httpUploader = null;

            HttpRequestor.Response response;
            final long bytesWritten;
            try {
                bytesWritten = this.body.getBytesWritten();

                if (this.claimedBytes != bytesWritten) {
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
                    if (response.statusCode != 200) {
                        throw BMYRequestUtil.unexpectedStatus(response);
                    }
                    Meta entry = BMYRequestUtil.readJsonFromResponse(Meta.reader, response.body);
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

    private <E extends Throwable> HttpRequestor.Response chunkedUploadCommon(String rootId, String[] params,
            long chunkSize, BMYStreamWriter<E> writer) throws BMYException, E {
        String apiPath = "1/chunked_upload";
        ArrayList<HttpRequestor.Header> headers = new ArrayList<HttpRequestor.Header>();
        headers.add(new HttpRequestor.Header("Content-Type", "application/octet-stream"));
        headers.add(new HttpRequestor.Header("Content-Length", String.valueOf(chunkSize)));

        HttpRequestor.Uploader uploader = this.getUploaderWithPost(apiPath, params, headers);

        try {
            NoThrowOutputStream nt = new NoThrowOutputStream(uploader.getBody());
            writer.write(nt);
            long bytesWritten = nt.getBytesWritten();
            if (bytesWritten != chunkSize) {
                throw new IllegalStateException("'chunkSize' is " + chunkSize + ", but 'writer' only wrote "
                        + bytesWritten + " bytes");
            }
            Response response = uploader.finish();
            return response;
        } catch (IOException e) {
            throw new BMYException.NetworkIO(e);
        } catch (NoThrowOutputStream.HiddenException e) {
            throw new BMYException.NetworkIO(e.underlying);
        } finally {
            uploader.close();
        }
    }

    private ChunkedUpload chunkedUploadParse200(HttpRequestor.Response response) throws BMYException.BadResponse,
            BMYException.NetworkIO {
        assert response.statusCode == 200 : response.statusCode;
        return BMYRequestUtil.readJsonFromResponse(ChunkedUpload.reader, response.body);
    }

    public String chunkedUploadFirst(String rootId, byte[] data) throws BMYException {
        return chunkedUploadFirst(rootId, data, 0, data.length);
    }

    public String chunkedUploadFirst(String rootId, byte[] data, int dataOffset, int dataLength) throws BMYException {
        return chunkedUploadFirst(rootId, dataLength, new BMYStreamWriter.ByteArrayCopier(data, dataOffset, dataLength));
    }

    public <E extends Throwable> String chunkedUploadFirst(String rootId, int chunkSize, BMYStreamWriter<E> writer)
            throws BMYException, E {
        HttpRequestor.Response response = chunkedUploadCommon(rootId, null, chunkSize, writer);
        try {
            if (response.statusCode != 200) {
                throw BMYRequestUtil.unexpectedStatus(response);
            }

            ChunkedUpload returnedState = chunkedUploadParse200(response);
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
        if (uploadId == null) {
            throw new IllegalArgumentException("'uploadId' can't be null");
        }
        if (uploadId.length() == 0) {
            throw new IllegalArgumentException("'uploadId' can't be empty");
        }
        if (uploadOffset < 0) {
            throw new IllegalArgumentException("'offset' can't be negative");
        }

        String[] params = { "upload_id", uploadId, "offset", Long.toString(uploadOffset) };

        HttpRequestor.Response response = chunkedUploadCommon(rootId, params, chunkSize, writer);
        try {
            if (response.statusCode != 200) {
                throw BMYRequestUtil.unexpectedStatus(response);
            }
            ChunkedUpload returnedState = chunkedUploadParse200(response);
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

    public Uploader startUploadFileChunked(String rootId, String path, long numBytes, long modifiedAtMillis,
            Boolean overwrite) {
        return startUploadFileChunked(rootId, path, (int) CHUNKED_UPLOAD_CHUNK_SIZE, numBytes, modifiedAtMillis,
                overwrite);
    }

    public Uploader startUploadFileChunked(String rootId, String path, int chunkSize, long numBytes,
            long modifiedAtMillis, Boolean overwrite) {
        Uploader uploader = new ChunkedUploader(rootId, path, numBytes, modifiedAtMillis, overwrite,
                new ChunkedUploadOutputStream(rootId, chunkSize));
        return uploader;
    }

    public <E extends Throwable> Meta uploadFileChunked(String rootId, String path, long numBytes,
            long modifiedAtMillis, Boolean overwrite, BMYStreamWriter<E> writer) throws BMYException, E {
        Uploader uploader = startUploadFileChunked(rootId, path, numBytes, modifiedAtMillis, overwrite);
        return finishUploadFile(uploader, writer);
    }

    public <E extends Throwable> Meta uploadFileChunked(String rootId, String path, int chunkSize, long numBytes,
            long modifiedAtMillis, Boolean overwrite, BMYStreamWriter<E> writer) throws BMYException, E {
        Uploader uploader = startUploadFileChunked(rootId, path, chunkSize, numBytes, modifiedAtMillis, overwrite);
        return finishUploadFile(uploader, writer);
    }

    public class ChunkedUploader extends Uploader {
        private String rootId;
        private String path;
        private long numBytes;
        private long modifiedAtMillis;
        private Boolean overwrite;
        private ChunkedUploadOutputStream body;

        private ChunkedUploader(String rootId, String path, long numBytes, long modifiedAtMillis, Boolean overwrite,
                ChunkedUploadOutputStream body) {
            this.rootId = rootId;
            this.path = path;
            this.numBytes = numBytes;
            this.modifiedAtMillis = modifiedAtMillis;
            this.overwrite = overwrite;
            this.body = body;
        }

        @Override
        public OutputStream getBody() {
            return this.body;
        }

        @Override
        public void abort() {
            // TODO: figure out how to abort chunked uploads
        }

        @Override
        public Meta finish() throws BMYException {
            if (this.body.uploadId == null) {
                return uploadFileSingle(this.rootId, this.path, this.numBytes, this.modifiedAtMillis, this.overwrite,
                        new BMYStreamWriter.ByteArrayCopier(body.chunk, 0, body.chunkPos));
            } else {
                this.body.finishChunk();
                if (this.numBytes != -1) {
                    // Make sure the number of bytes they sent matches what they
                    // said they'd send.
                    if (this.numBytes != this.body.uploadOffset) {
                        throw new IllegalStateException("'numBytes' is " + this.numBytes + " but you wrote "
                                + this.body.uploadOffset + " bytes");
                    }
                }

                return BMYRequestUtil.runAndRetry(3, new BMYRequestUtil.RequestMaker<Meta, RuntimeException>() {
                    public Meta run() throws BMYException {
                        return commitChunkedUploadByPath(ChunkedUploader.this.rootId, ChunkedUploader.this.path,
                                ChunkedUploader.this.body.uploadId, ChunkedUploader.this.modifiedAtMillis);
                    }
                });
            }
        }

        @Override
        public void close() {
            // TODO: firgure out how to abort chunked uploads
        }
    }

    private class ChunkedUploadOutputStream extends OutputStream {
        private String rootId;
        private byte[] chunk;
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
            this.chunk[this.chunkPos++] = (byte) i;
            try {
                finishChunkIfNecessary();
            } catch (BMYException e) {
                throw new IOBMYException(e);
            }
        }

        private void finishChunkIfNecessary() throws BMYException {
            assert this.chunkPos <= this.chunk.length;
            if (this.chunkPos == this.chunk.length) {
                finishChunk();
            }
        }

        private void finishChunk() throws BMYException {
            if (this.chunkPos == 0) {
                return;
            }
            if (this.uploadId == null) {
                this.uploadId = BMYRequestUtil.runAndRetry(3,
                        new BMYRequestUtil.RequestMaker<String, RuntimeException>() {
                            @Override
                            public String run() throws BMYException, RuntimeException {
                                return chunkedUploadFirst(ChunkedUploadOutputStream.this.rootId,
                                        ChunkedUploadOutputStream.this.chunk, 0,
                                        ChunkedUploadOutputStream.this.chunkPos);
                            }
                        });
                this.uploadOffset = this.chunkPos;
            } else {
                int arrayOffset = 0;
                while (true) {
                    final int arrayOffsetFinal = arrayOffset;
                    long correctedOffset = BMYRequestUtil.runAndRetry(3,
                            new BMYRequestUtil.RequestMaker<Long, RuntimeException>() {
                                @Override
                                public Long run() throws BMYException, RuntimeException {
                                    return chunkedUploadAppend(ChunkedUploadOutputStream.this.rootId,
                                            ChunkedUploadOutputStream.this.uploadId,
                                            ChunkedUploadOutputStream.this.uploadOffset,
                                            ChunkedUploadOutputStream.this.chunk, arrayOffsetFinal,
                                            ChunkedUploadOutputStream.this.chunkPos - arrayOffsetFinal);
                                }
                            });
                    long expectedOffset = this.uploadOffset + this.chunkPos;
                    if (correctedOffset == -1) {
                        // Everything ok
                        this.uploadOffset = expectedOffset;
                        break;
                    } else { /* the following code never get executed by BMY */
                        // Make sure the returned offset is within what we
                        // expect.
                        assert correctedOffset != expectedOffset;
                        if (correctedOffset < this.uploadOffset) {
                            // Somehow the server lost track of the previous
                            // data we sent it.
                            throw new BMYException.BadResponse("we were at offset " + this.uploadOffset
                                    + ", server said " + correctedOffset);
                        } else if (correctedOffset > expectedOffset) {
                            // Somehow the server has more data than we gave it!
                            throw new BMYException.BadResponse("we were at offset " + this.uploadOffset
                                    + ", server said " + correctedOffset);
                        }
                        // Server needs us to resend partial data.
                        int adjustAmount = (int) (correctedOffset - this.uploadOffset);
                        arrayOffset += adjustAmount;
                    }
                }
            }
            this.chunkPos = 0;
        }

        @Override
        public void write(byte[] bytes, int offset, int length) throws IOException {
            int inputEnd = offset + length;
            int inputPos = offset;
            while (inputPos < inputEnd) {
                int spaceInChunk = this.chunk.length - this.chunkPos;
                int leftToWrite = inputEnd - inputPos;
                int bytesToCopy = Math.min(leftToWrite, spaceInChunk);
                System.arraycopy(bytes, inputPos, this.chunk, this.chunkPos, bytesToCopy);
                this.chunkPos += bytesToCopy;
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

    public static class IOBMYException extends IOException {
        private static final long serialVersionUID = 1L;

        private BMYException underlying = null;

        public IOBMYException(BMYException underlying) {
            super(underlying);
            this.underlying = underlying;
        }

        @Override
        public BMYException getCause() {
            return this.underlying;
        }
    }

    public static abstract class Uploader {
        public abstract OutputStream getBody();

        public abstract void abort();

        public abstract void close();

        public abstract Meta finish() throws BMYException;
    }

    public static class Downloader {
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
                throw new BMYException.NetworkIO(ex.getCause());
            } catch (IOUtil.WriteException ex) {
                throw ex.getCause();
            } finally {
                this.close();
            }
            return this.meta;
        }

        public void close() {
            IOUtil.closeInput(this.body);
        }
    }

    private List<String> getPaginationParams(Integer offset, Integer limit) {
        List<String> params = new ArrayList<String>(8);
        if (offset != null) {
            params.add("offset");
            params.add(String.valueOf(offset));
        }
        if (limit != null) {
            params.add("limit");
            params.add(String.valueOf(limit));
        }
        return params;
    }

    private <T> T doDelete(String path, String[] params, ArrayList<HttpRequestor.Header> headers,
            BMYRequestUtil.ResponseHandler<T> handler) throws BMYException {
        return BMYRequestUtil.doDelete(this.requestConfig, this.host.api, path, this.token, params, headers, handler);
    }

    private <T> T doGet(String path, String[] params, ArrayList<HttpRequestor.Header> headers,
            BMYRequestUtil.ResponseHandler<T> handler) throws BMYException {
        return BMYRequestUtil.doGet(this.requestConfig, this.host.api, path, this.token, params, headers, handler);
    }

    private <T> T doPost(String path, String[] params, ArrayList<HttpRequestor.Header> headers, String body,
            BMYRequestUtil.ResponseHandler<T> handler) throws BMYException {
        return BMYRequestUtil.doPost(this.requestConfig, this.host.api, path, this.token, params, body, headers,
                handler);
    }

    private HttpRequestor.Uploader getUploaderWithPut(String path, String[] params,
            ArrayList<HttpRequestor.Header> headers) throws BMYException {
        return BMYRequestUtil.getUploaderWithPut(this.requestConfig, this.host.api, path, this.token, params, headers);
    }

    private HttpRequestor.Uploader getUploaderWithPost(String path, String[] params,
            ArrayList<HttpRequestor.Header> headers) throws BMYException {
        return BMYRequestUtil.getUploaderWithPost(this.requestConfig, this.host.api, path, this.token, params, headers);
    }
}
