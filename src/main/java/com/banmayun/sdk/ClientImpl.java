package com.banmayun.sdk;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.banmayun.sdk.core.ChunkedUpload;
import com.banmayun.sdk.core.Comment;
import com.banmayun.sdk.core.Delta;
import com.banmayun.sdk.core.Group;
import com.banmayun.sdk.core.Group.GroupType;
import com.banmayun.sdk.core.Link;
import com.banmayun.sdk.core.Link.LinkDevice;
import com.banmayun.sdk.core.Meta;
import com.banmayun.sdk.core.Permission;
import com.banmayun.sdk.core.Relation;
import com.banmayun.sdk.core.Relation.RelationRole;
import com.banmayun.sdk.core.ResultList;
import com.banmayun.sdk.core.Revision;
import com.banmayun.sdk.core.Root;
import com.banmayun.sdk.core.Share;
import com.banmayun.sdk.core.Trash;
import com.banmayun.sdk.core.User;
import com.banmayun.sdk.core.User.UserRole;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.net.PercentEscaper;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class ClientImpl implements Client {

    public enum ThumbnailFormat {
        JPEG, PNG
    }

    public enum ThumbnailSize {
        XS, S, M, L, XL
    }

    private PercentEscaper percentEscaper = null;
    private PercentEscaper urlEncoder = null;
    private Session session = null;
    private WebResource base = null;

    public ClientImpl(String host, int port, Session session) {
        this.percentEscaper = new PercentEscaper("", true);
        this.urlEncoder = new PercentEscaper("/", true);
        this.session = session;

        com.sun.jersey.api.client.config.ClientConfig clientConfig = new com.sun.jersey.api.client.config.DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create(clientConfig);

        this.base = client.resource("http://" + host + ":" + port + "/" + Version.API_VERSION);
    }

    @Override
    public Link signInUser(String username, String password, String linkName, LinkDevice linkDevice, String ldapName)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/auth/sign_in", false).queryParam("username", username)
                .queryParam("password", password).queryParam("link_name", linkName)
                .queryParam("link_device", linkDevice.toString()).queryParam("ldap_name", ldapName);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Link.class);
    }

    @Override
    public Link signOutUser() throws ClientException {
        WebResource resource = this.fillCommonParams("/auth/sign_out", true);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Link.class);
    }

    @Override
    public User forgotUserPassword(String email) throws ClientException {
        WebResource resource = this.fillCommonParams("/auth/forgot_password", false).queryParam("email", email);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", User.class);
    }

    @Override
    public User changeUserPassword(String username, String password, String newPassword) throws ClientException {
        WebResource resource = this.fillCommonParams("/auth/change_password", false).queryParam("username", username)
                .queryParam("password", password).queryParam("new_password", newPassword);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", User.class);
    }

    @Override
    public User resetUserPassword(String passwordResetToken, String newPassword) throws ClientException {
        WebResource resource = this.fillCommonParams("/auth/reset_password", false)
                .queryParam("token", passwordResetToken).queryParam("new_password", newPassword);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", User.class);
    }

    @Override
    public User existsUser(User user) throws ClientException {
        WebResource resource = this.fillCommonParams("/users/exists", false);
        return resource.entity(user, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", User.class);
    }

    @Override
    public User createUser(User user) throws ClientException {
        WebResource resource = null;
        if (this.session != null) {
            resource = this.fillCommonParams("/users", true);
        } else {
            resource = this.fillCommonParams("/users", false);
        }
        return resource.entity(user, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", User.class);
    }

    @Override
    public User getUser(String targetUserId) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", User.class);
    }

    @Override
    public ResultList<User> listUsers(UserRole role, Integer offset, Integer limit) throws ClientException {
        WebResource resource = this.fillCommonParams("/users", true);
        if (role != null) {
            resource = resource.queryParam("role", role.toString());
        }
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<User>>() {
        });
    }

    @Override
    public User updateUser(String targetUserId, User update) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/update", true);
        return resource.entity(update, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", User.class);
    }

    @Override
    public User setUserPassword(String targetUserId, String newPassword) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId, true).queryParam("new_password", newPassword);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", User.class);
    }

    @Override
    public void setUserAvatar(String targetUserId, InputStream input) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/avatar", true);
        resource.entity(input, MediaType.APPLICATION_OCTET_STREAM).method("POST");
    }

    @Override
    public InputStream getUserAvatar(String targetUserId, ThumbnailFormat format, ThumbnailSize size)
            throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/avatar", true);
        if (format != null) {
            resource = resource.queryParam("format", format.toString());
        }
        if (size != null) {
            resource = resource.queryParam("size", size.toString());
        }
        return resource.accept(MediaType.APPLICATION_OCTET_STREAM).method("GET", InputStream.class);
    }

    @Override
    public Group addUserGroup(String targetUserId, String groupId, Relation relation) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/groups", true).queryParam("group_id",
                groupId);
        return resource.entity(relation, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Group.class);
    }

    @Override
    public Group getUserGroup(String targetUserId, String groupId) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/groups/" + groupId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Group.class);
    }

    @Override
    public ResultList<Group> listGroupsForUser(String targetUserId, RelationRole role, Integer offset, Integer limit)
            throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/groups", true);
        if (role != null) {
            resource = resource.queryParam("role", role.toString());
        }
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Group>>() {
        });
    }

    @Override
    public Group updateUserGroup(String targetUserId, String groupId, Relation update) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/groups/" + groupId + "/update", true);
        return resource.entity(update, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Group.class);
    }

    @Override
    public Group removeUserGroup(String targetUserId, String groupId) throws ClientException {
        String userId = targetUserId == null ? this.session.getLink().userId : targetUserId;
        WebResource resource = this.fillCommonParams("/users/" + userId + "/groups/" + groupId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Group.class);
    }

    @Override
    public Group existsGroup(Group group) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/exists", true);
        return resource.entity(group, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Group.class);
    }

    @Override
    public Group createGroup(Group group, String ownerId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups", true);
        if (ownerId != null) {
            resource = resource.queryParam("owner_id", ownerId);
        }
        return resource.entity(group, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Group.class);
    }

    @Override
    public Group getGroup(String groupId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Group.class);
    }

    @Override
    public ResultList<Group> listGroups(GroupType type, Integer offset, Integer limit) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups", true);
        if (type != null) {
            resource = resource.queryParam("type", type.toString());
        }
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Group>>() {
        });
    }

    @Override
    public Group updateGroup(String groupId, Group update) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/update", true);
        return resource.entity(update, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Group.class);
    }

    @Override
    public Group deleteGroup(String groupId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Group.class);
    }

    @Override
    public void setGroupLogo(String groupId, InputStream input) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/logo", true);
        resource.entity(input, MediaType.APPLICATION_OCTET_STREAM).method("POST");
    }

    @Override
    public InputStream getGroupLogo(String groupId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/logo", true);
        return resource.accept(MediaType.APPLICATION_OCTET_STREAM).method("GET", InputStream.class);
    }

    @Override
    public User addGroupUser(String groupId, String targetUserId, Relation relation) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/users", true).queryParam("user_id",
                targetUserId);
        return resource.entity(relation, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", User.class);
    }

    @Override
    public User getGroupUser(String groupId, String targetUserId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/users/" + targetUserId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", User.class);
    }

    @Override
    public ResultList<User> listUsersForGroup(String groupId, String targetUserId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/users", true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<User>>() {
        });
    }

    @Override
    public User updateGroupUser(String groupId, String targetUserId, Relation update) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/users/" + targetUserId + "/update", true);
        return resource.entity(update, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", User.class);
    }

    @Override
    public User removeGroupUser(String groupId, String targetUserId) throws ClientException {
        WebResource resource = this.fillCommonParams("/groups/" + groupId + "/users/" + targetUserId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", User.class);
    }

    @Override
    public Root getRoot(String rootId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Root.class);
    }

    @Override
    public Root setRootDefaultPermission(String rootId, Permission defaultPermission) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/default_permission", true);
        return resource.entity(defaultPermission, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Root.class);
    }

    @Override
    public Root setRootQuota(String rootId, long quota) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/quota", true).queryParam("quota",
                String.valueOf(quota));
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Root.class);
    }

    @Override
    public Meta uploadFileByPath(String rootId, String path, Date clientMtime, Boolean overwrite, InputStream input)
            throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/p" + this.urlEncode(path), true)
                .queryParam("client_mtime", String.valueOf(clientMtime));
        if (overwrite != null) {
            resource = resource.queryParam("overwrite", overwrite.toString());
        }
        return resource.entity(input, MediaType.APPLICATION_OCTET_STREAM).accept(MediaType.APPLICATION_JSON)
                .method("PUT", Meta.class);
    }

    @Override
    public InputStream getFileByPath(String rootId, String path, Long version, Long offset, Long bytes)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/p" + this.urlEncode(path), true);
        if (version != null) {
            resource = resource.queryParam("version", version.toString());
        }
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (bytes != null) {
            resource = resource.queryParam("bytes", bytes.toString());
        }
        return resource.accept(MediaType.APPLICATION_OCTET_STREAM).method("GET", InputStream.class);
    }

    @Override
    public Meta trashRecursivelyByPath(String rootId, String path) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/p" + this.urlEncode(path), true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Meta.class);
    }

    @Override
    public Meta uploadFile(String rootId, String metaId, Date clientMtime, InputStream input) throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId, true).queryParam(
                "client_mtime", String.valueOf(clientMtime));
        return resource.entity(input, MediaType.APPLICATION_OCTET_STREAM).accept(MediaType.APPLICATION_JSON)
                .method("POST", Meta.class);
    }

    @Override
    public InputStream getFile(String rootId, String metaId, Long version, Long offset, Long bytes)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId, true);
        if (version != null) {
            resource = resource.queryParam("version", version.toString());
        }
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (bytes != null) {
            resource = resource.queryParam("bytes", bytes.toString());
        }
        return resource.accept(MediaType.APPLICATION_OCTET_STREAM).method("GET", InputStream.class);
    }

    @Override
    public Meta trashRecursively(String rootId, String metaId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Meta.class);
    }

    @Override
    public Meta getMeta(String rootId, String metaId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/meta", true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Meta.class);
    }

    @Override
    public InputStream getFileThumbnail(String rootId, String metaId, ThumbnailFormat format, ThumbnailSize size)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/thumbnail", true);
        if (format != null) {
            resource = resource.queryParam("format", format.toString());
        }
        if (size != null) {
            resource = resource.queryParam("size", format.toString());
        }
        return resource.accept(MediaType.APPLICATION_OCTET_STREAM).method("GET", InputStream.class);
    }

    @Override
    public ResultList<Revision> listRevisionsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/revisions", true);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Revision>>() {
        });
    }

    @Override
    public Comment createComment(String rootId, String metaId, Comment comment) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/comments", true);
        return resource.entity(comment, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Comment.class);
    }

    @Override
    public Comment getComment(String rootId, String metaId, String commentId) throws ClientException {
        WebResource resource = this.fillCommonParams(
                "/roots/" + rootId + "/files/" + metaId + "/comments/" + commentId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Comment.class);
    }

    @Override
    public ResultList<Comment> listComments(Integer offset, Integer limit) throws ClientException {
        return this.listComments(null, null, offset, limit);
    }

    @Override
    public ResultList<Comment> listCommentsForRoot(String rootId, Integer offset, Integer limit) throws ClientException {
        return this.listComments(rootId, null, offset, limit);
    }

    @Override
    public ResultList<Comment> listCommentsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException {
        return this.listComments(rootId, metaId, offset, limit);
    }

    private ResultList<Comment> listComments(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException {
        if (rootId == null) {
            rootId = "all";
        }
        if (metaId == null) {
            metaId = "all";
        }
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/comments", true);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Comment>>() {
        });
    }

    @Override
    public Comment deleteComment(String rootId, String metaId, String commentId) throws ClientException {
        WebResource resource = this.fillCommonParams(
                "/roots/" + rootId + "/files/" + metaId + "/comments/" + commentId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Comment.class);
    }

    @Override
    public void deleteComments() throws ClientException {
        this.deleteComments(null, null);
    }

    @Override
    public void deleteCommentsForRoot(String rootId) throws ClientException {
        this.deleteComments(rootId, null);
    }

    @Override
    public void deleteCommentsForMeta(String rootId, String metaId) throws ClientException {
        this.deleteComments(rootId, metaId);
    }

    private void deleteComments(String rootId, String metaId) throws ClientException {
        if (rootId == null) {
            rootId = "all";
        }
        if (metaId == null) {
            metaId = "all";
        }
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/comments", true);
        resource.accept(MediaType.APPLICATION_JSON).method("DELETE");
    }

    @Override
    public Share createShare(String rootId, String metaId, String password, Date expiresAt) throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/shares", true)
                .queryParam("passwor", password).queryParam("expires_at", expiresAt.toString());
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Share.class);
    }

    @Override
    public Share getShare(String rootId, String metaId, String shareId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/shares/" + shareId,
                true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Share.class);
    }

    @Override
    public ResultList<Share> listShares(Integer offset, Integer limit) throws ClientException {
        return this.listShares(null, null, offset, limit);
    }

    @Override
    public ResultList<Share> listSharesForRoot(String rootId, Integer offset, Integer limit) throws ClientException {
        return this.listShares(rootId, null, offset, limit);
    }

    @Override
    public ResultList<Share> listSharesForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException {
        return this.listShares(rootId, metaId, offset, limit);
    }

    private ResultList<Share> listShares(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException {
        if (rootId == null) {
            rootId = "all";
        }
        if (metaId == null) {
            metaId = "all";
        }
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/shares", true);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Share>>() {
        });
    }

    @Override
    public Share deleteShare(String rootId, String metaId, String shareId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/shares/" + shareId,
                true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Share.class);
    }

    @Override
    public void deleteShares() throws ClientException {
        this.deleteShares(null, null);
    }

    @Override
    public void deleteSharesForRoot(String rootId) throws ClientException {
        this.deleteShares(rootId, null);
    }

    @Override
    public void deleteSharesForMeta(String rootId, String metaId) throws ClientException {
        this.deleteShares(rootId, metaId);
    }

    private void deleteShares(String rootId, String metaId) throws ClientException {
        if (rootId == null) {
            rootId = "all";
        }
        if (metaId == null) {
            metaId = "all";
        }
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/files/" + metaId + "/shares", true);
        resource.accept(MediaType.APPLICATION_JSON).method("DELETE");
    }

    @Override
    public Meta commitChunkedUploadByPath(String rootId, String path, String uploadId, Date clientMtime)
            throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/fileops/commit_chunked_upload", true)
                .queryParam("root_id", rootId).queryParam("path", this.percentEscape(path))
                .queryParam("upload_id", uploadId).queryParam("client_mtime", clientMtime.toString());
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta copyRecursivelyByPath(String rootId, String path, String toPath) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/copy", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path)).queryParam("to_path", this.percentEscape(toPath));
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta createFolderByPath(String rootId, String path, Date clientMtime) throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/fileops/create_folder", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path));
        if (clientMtime != null) {
            resource.queryParam("client_mtime", clientMtime.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta getMetaByPath(String rootId, String path, Boolean list) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/get_meta", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path));
        if (list != null) {
            resource = resource.queryParam("list", list.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public List<Meta> listFolderByPath(String rootId, String path) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/list_folder", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path));
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", new GenericType<List<Meta>>() {
        });
    }

    @Override
    public Meta moveRecursivelyByPath(String rootId, String path, String toPath) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/move", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path)).queryParam("to_path", this.percentEscape(toPath));
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta rollbackFileByPath(String rootId, String path, long toVersion) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/rollback", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path)).queryParam("to_version", String.valueOf(toVersion));
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta thunderUploadFileByPath(String rootId, String path, String md5, long bytes, Date clientMtime)
            throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/fileops/thunder_upload", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path)).queryParam("md5", md5)
                .queryParam("bytes", String.valueOf(bytes)).queryParam("client_mtime", clientMtime.toString());
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta utimeFolderByPath(String rootId, String path, Date clientMtime) throws ClientException {
        // TODO: rfc1123
        WebResource resource = this.fillCommonParams("/fileops/utime_folder", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path)).queryParam("client_mtime", clientMtime.toString());
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public Meta setPermissionByPath(String rootId, String path, Permission permission) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/set_permission", true).queryParam("root_id", rootId)
                .queryParam("path", this.percentEscape(path));
        return resource.entity(permission, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .method("POST", Meta.class);
    }

    @Override
    public Meta listPermissionsForRoot(String rootId) throws ClientException {
        WebResource resource = this.fillCommonParams("/fileops/list_permissions", true).queryParam("root_id", rootId);
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Meta.class);
    }

    @Override
    public ChunkedUpload chunkedUpload(String uploadId, Long offset, InputStream input) throws ClientException {
        WebResource resource = this.fillCommonParams("/chunked_upload", true);
        if (uploadId != null) {
            resource = resource.queryParam("upload_id", uploadId);
        }
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        return resource.entity(input, MediaType.APPLICATION_OCTET_STREAM).accept(MediaType.APPLICATION_JSON)
                .method("POST", ChunkedUpload.class);
    }

    @Override
    public Delta deltaForRoot(String rootId, String cursorId) throws ClientException {
        WebResource resource = this.fillCommonParams("/delta", true);
        if (cursorId != null) {
            resource = resource.queryParam("cursor_id", cursorId);
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Delta.class);
    }

    @Override
    public Trash getTrash(String rootId, String trashId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/trashes/" + trashId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", Trash.class);
    }

    @Override
    public ResultList<Trash> listTrashesForRoot(String rootId, Integer offset, Integer limit) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/trashes", true);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Trash>>() {
        });
    }

    @Override
    public Trash deleteTrash(String rootId, String trashId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/trashes/" + trashId, true);
        return resource.accept(MediaType.APPLICATION_JSON).method("DELETE", Trash.class);
    }

    @Override
    public void deleteTrashesForRoot(String rootId) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/trashes", true);
        resource.accept(MediaType.APPLICATION_JSON).method("DELETE");
    }

    @Override
    public Trash restoreTrashRecursively(String rootId, String trashId, String toPath) throws ClientException {
        WebResource resource = this.fillCommonParams("/roots/" + rootId + "/trashes/" + trashId + "/restore", true);
        if (toPath != null) {
            resource = resource.queryParam("to_path", this.percentEscape(toPath));
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("POST", Trash.class);
    }

    @Override
    public ResultList<User> searchUsers(String query, Integer offset, Integer limit) throws ClientException {
        return this.searchUsers(null, query, offset, limit);
    }

    @Override
    public ResultList<User> searchUsersForGroup(String groupId, String query, Integer offset, Integer limit)
            throws ClientException {
        return this.searchUsers(groupId, query, offset, limit);
    }

    private ResultList<User> searchUsers(String groupId, String query, Integer offset, Integer limit)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/search/users", true);
        if (groupId != null) {
            resource = resource.queryParam("group_id", groupId);
        }
        resource = resource.queryParam("query", query);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<User>>() {
        });
    }

    @Override
    public ResultList<Group> searchGroups(String query, Integer offset, Integer limit) throws ClientException {
        return this.searchGroups(null, query, offset, limit);
    }

    @Override
    public ResultList<Group> searchGroupsForUser(String userId, String query, Integer offset, Integer limit)
            throws ClientException {
        return this.searchGroups(userId, query, offset, limit);
    }

    private ResultList<Group> searchGroups(String userId, String query, Integer offset, Integer limit)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/search/groups", true);
        if (userId != null) {
            resource = resource.queryParam("user_id", userId);
        }
        resource = resource.queryParam("query", query);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Group>>() {
        });
    }

    @Override
    public ResultList<Meta> searchFiles(String query, Integer offset, Integer limit) throws ClientException {
        return this.searchFiles(null, null, query, offset, limit);
    }

    @Override
    public ResultList<Meta> searchFilesForRoot(String rootId, String query, Integer offset, Integer limit)
            throws ClientException {
        return this.searchFiles(rootId, null, query, offset, limit);
    }

    @Override
    public ResultList<Meta> searchFilesForMeta(String rootId, String path, String query, Integer offset, Integer limit)
            throws ClientException {
        return this.searchFiles(rootId, path, query, offset, limit);
    }

    private ResultList<Meta> searchFiles(String rootId, String path, String query, Integer offset, Integer limit)
            throws ClientException {
        WebResource resource = this.fillCommonParams("/search/files", true);
        if (rootId != null) {
            resource = resource.queryParam("root_id", rootId);
        }
        if (path != null) {
            resource = resource.queryParam("path", this.percentEscape(path));
        }
        resource = resource.queryParam("query", query);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Meta>>() {
        });
    }

    @Override
    public ResultList<User> topUsers(String orderBy, Integer offset, Integer limit) throws ClientException {
        WebResource resource = this.fillCommonParams("/top/users", true).queryParam("order_by", orderBy);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<User>>() {
        });
    }

    @Override
    public ResultList<Group> topGroups(String orderBy, Integer offset, Integer limit) throws ClientException {
        WebResource resource = this.fillCommonParams("/top/groups", true).queryParam("order_by", orderBy);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Group>>() {
        });
    }

    @Override
    public ResultList<Meta> topFiles(String orderBy, Integer offset, Integer limit) throws ClientException {
        WebResource resource = this.fillCommonParams("/top/files", true).queryParam("order_by", orderBy);
        if (offset != null) {
            resource = resource.queryParam("offset", offset.toString());
        }
        if (limit != null) {
            resource = resource.queryParam("limit", limit.toString());
        }
        return resource.accept(MediaType.APPLICATION_JSON).method("GET", new GenericType<ResultList<Meta>>() {
        });
    }

    private WebResource fillCommonParams(String url, boolean tokenRequired) {
        WebResource ret = this.base.path(url).queryParam("locale", this.session.getLocale().toString())
                .queryParam("time_zone", this.session.getTimeZone().toString());
        if (!tokenRequired) {
            return ret;
        }

        return ret.queryParam("token", this.session.getLink().token);
    }

    private String percentEscape(String arg) {
        return this.percentEscaper.escape(arg);
    }

    private String urlEncode(String arg) {
        return this.urlEncoder.escape(arg);
    }
}
