package com.example.estsoft.travelfriendflow2.mytravel;

/**
 * Created by est on 2016-08-11.
 * MyTravelListActivity, LookAroundActivity에서 사용
 */

public class Travel{
    String title = "";
    String txt_creationDate;
    String planTime;
    String planSeason;
    int background;
    int schNo;

    public Travel(String title, String txt_creationDate, String planTime, String planSeason,int background){
        this.title = title;
        this.txt_creationDate = txt_creationDate;
        this.planTime = planTime;
        this.planSeason = planSeason;
        this.background = background;
    }

    public Travel(){}

    public String getTitle() {  return title;  }
    public void setTitle(String title) { this.title = title;  }
    public String getTxt_creationDate() { return txt_creationDate; }
    public void setTxt_creationDate(String txt_creationDate) {  this.txt_creationDate = txt_creationDate;  }
    public String getPlanTime() {   return planTime;  }
    public void setPlanTime(String planTime) {  this.planTime = planTime;  }
    public String getPlanSeason() {   return planSeason;  }
    public void setPlanSeason(String planSeason) {   this.planSeason = planSeason;   }
    public int getBackground() {    return background;   }
    public void setBackground(int background) {  this.background = background;   }
    public int getSchNo() {   return schNo;  }
    public void setSchNo(int schNo) {     this.schNo = schNo;  }
}