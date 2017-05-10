package com.lfq.zzuli.qingtingmusic.entity;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/9 0009.
 */
public class BillboardBean {
    private String billboard_type; //榜单类型
    private String billboard_no;
    private String update_date;  //更新时间
    private String billboard_songnum; //榜单歌曲数量
    private int havemore;  //更多
    private String name;  //榜单名
    private String comment; //评论
    private String pic_s640;
    private String pic_s444;
    private String pic_s260;
    private String pic_s210;
    private String web_url; //地址

    public String getBillboard_no() {
        return billboard_no;
    }

    public void setBillboard_no(String billboard_no) {
        this.billboard_no = billboard_no;
    }

    public String getBillboard_songnum() {
        return billboard_songnum;
    }

    public void setBillboard_songnum(String billboard_songnum) {
        this.billboard_songnum = billboard_songnum;
    }

    public String getBillboard_type() {
        return billboard_type;
    }

    public void setBillboard_type(String billboard_type) {
        this.billboard_type = billboard_type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getHavemore() {
        return havemore;
    }

    public void setHavemore(int havemore) {
        this.havemore = havemore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic_s210() {
        return pic_s210;
    }

    public void setPic_s210(String pic_s210) {
        this.pic_s210 = pic_s210;
    }

    public String getPic_s260() {
        return pic_s260;
    }

    public void setPic_s260(String pic_s260) {
        this.pic_s260 = pic_s260;
    }

    public String getPic_s444() {
        return pic_s444;
    }

    public void setPic_s444(String pic_s444) {
        this.pic_s444 = pic_s444;
    }

    public String getPic_s640() {
        return pic_s640;
    }

    public void setPic_s640(String pic_s640) {
        this.pic_s640 = pic_s640;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public BillboardBean() {
    }

    public BillboardBean(String billboard_no, String billboard_songnum,
                         String billboard_type, String comment, int havemore,
                         String name, String pic_s210, String pic_s260,
                         String pic_s444, String pic_s640, String update_date, String web_url) {
        this.billboard_no = billboard_no;
        this.billboard_songnum = billboard_songnum;
        this.billboard_type = billboard_type;
        this.comment = comment;
        this.havemore = havemore;
        this.name = name;
        this.pic_s210 = pic_s210;
        this.pic_s260 = pic_s260;
        this.pic_s444 = pic_s444;
        this.pic_s640 = pic_s640;
        this.update_date = update_date;
        this.web_url = web_url;
    }


    @Override
    public String toString() {
        return "BillBoardBean{" +
                "billboard_no='" + billboard_no + '\'' +
                ", billboard_type='" + billboard_type + '\'' +
                ", update_date='" + update_date + '\'' +
                ", billboard_songnum='" + billboard_songnum + '\'' +
                ", havemore=" + havemore +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", pic_s640='" + pic_s640 + '\'' +
                ", pic_s444='" + pic_s444 + '\'' +
                ", pic_s260='" + pic_s260 + '\'' +
                ", pic_s210='" + pic_s210 + '\'' +
                ", web_url='" + web_url + '\'' +
                '}';
    }
}
