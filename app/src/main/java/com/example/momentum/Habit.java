package com.example.momentum;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Samtarkras
 * @version 1.3
 */
public class Habit {
   /**
    * this var contains the habit Date
    * this var is of type {@link Date}
    */
   private Date date;
   /**
    * this var contains the weekly frequency of the habit
    * this var is of type {@link Boolean[]}
    */
   private ArrayList<String> weekly_frequency;
   /**
    * this var contains the habits privacy
    * this var is of type {@link Boolean}
    */
   private Boolean habit_private;
   /**
    * this var contains the habit Title
    * this var is of type {@link String}
    */
   private String reason;

   public Habit(String r, Date d, Boolean hp, ArrayList<String> wf){
      this.reason = r;
      this.date = d;
      this.habit_private = hp;
      this.weekly_frequency = wf;
   }

   public String getReason() {
      return reason;
   }
   public Date getDate() {
      return date;
   }
   public ArrayList<String> getWeekly_frequency() {
      return weekly_frequency;
   }
   public Boolean isPrivate_account() {
      return habit_private;
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
   public void setWeekly_frequency(ArrayList<String> weekly_frequency) {
      this.weekly_frequency = weekly_frequency;
   }
   public String toString(){
      String hp_string = "public";
      if (habit_private){
         hp_string = "private";
      }
      return " -- reason: " + reason + " -- date: " + date + " -- privacy: " + hp_string + "-- day frequency: " + weekly_frequency;
   }
}
