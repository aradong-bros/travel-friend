package com.example.estsoft.travelfriendflow2.mytravel;

/**
 * Created by estsoft on 2016-08-05.
 */
public class AttractionVo {
    int no;
    String location;

    public AttractionVo(){    }

    public AttractionVo(int no, String location) {
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
        return "AttractionVo{" +
                "no=" + no +
                ", location='" + location + '\'' +
                '}';
    }
}
