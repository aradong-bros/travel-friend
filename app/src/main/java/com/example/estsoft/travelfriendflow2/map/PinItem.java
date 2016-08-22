package com.example.estsoft.travelfriendflow2.map;

/**
 * Created by estsoft on 2016-07-29.
 */
public class PinItem {
    public String no;       /*해당 지역 NO (PK)*/
    public String title;
    public String picture;
    public double latitude;
    public double longitude;
    public String info;
    public String category;
    public String address;
    public Integer order;
    public String status;

    public Integer getOrder() {        return order;    }
    public void setOrder(Integer order) {        this.order = order;    }
    public double getLongitude() {        return longitude;    }
    public void setLongitude(double longitude) {        this.longitude = longitude;    }
    public double getLatitude() {        return latitude;    }
    public void setLatitude(double latitude) {        this.latitude = latitude;    }
    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }
    public String getNo() {        return no;    }
    public void setNo(String no) {        this.no = no;    }
    public String getStatus() {        return status;    }
    public void setStatus(String status) {        this.status = status;    }
}
