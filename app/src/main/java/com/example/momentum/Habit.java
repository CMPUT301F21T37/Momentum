package com.example.momentum;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Samtarkras
 * @version 1.4
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
   private ArrayList<?> weekly_frequency;
   /**
    * this var contains the habits privacy
    * this var is of type {@link Boolean}
    */
   private Boolean habit_private;
   /**
    * this var contains the habit reason
    * this var is of type {@link String}
    */
   private String reason;
   /**
    * this var contains the habit title
    * this var is of type {@link String}
    */
   private String title;
   /**
    * this var contains the order of the habit
    * this var is of type {@link Integer}
    */
   private Integer order;

   public Habit(String t, String r, Date d, Boolean hp, ArrayList<?> wf, Integer o){
      this.title = t;
      this.reason = r;
      this.date = d;
      this.habit_private = hp;
      this.weekly_frequency = wf;
      this.order = o;
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
   public ArrayList<?> getWeekly_frequency() {
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
   public void setWeekly_frequency(ArrayList<?> weekly_frequency) {
      this.weekly_frequency = weekly_frequency;
   }

   public Integer getOrder() {
      return order;
   }
   public void setOrder(Integer order) {
      this.order = order;
   }
}
