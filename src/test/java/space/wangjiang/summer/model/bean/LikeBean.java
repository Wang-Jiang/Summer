package space.wangjiang.summer.model.bean;

import space.wangjiang.summer.model.Model;

/**
 * Generated by Summer, you should not modify this file.
 */
public abstract class LikeBean<M extends LikeBean<M>> extends Model<M> {

    public void setBlogId(java.lang.Integer blogId) {
        set("blog_id", blogId);
    }

    public java.lang.Integer getBlogId() {
        return (java.lang.Integer) get("blog_id");
    }

    public void setUserId(java.lang.Integer userId) {
        set("user_id", userId);
    }

    public java.lang.Integer getUserId() {
        return (java.lang.Integer) get("user_id");
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        set("create_time", createTime);
    }

    public java.sql.Timestamp getCreateTime() {
        return (java.sql.Timestamp) get("create_time");
    }

}