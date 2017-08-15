package com.sengled.media.player.entity;

/**
 * Created by admin on 2017/7/25.
 */
public class UserInfoDto {

    private String username;
    private String messageCode;
    private String info;
    private String description;
    private String jsessionid;
    private String profile_path;
    private String nick_name;
    private String appServerAddr;
    private String ucenterAddr;
    private int snapServiceMinVerison;
    private int ifCheckSessionid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJsessionid() {
        return jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getAppServerAddr() {
        return appServerAddr;
    }

    public void setAppServerAddr(String appServerAddr) {
        this.appServerAddr = appServerAddr;
    }

    public String getUcenterAddr() {
        return ucenterAddr;
    }

    public void setUcenterAddr(String ucenterAddr) {
        this.ucenterAddr = ucenterAddr;
    }

    public int getSnapServiceMinVerison() {
        return snapServiceMinVerison;
    }

    public void setSnapServiceMinVerison(int snapServiceMinVerison) {
        this.snapServiceMinVerison = snapServiceMinVerison;
    }

    public int getIfCheckSessionid() {
        return ifCheckSessionid;
    }

    public void setIfCheckSessionid(int ifCheckSessionid) {
        this.ifCheckSessionid = ifCheckSessionid;
    }
}
