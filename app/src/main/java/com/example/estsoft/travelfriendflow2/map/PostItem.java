package com.example.estsoft.travelfriendflow2.map;

/**
 * Created by YeonJi on 2016-08-22.
 * Post Table
 */
public class PostItem {
    public String no;       /* 해당 관광지 NO (PK)*/
    public String city_no;
    public String postList_no;
    public String postOrder;

    public String title;
    public String picture;
    public double latitude;
    public double longitude;
    public String info;
    public String category;
    public String address;

    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }
    public String getNo() {        return no;    }
    public void setNo(String no) {        this.no = no;    }
    public String getCity_no() {        return city_no;    }
    public void setCity_no(String city_no) {        this.city_no = city_no;    }
    public String getPostList_no() {        return postList_no;    }
    public void setPostList_no(String postList_no) {        this.postList_no = postList_no;    }
    public String getPostOrder() {        return postOrder;    }
    public void setPostOrder(String postOrder) {        this.postOrder = postOrder;    }
    public String getPicture() {        return picture;    }
    public void setPicture(String picture) {        this.picture = picture;    }
    public double getLatitude() {        return latitude;    }
    public void setLatitude(double latitude) {        this.latitude = latitude;    }
    public double getLongitude() {        return longitude;    }
    public void setLongitude(double longitude) {        this.longitude = longitude;    }
    public String getInfo() {        return info;    }
    public void setInfo(String info) {        this.info = info;    }
    public String getCategory() {        return category;    }
    public void setCategory(String category) {        this.category = category;    }
    public String getAddress() {        return address;    }
    public void setAddress(String address) {        this.address = address;    }
}
