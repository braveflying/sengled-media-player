package com.sengled.media.player.entity;

import android.util.Log;

import org.json.JSONObject;


public class Lives {

    private Creator creator;

    private String id;
    private String name;
    private String city;
    private String share_addr;
    private String stream_addr;
    private int version;
    private int slot;
    private int optimal;
    private int group;
    private String distance;
    private int link;
    private int multi;
    private int rotate;
    private String token;
    private String image_path;

    public Lives(JSONObject live, String rtspAddr, String imagePrePath, String imageSuffixPath) {
        try {
            if( live != null )
            {
                this.name = live.optString("name");
                this.token = this.name;
                this.stream_addr = rtspAddr + "/"+this.token+".sdp";
                this.image_path=imagePrePath+this.token+imageSuffixPath;
            }
            else
            {
                this.stream_addr = rtspAddr;
                this.image_path="";
                this.token="VIDEO";
            }

        }catch (Exception e)
        {
            Log.e("IJKMEDIA","ERROR Here");
        }
    }

    public String getImage_path() { return image_path;}

    public Creator getCreator() {
        return creator;
    }

    public String getToken() {return token;}
    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShare_addr() {
        return share_addr;
    }

    public void setShare_addr(String share_addr) {
        this.share_addr = share_addr;
    }

    public String getStream_addr() {
        return stream_addr;
    }

    public void setStream_addr(String stream_addr) {
        this.stream_addr = stream_addr;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getOptimal() {
        return optimal;
    }

    public void setOptimal(int optimal) {
        this.optimal = optimal;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public int getMulti() {
        return multi;
    }

    public void setMulti(int multi) {
        this.multi = multi;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public static class Creator {
        private int id;
        private int level;
        private String nick;
        private String portrait;

        public Creator(JSONObject creator) {
            this.nick = creator.optString("nick");
            this.portrait =creator.optString("portrait");
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }
    }
}
