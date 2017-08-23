package com.hackathon.cmos.www.voicemusicplayer.entity;

/**
 * Created by zhagnxi on 2017/8/15.
 */

public class Song {
    private String albumid;
    private String albumname;
    private String albumpic_big;
    private String albumpic_small;
    private String downUrl;
    private String m4a;
    private String singerid;
    private String singername;
    private String songid;
    private String songname;
    private String seconds;

    public Song() {
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public Song(String albumid, String albumname, String albumpic_big,
                String albumpic_small, String downUrl, String m4a, String singerid,
                String singername, String songid, String songname,String seconds) {
        this.albumid = albumid;
        this.albumname = albumname;
        this.albumpic_big = albumpic_big;
        this.albumpic_small = albumpic_small;
        this.downUrl = downUrl;
        this.m4a = m4a;
        this.singerid = singerid;
        this.singername = singername;
        this.songid = songid;
        this.songname = songname;
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return "Song{" +
                "albumid='" + albumid + '\'' +
                ", albumname='" + albumname + '\'' +
                ", albumpic_big='" + albumpic_big + '\'' +
                ", albumpic_small='" + albumpic_small + '\'' +
                ", downUrl='" + downUrl + '\'' +
                ", m4a='" + m4a + '\'' +
                ", singerid='" + singerid + '\'' +
                ", singername='" + singername + '\'' +
                ", songid='" + songid + '\'' +
                ", songname='" + songname + '\'' +
                ", seconds='" + seconds + '\'' +
                '}';
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getAlbumpic_big() {
        return albumpic_big;
    }

    public void setAlbumpic_big(String albumpic_big) {
        this.albumpic_big = albumpic_big;
    }

    public String getAlbumpic_small() {
        return albumpic_small;
    }

    public void setAlbumpic_small(String albumpic_small) {
        this.albumpic_small = albumpic_small;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getM4a() {
        return m4a;
    }

    public void setM4a(String m4a) {
        this.m4a = m4a;
    }

    public String getSingerid() {
        return singerid;
    }

    public void setSingerid(String singerid) {
        this.singerid = singerid;
    }

    public String getSingername() {
        return singername;
    }

    public void setSingername(String singername) {
        this.singername = singername;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }
}
