package com.hackathon.cmos.www.voicemusicplayer.entity;

import java.util.List;

/**
 * Created by zhangxi on 2017/8/15.
 */

public class PageBean {
    private List<Song> contentlist;
    private String maxResult;
    private String allNum;
    private String allPages;
    private String currentPage;

    public PageBean() {
    }

    public PageBean(List<Song> contentlist, String maxResult, String allNum, String allPages, String currentPage) {
        this.contentlist = contentlist;
        this.maxResult = maxResult;
        this.allNum = allNum;
        this.allPages = allPages;
        this.currentPage = currentPage;
    }

    public List<Song> getContentlist() {
        return contentlist;
    }

    public void setContentlist(List<Song> contentlist) {
        this.contentlist = contentlist;
    }

    public String getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(String maxResult) {
        this.maxResult = maxResult;
    }

    public String getAllNum() {
        return allNum;
    }

    public void setAllNum(String allNum) {
        this.allNum = allNum;
    }

    public String getAllPages() {
        return allPages;
    }

    public void setAllPages(String allPages) {
        this.allPages = allPages;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }
}
