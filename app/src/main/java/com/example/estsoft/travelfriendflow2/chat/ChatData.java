package com.example.estsoft.travelfriendflow2;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ChatData implements Serializable {

    private Long no;
    private Long userNum;
    private String id;
    private Long regionNum;
    private String txt;
    private String image;
    private static final long serialVersionUID = 12344321L;

    public ChatData(){

    }

    public ChatData(String id, Long regionNum, String txt, String image){
        this.id = id;
        this.regionNum = regionNum;
        this.txt = txt;
        this.image = image;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Long getRegionNum() {
        return regionNum;
    }
    public void setRegionNum(Long regionNum) {
        this.regionNum = regionNum;
    }
    public String getTxt() {
        return txt;
    }
    public void setTxt(String txt) {
        this.txt = txt;
    }
    public Long getNo() {
        return no;
    }
    public void setNo(Long no) {
        this.no = no;
    }
    public Long getUserNum() {
        return userNum;
    }

    public void setUserNum(Long userNum) {
        this.userNum = userNum;
    }

    @Override
    public String toString() {
        return "ChatData [no=" + no + ", userNum=" + userNum + ", id=" + id + ", regionNum=" + regionNum + ", txt="
                + txt + ", image=" + image + "]";
    }

    private void writObject(ObjectOutputStream stream) throws IOException{
        stream.defaultWriteObject();
    }


}
