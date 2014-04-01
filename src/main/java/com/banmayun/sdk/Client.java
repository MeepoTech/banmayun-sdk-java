package com.banmayun.sdk;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.banmayun.sdk.ClientImpl.ThumbnailFormat;
import com.banmayun.sdk.ClientImpl.ThumbnailSize;
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
import com.banmayun.sdk.core.Trash;
import com.banmayun.sdk.core.User;
import com.banmayun.sdk.core.Group.GroupType;
import com.banmayun.sdk.core.Link.LinkDevice;
import com.banmayun.sdk.core.Relation.RelationRole;
import com.banmayun.sdk.core.User.UserRole;

public interface Client {

    public Link signInUser(String username, String password, String linkName, LinkDevice linkDevice, String ldapName)
            throws ClientException;

    public Link signOutUser() throws ClientException;

    public User forgotUserPassword(String email) throws ClientException;

    public User changeUserPassword(String username, String password, String newPassword) throws ClientException;

    public User resetUserPassword(String passwordResetToken, String newPassword) throws ClientException;

    public User existsUser(User user) throws ClientException;

    public User createUser(User user) throws ClientException;

    public User getUser(String targetUserId) throws ClientException;

    public ResultList<User> listUsers(UserRole role, Integer offset, Integer limit) throws ClientException;

    public User updateUser(String targetUserId, User update) throws ClientException;

    public User setUserPassword(String targetUserId, String newPassword) throws ClientException;

    public void setUserAvatar(String targetUserId, InputStream input) throws ClientException;

    public InputStream getUserAvatar(String targetUserId, ThumbnailFormat format, ThumbnailSize size)
            throws ClientException;

    public Group addUserGroup(String targetUserId, String groupId, Relation relation) throws ClientException;

    public Group getUserGroup(String targetUserId, String groupId) throws ClientException;

    public ResultList<Group> listGroupsForUser(String targetUserId, RelationRole role, Integer offset, Integer limit)
            throws ClientException;

    public Group updateUserGroup(String targetUserId, String groupId, Relation update) throws ClientException;

    public Group removeUserGroup(String targetUserId, String groupId) throws ClientException;

    public Group existsGroup(Group group) throws ClientException;

    public Group createGroup(Group group, String ownerId) throws ClientException;

    public Group getGroup(String groupId) throws ClientException;

    public ResultList<Group> listGroups(GroupType type, Integer offset, Integer limit) throws ClientException;

    public Group updateGroup(String groupId, Group update) throws ClientException;

    public Group deleteGroup(String groupId) throws ClientException;

    public void setGroupLogo(String groupId, InputStream input) throws ClientException;

    public InputStream getGroupLogo(String groupId) throws ClientException;

    public User addGroupUser(String groupId, String targetUserId, Relation relation) throws ClientException;

    public User getGroupUser(String groupId, String targetUserId) throws ClientException;

    public ResultList<User> listUsersForGroup(String groupId, String targetUserId) throws ClientException;

    public User updateGroupUser(String groupId, String targetUserId, Relation update) throws ClientException;

    public User removeGroupUser(String groupId, String targetUserId) throws ClientException;

    public Root getRoot(String rootId) throws ClientException;

    public Root setRootDefaultPermission(String rootId, Permission defaultPermission) throws ClientException;

    public Root setRootQuota(String rootId, long quota) throws ClientException;

    public Meta uploadFileByPath(String rootId, String path, Date clientMtime, Boolean overwrite, InputStream input)
            throws ClientException;

    public InputStream getFileByPath(String rootId, String path, Long version, Long offset, Long bytes)
            throws ClientException;

    public Meta trashRecursivelyByPath(String rootId, String path) throws ClientException;

    public Meta uploadFile(String rootId, String metaId, Date clientMtime, InputStream input) throws ClientException;

    public InputStream getFile(String rootId, String metaId, Long version, Long offset, Long bytes)
            throws ClientException;

    public Meta trashRecursively(String rootId, String metaId) throws ClientException;

    public Meta getMeta(String rootId, String metaId) throws ClientException;

    public InputStream getFileThumbnail(String rootId, String metaId, ThumbnailFormat format, ThumbnailSize size)
            throws ClientException;

    public ResultList<Revision> listRevisionsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException;

