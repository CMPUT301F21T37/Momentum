package com.example.momentum;

import java.util.Date;

/**
 * @author Samtarkras
 * @version 1.3
 */
public class Habit {
   /**
    * this var contains the habit Title
    * this var is of type {@link String}
    */
   private String title;
   /**
    * this var contains the habit Reason
    * this var is of type {@link String}
    */
   private String reason;
   /**
    * this var contains the habit Date
    * this var is of type {@link Date}
    */
   private Date date;
   /**
    * this var contains the habits privacy
    * this var is of type {@link Boolean}
    */
   private Boolean habit_private;
   /**
    * this var contains the weekly frequency of the habit
    * this var is of type {@link Boolean[]}
    */
   private Boolean[] weekly_frequency;


   public Habit(String t, String r, Date d, Boolean hp, Boolean[] wf){
      this.title = t;
      this.reason = r;
      this.date = d;
      this.habit_private = hp;
      this.weekly_frequency = wf;
   }

   public String getTitle() {
      return title;
   }
   public String getReason() {
      return reason;
   }
   public Date getDate() {
      return date;
   }
   public Boolean[] getWeekly_frequency() {
      return weekly_frequency;
   }
   public Boolean isPrivate_account() {
      return habit_private;
   }

   public void setTitle(String title) {
      this.title = title;
   }
   public void setReason(String reason) {
      this.reason = reason;
   }
   public void setDate(Date date) {
      this.date = date;
   }
   public void setPrivacy(Boolean habit_private) {
      this.habit_private = habit_private;
   }
   public void setWeekly_frequency(Boolean[] weekly_frequency) {
      this.weekly_frequency = weekly_frequency;
   }
   public String toString(){
      String hp_string = "public";
      String m = "";
      String t = "";
      String w = "";
      String th = "";
      String f = "";
      String s = "";
      String su = "";
      if (habit_private){
         hp_string = "private";
      }
      if(weekly_frequency[0]){
         m = " Mon ";
      }
      if(weekly_frequency[1]){
         t = " Tue ";
      }
      if(weekly_frequency[2]){
         w = " Wed ";
      }
      if(weekly_frequency[3]){
         th = " Thu ";
      }
      if(weekly_frequency[4]){
         f = " Fri ";
      }
      if(weekly_frequency[5]){
         s = " Sat ";
      }
      if(weekly_frequency[6]){
         su = " Sun ";
      }

      return title + " -- reason: " + reason + " -- date: " + date + " -- privacy: " + hp_string + "-- day frequency: " + m +t + w +th +f +s +su;
   }
}
