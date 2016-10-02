package com.johnguant.redditthing.redditapi.model;

import java.util.List;

public class Comment {
    private String approvedBy;
    private String author;
    private String authorFlairCssClass;
    private String authorFlairText;
    private String bannedBy;
    private String body;
    private String bodyHtml;
    private String edited;
    private int gilded;
    private Boolean likes;
    private String linkAuthor;
    private String linkId;
    private String linkTitle;
    private String linkUrl;
    private int numReports;
    private String parentId;
    private List<Thing<Comment>> replies;
    private boolean saved;
    private int score;
    private boolean scoreHidden;
    private String subreddit;
    private String subredditId;
    private String distinguished;

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorFlairCssClass() {
        return authorFlairCssClass;
    }

    public void setAuthorFlairCssClass(String authorFlairCssClass) {
        this.authorFlairCssClass = authorFlairCssClass;
    }

    public String getAuthorFlairText() {
        return authorFlairText;
    }

    public void setAuthorFlairText(String authorFlairText) {
        this.authorFlairText = authorFlairText;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public void setBannedBy(String bannedBy) {
        this.bannedBy = bannedBy;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public int getGilded() {
        return gilded;
    }

    public void setGilded(int gilded) {
        this.gilded = gilded;
    }

    public Boolean getLikes() {
        return likes;
    }

    public void setLikes(Boolean likes) {
        this.likes = likes;
    }

    public String getLinkAuthor() {
        return linkAuthor;
    }

    public void setLinkAuthor(String linkAuthor) {
        this.linkAuthor = linkAuthor;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getNumReports() {
        return numReports;
    }

    public void setNumReports(int numReports) {
        this.numReports = numReports;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<Thing<Comment>> getReplies() {
        return replies;
    }

    public void setReplies(List<Thing<Comment>> replies) {
        this.replies = replies;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isScoreHidden() {
        return scoreHidden;
    }

    public void setScoreHidden(boolean scoreHidden) {
        this.scoreHidden = scoreHidden;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSubredditId() {
        return subredditId;
    }

    public void setSubredditId(String subredditId) {
        this.subredditId = subredditId;
    }

    public String getDistinguished() {
        return distinguished;
    }

    public void setDistinguished(String distinguished) {
        this.distinguished = distinguished;
    }
}
