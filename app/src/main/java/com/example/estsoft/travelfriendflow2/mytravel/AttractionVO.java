package com.example.estsoft.travelfriendflow2.mytravel;

/**
 * Created by estsoft on 2016-08-05.
 */
public class AttractionVO {
    int no;
    String location;

    public AttractionVO(){    }

    public AttractionVO(int no, String location) {
        this.no = no;
        this.location = location;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "AttractionVO{" +
                "no=" + no +
                ", location='" + location + '\'' +
                '}';
    }
}
