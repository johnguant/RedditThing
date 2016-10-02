package com.johnguant.redditthing.redditapi.model;

public class Account {

    private int commentKarma;
    private boolean hasMail;
    private boolean hasModMail;
    private boolean hasVerifiedMail;
    private String id;
    private int inboxCount;
    private boolean isFriend;
    private boolean isGold;
    private boolean isMod;
    private int linkKarma;
    private String modhash;
    private String name;
    private boolean over18;

    public int getCommentKarma() {
        return commentKarma;
    }

    public void setCommentKarma(int commentKarma) {
        this.commentKarma = commentKarma;
    }

    public boolean isHasMail() {
        return hasMail;
    }

    public void setHasMail(boolean hasMail) {
        this.hasMail = hasMail;
    }

    public boolean isHasModMail() {
        return hasModMail;
    }

    public void setHasModMail(boolean hasModMail) {
        this.hasModMail = hasModMail;
    }

    public boolean isHasVerifiedMail() {
        return hasVerifiedMail;
    }

    public void setHasVerifiedMail(boolean hasVerifiedMail) {
        this.hasVerifiedMail = hasVerifiedMail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInboxCount() {
        return inboxCount;
    }

    public void setInboxCount(int inboxCount) {
        this.inboxCount = inboxCount;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public boolean isGold() {
        return isGold;
    }

    public void setGold(boolean gold) {
        isGold = gold;
    }

    public boolean isMod() {
        return isMod;
    }

    public void setMod(boolean mod) {
        isMod = mod;
    }

    public int getLinkKarma() {
        return linkKarma;
    }

    public void setLinkKarma(int linkKarma) {
        this.linkKarma = linkKarma;
    }

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOver18() {
        return over18;
    }

    public void setOver18(boolean over18) {
        this.over18 = over18;
    }
}