    public Comment createComment(String rootId, String metaId, Comment comment) throws ClientException;

    public Comment getComment(String rootId, String metaId, String commentId) throws ClientException;

    public ResultList<Comment> listComments(Integer offset, Integer limit) throws ClientException;

    public ResultList<Comment> listCommentsForRoot(String rootId, Integer offset, Integer limit) throws ClientException;

    public ResultList<Comment> listCommentsForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException;

    public Comment deleteComment(String rootId, String metaId, String commentId) throws ClientException;

    public void deleteComments() throws ClientException;

    public void deleteCommentsForRoot(String rootId) throws ClientException;

    public void deleteCommentsForMeta(String rootId, String metaId) throws ClientException;

    public Share createShare(String rootId, String metaId, String password, Date expiresAt) throws ClientException;

    public Share getShare(String rootId, String metaId, String shareId) throws ClientException;

    public ResultList<Share> listShares(Integer offset, Integer limit) throws ClientException;

    public ResultList<Share> listSharesForRoot(String rootId, Integer offset, Integer limit) throws ClientException;

    public ResultList<Share> listSharesForMeta(String rootId, String metaId, Integer offset, Integer limit)
            throws ClientException;

    public Share deleteShare(String rootId, String metaId, String shareId) throws ClientException;

    public void deleteShares() throws ClientException;

    public void deleteSharesForRoot(String rootId) throws ClientException;

    public void deleteSharesForMeta(String rootId, String metaId) throws ClientException;

    public Meta commitChunkedUploadByPath(String rootId, String path, String uploadId, Date clientMtime)
            throws ClientException;

    public Meta copyRecursivelyByPath(String rootId, String path, String toPath) throws ClientException;

    public Meta createFolderByPath(String rootId, String path, Date clientMtime) throws ClientException;

    public Meta getMetaByPath(String rootId, String path, Boolean list) throws ClientException;

    public List<Meta> listFolderByPath(String rootId, String path) throws ClientException;

    public Meta moveRecursivelyByPath(String rootId, String path, String toPath) throws ClientException;

    public Meta rollbackFileByPath(String rootId, String path, long toVersion) throws ClientException;

    public Meta thunderUploadFileByPath(String rootId, String path, String md5, long bytes, Date clientMtime)
            throws ClientException;

    public Meta utimeFolderByPath(String rootId, String path, Date clientMtime) throws ClientException;

    public Meta setPermissionByPath(String rootId, String path, Permission permission) throws ClientException;

    public Meta listPermissionsForRoot(String rootId) throws ClientException;

    public ChunkedUpload chunkedUpload(String uploadId, Long offset, InputStream input) throws ClientException;

    public Delta deltaForRoot(String rootId, String cursorId) throws ClientException;

    public Trash getTrash(String rootId, String trashId) throws ClientException;

    public ResultList<Trash> listTrashesForRoot(String rootId, Integer offset, Integer limit) throws ClientException;

    public Trash deleteTrash(String rootId, String trashId) throws ClientException;

    public void deleteTrashesForRoot(String rootId) throws ClientException;

    public Trash restoreTrashRecursively(String rootId, String trashId, String toPath) throws ClientException;

    public ResultList<User> searchUsers(String query, Integer offset, Integer limit) throws ClientException;

    public ResultList<User> searchUsersForGroup(String groupId, String query, Integer offset, Integer limit)
            throws ClientException;

    public ResultList<Group> searchGroups(String query, Integer offset, Integer limit) throws ClientException;

    public ResultList<Group> searchGroupsForUser(String userId, String query, Integer offset, Integer limit)
            throws ClientException;

    public ResultList<Meta> searchFiles(String query, Integer offset, Integer limit) throws ClientException;

    public ResultList<Meta> searchFilesForRoot(String rootId, String query, Integer offset, Integer limit)
            throws ClientException;

    public ResultList<Meta> searchFilesForMeta(String rootId, String path, String query, Integer offset, Integer limit)
            throws ClientException;

    public ResultList<User> topUsers(String orderBy, Integer offset, Integer limit) throws ClientException;

    public ResultList<Group> topGroups(String orderBy, Integer offset, Integer limit) throws ClientException;

    public ResultList<Meta> topFiles(String orderBy, Integer offset, Integer limit) throws ClientException;
}
