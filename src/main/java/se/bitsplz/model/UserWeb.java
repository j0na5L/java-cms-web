package se.bitsplz.model;

import se.bitsplz.cms.model.UserData.UserStatus;


public class UserWeb{
   
   private final Long id;
   private final int userId;
   private final String username;
   private final String firstName;
   private final String lastName;
   private final UserStatus userStatus;
   
   
   public UserWeb(Long id, int userId, String username, String firstName, String lastName, UserStatus userStatus){
      this.id = id;
      this.userId = userId;
      this.username = username;
      this.firstName = firstName;
      this.lastName = lastName;
      this.userStatus = userStatus;
   }
   
   
   public Long getId(){
      return this.id;
   }
   
   
   public int getUserId(){
      return this.userId;
   }
   
   
   public String getUsername(){
      return this.username;
   }
   
   
   public String getFirstName(){
      return this.firstName;
   }
   
   
   public String getLastName(){
      return this.lastName;
   }
   
   
   public UserStatus getUserStatus(){
      return this.userStatus;
   }
   
   
   @Override
   public String toString(){
      return "WebUser{" + "id=" + this.id + ", firstName='" + this.firstName + '\'' + ", lastName='" + this.lastName + '\'' + ", username='" + this.username + '\'' + ", userId='" + this.userId + '\'' + ", userStatus='" + this.userStatus + '\'' + '}';
   }
}
